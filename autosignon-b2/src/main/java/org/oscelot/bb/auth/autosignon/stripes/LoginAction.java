/*
 * Copyright (c) 2014 Swinburne University of Technology
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.oscelot.bb.auth.autosignon.stripes;

import blackboard.data.course.Course;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.user.UserDbLoader;
import blackboard.platform.authentication.*;
import blackboard.platform.authentication.log.AuthenticationLogger;
import blackboard.platform.session.BbSession;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;
import org.oscelot.bb.auth.autosignon.api.MacAlgorithm;
import org.oscelot.bb.auth.autosignon.api.SecurityUtil;
import org.oscelot.bb.auth.autosignon.provider.AutosignonProviderSettings;
import org.oscelot.bb.auth.autosignon.service.AuthProviderService;
import org.oscelot.bb.auth.autosignon.service.AutosignonSettingsService;
import org.oscelot.bb.stripes.BlackboardActionBeanContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wfuller
 * Date: 7/01/14
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */
@UrlBinding("/service/login/{apId}")
public class LoginAction implements ActionBean {

  final Logger logger = LoggerFactory.getLogger(LoginAction.class);
  final static String DEBUG_PAGE = "/WEB-INF/jsp/debuginfo.jsp";

  @Validate(required = true)
  private String apId;
  private ArrayList<String> debugMessages;
  private boolean debugMode = false;


  private BlackboardActionBeanContext context;
  private CourseDbLoader courseDbLoader;
  private UserDbLoader userDbLoader;
  private AuthenticationLogger authLogger;
  private AuthenticationManager authManager;
  private AuthProviderService apService;
  private SessionManager sessionManager;
  private AutosignonSettingsService settingsService;
  private SecurityUtil securityUtil;


  /**
   * @return a resolution to the appropriate page.
   */
  @DefaultHandler
  public Resolution performLogin() throws PersistenceException, InvalidKeyException, NoSuchAlgorithmException {
    debugMessages = new ArrayList<String>();
    logger.debug("Validating Autosignon login attempt");

    if (apId == null || apId.isEmpty() || !apId.matches("_\\d+_1")) {
      logger.info("Invalid or missing Auth Provider Id (" + apId + ")");
      return new ForwardResolution("/WEB-INF/jsp/autherror.jsp");
    }

    logger.debug("Loading Authentication Provider for ID: " + apId);
    AuthenticationProvider ap = apService.loadAuthProvider(apId);

    if (ap == null) {
      logger.info("No Auth Provider found for ID: " + apId);
      return new ForwardResolution("/WEB-INF/jsp/autherror.jsp");
    }
    addDebugMessage("Auth Provider: "+ap.getName());

    AutosignonProviderSettings settings = settingsService.retrieveSettings(apId);
    debugMode = settings.isDebug();
    logger.debug("Debug Mode is: "+debugMode);
    Map<String, String> parameters = unpackParameters(context.getRequest().getParameterMap());

    if (!requestParametersAreValid(settings, parameters)) {
      addDebugMessage("Missing required parameters. Make sure you're including all of, userId, timestamp, "+settings.getExtraParameters());
      return new ForwardResolution("/WEB-INF/jsp/autherror.jsp");
    }


    int timestampDelta = settings.getTimestampDelta();
    String secret = settings.getSharedSecret();

    String courseId = parameters.get(settings.getCourseIdParamName());
    String userId = parameters.get(settings.getUserIdParamName());
    String mac = parameters.get(settings.getMacParamName());
    String forward = parameters.get(settings.getForwardParamName());
    String extraParamNames = settings.getExtraParameters();
    long timestamp = (long) 0;
    try {
      timestamp = Long.parseLong(parameters.get(settings.getTimestampParamName()));
    } catch (NumberFormatException e) {
      addDebugMessage("Couldn't parse the timestamp. Are you sure it's in Unix epoch format?");
      logger.error("The timestamp provided was not a number.");
      return new ForwardResolution("/WEB-INF/jsp/autherror.jsp");
    }
    addDebugMessage("courseId: "+courseId+"<br>userId: "+userId+"<br>mac: "+mac+
        "<br>forward: "+forward+"<br>extra params: "+extraParamNames+"<br>timestamp: "+timestamp);

    Map<String, String> parametersToHash = new HashMap<>();
    parametersToHash.put(settings.getUserIdParamName(), parameters.get(settings.getUserIdParamName()));
    parametersToHash.put(settings.getTimestampParamName(), parameters.get(settings.getTimestampParamName()));
    if (extraParamNames != null && !extraParamNames.isEmpty()) {
      for (String paramName : extraParamNames.split(",")) {
        parametersToHash.put(paramName, parameters.get(paramName));
      }
    }
    addDebugMessage("String used to create Hash on server: <b>" + createStringForHashing(parametersToHash) + "&lt;secret key&gt;</b>" +
        " (<i>Parameters must be sorted by parameter name. Don't include &lt;secret key&gt; if you're using a HMAC hash algorithm.</i>)");

    for (String key : parametersToHash.keySet()) {
      logger.debug("Param: " + key + " = \"" + parametersToHash.get(key) + "\"");
    }

    logger.debug("Calculating the Authentication MAC.");
    String calculatedMac = securityUtil.calculateMac(parametersToHash,
        secret, MacAlgorithm.valueOf(settings.getMacAlgorithm()));

    logger.debug("Server MAC: " + calculatedMac + " Client MAC: " + mac);
    addDebugMessage("Server MAC: " + calculatedMac + " <br>Client MAC: " + mac);
    if (!mac.equals(calculatedMac)) {
      logger.debug("Invalid MAC.");
      addDebugMessage("Invalid MAC");

      AuthenticationEvent event = apService.buildAuthenticationEvent(EventType.FailedLogin_Password,
          userId, "Invalid MAC", ap, context.getBlackboardContext());
      authLogger.logAuthenticationEvent(event);
      return new ForwardResolution("/WEB-INF/jsp/autherror.jsp");
    }

    if (!securityUtil.timestampIsValid(timestamp, timestampDelta)) {
      logger.debug("Invalid Timestamp.");
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss.SSS");
      addDebugMessage("Invalid Timestamp. The timestamp you specified is: "+sdf.format(new Date(timestamp))+"<br>" +
          "It should be "+settings.getTimestampDelta()+" milliseconds either side of "+sdf.format(new Date()));

      AuthenticationEvent event = apService.buildAuthenticationEvent(EventType.FailedLogin_Password,
          userId, "Invalid Timestamp", ap, context.getBlackboardContext());
      authLogger.logAuthenticationEvent(event);

      return new ForwardResolution("/WEB-INF/jsp/autherror.jsp");
    }

    logger.debug("Attempting to load user.");
    User user = authManager.findUser(userId, ap);

    if (user == null) {
      addDebugMessage("Couldn't find User: " + userId+". Would redirect to Homepage.");
      if (debugMode) {
        return new ForwardResolution(DEBUG_PAGE);
      }
      return new RedirectResolution("/", false);
    }

    BbSession session = sessionManager.getSession(context.getRequest(), context.getResponse());
    sessionManager.attachUserToSession(user, ap, session, "Logged in from Autosignon." ,context.getRequest());
    addDebugMessage("User <b>"+userId+"</b> Authenticated Successfully.");

    if (courseId == null || courseId.trim().isEmpty()) {
      addDebugMessage("No courseId provided. Would redirect to Homepage.");
      logger.info("No courseId provided. Redirecting to Homepage.");
      if (debugMode) {
        return new ForwardResolution(DEBUG_PAGE);
      }
      return new RedirectResolution("/", false);
    }

    logger.debug("Attempting to load Course.");
    Course course = null;
    try {
      course = courseDbLoader.loadByCourseId(courseId);
    } catch (KeyNotFoundException e) {
      addDebugMessage("No Matching course for provided courseId. Would redirect to Homepage.");
      logger.info("No Matching course. Redirecting to Homepage.");
      if (debugMode) {
        return new ForwardResolution(DEBUG_PAGE);
      }
      return new RedirectResolution("/", false);
    }

    boolean courseAvailable = apService.userCanAccessCourse(course, user);

    if (!courseAvailable) {
      logger.info("User can't access course. Redirecting to Homepage.");
      addDebugMessage("User can't access course. Would redirect to Homepage.");
      if (debugMode) {
        return new ForwardResolution(DEBUG_PAGE);
      }
      return new RedirectResolution("/", false);
    }

    logger.debug("Redirecting to Course.");
    String courseLauncherUrl = "/webapps/portal/frameset.jsp?url=%2Fwebapps%2Fblackboard%2Fexecute%2Flauncher%3Ftype%3DCourse%26id%3D";
    addDebugMessage("User can access course. Would redirect to course at <a href='"+courseLauncherUrl+"'>"+courseLauncherUrl+"</a>.");
    if (debugMode) {
      return new ForwardResolution(DEBUG_PAGE);
    }
    return new RedirectResolution(courseLauncherUrl + course.getId().toExternalString(), false);
  }


  private boolean requestParametersAreValid(AutosignonProviderSettings settings, Map<String, String> parameters) {
    String userId = parameters.get(settings.getUserIdParamName());
    if (userId == null || userId.isEmpty() || userId.equals("null")) {
      addDebugMessage("Missing userId parameter");
      logger.error("Missing UserID for Autosignon!");
      return false;
    }
    String ts = parameters.get(settings.getTimestampParamName());
    if (ts == null || ts.isEmpty() || ts.equals("null")) {
      addDebugMessage("Missing timestamp parameter");
      logger.error("Missing Timestamp for Autosignon!");
      return false;
    }
    String extraParamNames = settings.getExtraParameters();
    if (extraParamNames != null && !extraParamNames.isEmpty()) {
      for (String paramName : extraParamNames.split(",")) {
        String param = parameters.get(settings.getTimestampParamName());
        if (param == null || param.isEmpty() || param.equals("null")) {
          addDebugMessage("Missing "+paramName+" parameter");
          return false;
        }
      }
    }
    return true;
  }


  /**
   * Unpacks the contents of ServletRequest.getParameterMap() into a Map with
   * a simple String value instead of String[].
   *
   * @param requestParams
   * @return
   */
  private Map<String, String> unpackParameters(Map<String, String[]> requestParams) {
    HashMap<String, String> params = new HashMap<>(requestParams.size());
    for (String key : requestParams.keySet()) {
      String[] val = requestParams.get(key);
      if (val != null && val.length > 0) {
        params.put(key, val[0]);
      } else {
        params.put(key, null);
      }
    }
    return params;
  }


  public void setContext(ActionBeanContext context) {
    this.context = (BlackboardActionBeanContext) context;
  }

  public ActionBeanContext getContext() {
    return context;
  }

  public String getApId() {
    return apId;
  }

  public void setApId(String apId) {
    this.apId = apId;
  }

  /**
   * NOTE: There should NOT be a setDebugMode() method, as this could then
   * be set by the user via a URL parameter.
   */
  public boolean getDebugMode() {
    return debugMode;
  }
  public boolean isDebugMode() {
    return debugMode;
  }


  @SpringBean
  public void injectCourseDbLoader(CourseDbLoader courseDbLoader) {
    this.courseDbLoader = courseDbLoader;
  }

  @SpringBean
  public void injectSettingsService(AutosignonSettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @SpringBean
  public void injectSecurityUtil(SecurityUtil securityUtil) {
    this.securityUtil = securityUtil;
  }

  @SpringBean
  public void injectUserDbLoader(UserDbLoader userDbLoader) {
    this.userDbLoader = userDbLoader;
  }

  @SpringBean
  public void injectAuthenticationLogger(AuthenticationLogger authLogger) {
    this.authLogger = authLogger;
  }

  @SpringBean
  public void injectAuthProviderService(AuthProviderService apService) {
    this.apService = apService;
  }

  @SpringBean
  public void injectAuthenticationManager(AuthenticationManager authManager) {
    this.authManager = authManager;
  }

  @SpringBean
  public void injectSessionManager(SessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }


  private void addDebugMessage(String message) {
    if (debugMode) {
      this.debugMessages.add(message);
    }
  }

  public ArrayList<String> getDebugMessages() {
    return debugMessages;
  }


  private String createStringForHashing(Map parameters) {
    List<String> parameterNames = new ArrayList<>(parameters.size());
    parameterNames.addAll(parameters.keySet());
    Collections.sort(parameterNames);

    StringBuilder paramList = new StringBuilder();
    for (String parameterName : parameterNames) {
      paramList.append(parameters.get(parameterName));
    }
    return paramList.toString();
  }

}

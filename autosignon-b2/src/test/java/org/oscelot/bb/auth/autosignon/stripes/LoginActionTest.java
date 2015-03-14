package org.oscelot.bb.auth.autosignon.stripes;

import blackboard.data.course.Course;
import blackboard.data.user.User;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.user.UserDbLoader;
import blackboard.platform.authentication.AuthenticationEvent;
import blackboard.platform.authentication.AuthenticationManager;
import blackboard.platform.authentication.AuthenticationProvider;
import blackboard.platform.authentication.SessionManager;
import blackboard.platform.authentication.log.AuthenticationLogger;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import org.junit.Before;
import org.junit.Test;
import org.oscelot.bb.DummyId;
import org.oscelot.bb.auth.autosignon.api.MacAlgorithm;
import org.oscelot.bb.auth.autosignon.api.SecurityUtil;
import org.oscelot.bb.auth.autosignon.provider.AutosignonProviderSettings;
import org.oscelot.bb.auth.autosignon.service.AuthProviderService;
import org.oscelot.bb.auth.autosignon.service.AutosignonSettingsService;
import org.oscelot.bb.stripes.BlackboardActionBeanContext;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: wfuller
 * Date: 10/01/14
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginActionTest {

  HttpServletRequest request;
  BlackboardActionBeanContext context;
  CourseDbLoader courseDbLoader;
  UserDbLoader userDbLoader;
  AuthenticationLogger authLogger;
  AuthProviderService apService;
  AuthenticationManager authMgr;
  SessionManager sessionManager;
  LoginAction loginAction;
  SecurityUtil securityUtil;
  AutosignonSettingsService settingsService;

  private String secret;
  private MacAlgorithm macAlgorithm;
  private Object extraParameters;
  private long timestampDelta;

  @Before
  public void setup() {
    request = mock(HttpServletRequest.class);
    courseDbLoader = mock(CourseDbLoader.class);
    userDbLoader = mock(UserDbLoader.class);
    authLogger = mock(AuthenticationLogger.class);
    apService = mock(AuthProviderService.class);
    authMgr = mock(AuthenticationManager.class);
    sessionManager = mock(SessionManager.class);
    settingsService = mock(AutosignonSettingsService.class);

    context = mock(BlackboardActionBeanContext.class);
    when(context.getRequest()).thenReturn(request);
    securityUtil = mock(SecurityUtil.class);
    loginAction = new LoginAction();

    loginAction.setContext(context);
    loginAction.injectCourseDbLoader(courseDbLoader);
    loginAction.injectAuthenticationLogger(authLogger);
    loginAction.injectSecurityUtil(securityUtil);
    loginAction.injectUserDbLoader(userDbLoader);
    loginAction.injectSettingsService(settingsService);
    loginAction.injectAuthProviderService(apService);
    loginAction.injectAuthenticationManager(authMgr);
    loginAction.injectSessionManager(sessionManager);
  }

  @Test
  public void performLogin_withValidRequest_redirectsToCourse() throws PersistenceException, InvalidKeyException, NoSuchAlgorithmException {
    String apId = "_1234_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("userId", "wfuller", "courseId", "ITB101", "timestamp", Long.toString(timestamp), "auth", "validMACstring");
    Map<String, String[]> reqParams = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", "", 2500);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    AuthenticationProvider ap = useAuthenticationProvider("_1234_1");

    when(securityUtil.calculateMac(anyMap(), anyString(), any(MacAlgorithm.class))).thenReturn("validMACstring");
    when(securityUtil.timestampIsValid(anyLong(), anyInt())).thenReturn(true);

    Course course = new Course();
    course.setId(new DummyId(Course.DATA_TYPE, "_1_1"));
    User user = new User();
    user.setStudentId("wfuller");
    when(courseDbLoader.loadByCourseId("ITB101")).thenReturn(course);
    when(apService.userCanAccessCourse(any(Course.class), any(User.class))).thenReturn(true);
    when(authMgr.findUser(eq("wfuller"), any(AuthenticationProvider.class))).thenReturn(user);

    loginAction.setApId(apId);
    RedirectResolution resolution = (RedirectResolution) loginAction.performLogin();

    assertTrue("Path should end with the Course pkId", resolution.getPath().endsWith("_1_1"));
    verify(courseDbLoader, atLeastOnce()).loadByCourseId("ITB101");
    verify(authMgr, atLeastOnce()).findUser(eq("wfuller"), any(AuthenticationProvider.class));

    verify(securityUtil, atLeastOnce()).calculateMac(getRequiredParams(settings, simpleParams), "mysecret", MacAlgorithm.HMAC_SHA1);
    verify(securityUtil, atLeastOnce()).timestampIsValid(timestamp, 2500);
  }

  @Test
  public void performValidLogin_withMD5Hash_redirectsToCourse() throws PersistenceException, InvalidKeyException, NoSuchAlgorithmException {
    String apId = "_1234_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("userId", "wfuller", "courseId", "ITB101", "timestamp", Long.toString(timestamp), "auth", "validMACstring");
    Map<String, String[]> reqParams = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.MD5, "mysecret", "", 2500);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    AuthenticationProvider ap = useAuthenticationProvider("_1234_1");

    when(securityUtil.calculateMac(anyMap(), anyString(), any(MacAlgorithm.class))).thenReturn("validMACstring");
    when(securityUtil.timestampIsValid(anyLong(), anyInt())).thenReturn(true);

    Course course = new Course();
    course.setId(new DummyId(Course.DATA_TYPE, "_1_1"));
    User user = new User();
    user.setStudentId("wfuller");
    when(courseDbLoader.loadByCourseId("ITB101")).thenReturn(course);
    when(apService.userCanAccessCourse(any(Course.class), any(User.class))).thenReturn(true);
    when(authMgr.findUser(eq("wfuller"), any(AuthenticationProvider.class))).thenReturn(user);

    loginAction.setApId(apId);
    RedirectResolution resolution = (RedirectResolution) loginAction.performLogin();

    assertTrue("Path should end with the Course pkId", resolution.getPath().endsWith("_1_1"));
    verify(courseDbLoader, atLeastOnce()).loadByCourseId("ITB101");
    verify(authMgr, atLeastOnce()).findUser(eq("wfuller"), any(AuthenticationProvider.class));

    verify(securityUtil, atLeastOnce()).calculateMac(getRequiredParams(settings, simpleParams), "mysecret", MacAlgorithm.MD5);
    verify(securityUtil, atLeastOnce()).timestampIsValid(timestamp, 2500);
  }

  private AuthenticationProvider useAuthenticationProvider(String apIdStr) throws PersistenceException {
    AuthenticationProvider ap = new AuthenticationProvider();
    DummyId id = new DummyId(AuthenticationProvider.DATA_TYPE, apIdStr);
    ap.setId(id);
    when(apService.loadAuthProvider(apIdStr)).thenReturn(ap);
    return ap;
  }


  @Test
  public void performLogin_withInvalidCourseIdAndValidUser_redirectsToRoot() throws InvalidKeyException, NoSuchAlgorithmException, PersistenceException {
    String apId = "_543_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("userId", "wfuller", "courseId", "ITB101", "timestamp", Long.toString(timestamp), "auth", "validMACstring");
    Map<String, String[]> reqParams = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", "", 2600);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    useAuthenticationProvider(apId);


    when(securityUtil.calculateMac(anyMap(), anyString(), any(MacAlgorithm.class))).thenReturn("validMACstring");
    when(securityUtil.timestampIsValid(anyLong(), anyInt())).thenReturn(true);

    Course course = new Course();
    course.setId(new DummyId(Course.DATA_TYPE, "_1_1"));
    User user = new User();
    user.setStudentId("wfuller");
    when(courseDbLoader.loadByCourseId("ITB101")).thenThrow(new KeyNotFoundException("Course Doesn't exist"));
    when(authMgr.findUser(eq("wfuller"), any(AuthenticationProvider.class))).thenReturn(user);

    loginAction.setApId(apId);
    RedirectResolution resolution = (RedirectResolution) loginAction.performLogin();

    assertEquals("/", resolution.getPath());
    verify(courseDbLoader, atLeastOnce()).loadByCourseId("ITB101");
    verify(authMgr, atLeastOnce()).findUser(eq("wfuller"), any(AuthenticationProvider.class));

    verify(securityUtil, atLeastOnce()).calculateMac(getRequiredParams(settings, simpleParams), settings.getSharedSecret(), MacAlgorithm.HMAC_SHA1);
    verify(securityUtil, atLeastOnce()).timestampIsValid(timestamp, 2600);
  }

  @Test
  public void performLogin_withNoCourseIdParamAndValidUser_redirectsToRoot() throws InvalidKeyException, NoSuchAlgorithmException, PersistenceException {
    String apId = "_543_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("userId", "wfuller", "courseId", null, "timestamp", Long.toString(timestamp), "auth", "validMACstring");
    simpleParams.remove("courseId");
    Map<String, String[]> reqParams = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", "", 2600);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    useAuthenticationProvider(apId);


    when(securityUtil.calculateMac(anyMap(), anyString(), any(MacAlgorithm.class))).thenReturn("validMACstring");
    when(securityUtil.timestampIsValid(anyLong(), anyInt())).thenReturn(true);

    Course course = new Course();
    course.setId(new DummyId(Course.DATA_TYPE, "_1_1"));
    User user = new User();
    user.setStudentId("wfuller");
    when(courseDbLoader.loadByCourseId("ITB101")).thenThrow(new KeyNotFoundException("Course Doesn't exist"));
    when(authMgr.findUser(eq("wfuller"), any(AuthenticationProvider.class))).thenReturn(user);

    loginAction.setApId(apId);
    RedirectResolution resolution = (RedirectResolution) loginAction.performLogin();

    assertEquals("/", resolution.getPath());
    verify(courseDbLoader, never()).loadByCourseId(anyString());
    verify(authMgr, atLeastOnce()).findUser(eq("wfuller"), any(AuthenticationProvider.class));

    verify(securityUtil, atLeastOnce()).calculateMac(getRequiredParams(settings, simpleParams), settings.getSharedSecret(), MacAlgorithm.HMAC_SHA1);
    verify(securityUtil, atLeastOnce()).timestampIsValid(timestamp, 2600);
  }

  @Test
  public void performLogin_withNoAuthProviderId_redirectsToErrorPage() throws InvalidKeyException, NoSuchAlgorithmException, PersistenceException {
    String apId = null;
    Map<String, String> parameters = new HashMap<>();
    when(request.getParameterMap()).thenReturn(parameters);

    loginAction.setApId(apId);
    ForwardResolution resolution = (ForwardResolution) loginAction.performLogin();

    assertEquals("/WEB-INF/jsp/autherror.jsp", resolution.getPath());
  }

  @Test
  public void performLogin_withInvalidAuthProviderId_redirectsToErrorPage() throws InvalidKeyException, NoSuchAlgorithmException, PersistenceException {
    String apId = "_543_1";
    Map<String, String> parameters = new HashMap<>();
    when(request.getParameterMap()).thenReturn(parameters);
    when(apService.loadAuthProvider(apId)).thenReturn(null);

    loginAction.setApId(apId);
    ForwardResolution resolution = (ForwardResolution) loginAction.performLogin();

    assertEquals("/WEB-INF/jsp/autherror.jsp", resolution.getPath());
  }

  @Test
  public void performLogin_withInvalidMAC_logsAuthErrorAndRedirectsToErrorPage() throws InvalidKeyException, NoSuchAlgorithmException, PersistenceException {
    String apId = "_543_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("userId", "wfuller", "courseId", "ITB101", "timestamp", Long.toString(timestamp),
        "auth", "invalidMACstring");
    Map<String, String[]> reqParams = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", "", 2600);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    useAuthenticationProvider(apId);

    when(securityUtil.calculateMac(anyMap(), anyString(), any(MacAlgorithm.class))).thenReturn("validMACstring");
    when(securityUtil.timestampIsValid(anyLong(), anyInt())).thenReturn(true);

    loginAction.setApId(apId);
    ForwardResolution resolution = (ForwardResolution) loginAction.performLogin();

    assertEquals("/WEB-INF/jsp/autherror.jsp", resolution.getPath());

    verify(securityUtil, atLeastOnce()).calculateMac(getRequiredParams(settings, simpleParams), settings.getSharedSecret(), MacAlgorithm.HMAC_SHA1);
    verify(authLogger, times(1)).logAuthenticationEvent(any(AuthenticationEvent.class));
    verify(securityUtil, times(0)).timestampIsValid(timestamp, 2600);
  }

  @Test
  public void performLogin_withInvalidTimestamp_logsAuthErrorAndRedirectsToErrorPage() throws InvalidKeyException, NoSuchAlgorithmException, PersistenceException {
    String apId = "_543_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("userId", "wfuller", "courseId", "ITB101", "timestamp", Long.toString(timestamp),
        "auth", "validMACstring");
    Map<String, String[]> reqParams = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", "", 2600);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    useAuthenticationProvider(apId);

    when(securityUtil.calculateMac(anyMap(), anyString(), any(MacAlgorithm.class))).thenReturn("validMACstring");
    when(securityUtil.timestampIsValid(anyLong(), anyInt())).thenReturn(false);

    loginAction.setApId(apId);
    ForwardResolution resolution = (ForwardResolution) loginAction.performLogin();

    assertEquals("/WEB-INF/jsp/autherror.jsp", resolution.getPath());
    verify(securityUtil, atLeastOnce()).calculateMac(getRequiredParams(settings, simpleParams), settings.getSharedSecret(), MacAlgorithm.HMAC_SHA1);
    verify(authLogger, times(1)).logAuthenticationEvent(any(AuthenticationEvent.class));
    verify(securityUtil, times(1)).timestampIsValid(timestamp, 2600);
  }

  @Test
  public void performLogin_withAlternativeParameterNames_completesSuccessfully() throws InvalidKeyException, NoSuchAlgorithmException, PersistenceException {
    String apId = "_1234_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("un", "wfuller", "cid", "ITB101", "ts", Long.toString(timestamp), "mac", "validMACstring");
    Map<String, String[]> reqParams = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", "", 2500);
    useRequestParamNames(settings, "mac", "un", "ts", "f", "cid");
    AuthenticationProvider ap = useAuthenticationProvider("_1234_1");

    when(securityUtil.calculateMac(anyMap(), anyString(), any(MacAlgorithm.class))).thenReturn("validMACstring");
    when(securityUtil.timestampIsValid(anyLong(), anyInt())).thenReturn(true);

    Course course = new Course();
    course.setId(new DummyId(Course.DATA_TYPE, "_1_1"));
    User user = new User();
    user.setStudentId("wfuller");
    when(courseDbLoader.loadByCourseId("ITB101")).thenReturn(course);
    when(apService.userCanAccessCourse(any(Course.class), any(User.class))).thenReturn(true);
    when(authMgr.findUser(eq("wfuller"), any(AuthenticationProvider.class))).thenReturn(user);

    loginAction.setApId(apId);
    RedirectResolution resolution = (RedirectResolution) loginAction.performLogin();

    assertTrue("Path should end with the Course pkId", resolution.getPath().endsWith("_1_1"));
    verify(courseDbLoader, atLeastOnce()).loadByCourseId("ITB101");
    verify(authMgr, atLeastOnce()).findUser(eq("wfuller"), any(AuthenticationProvider.class));

    verify(securityUtil, atLeastOnce()).calculateMac(getRequiredParams(settings, simpleParams), "mysecret", MacAlgorithm.HMAC_SHA1);
    verify(securityUtil, atLeastOnce()).timestampIsValid(timestamp, 2500);
  }

  @Test
  public void performLogin_withUnparseableTimestamp_redirectsToErrorPage() throws PersistenceException, NoSuchAlgorithmException, InvalidKeyException {
    String apId = "_543_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("userId", "wfuller", "courseId", "ITB101", "timestamp", "notATimestamp",
        "auth", "invalidMACstring");
    Map<String, String[]> reqParams = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", "", 2600);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    useAuthenticationProvider(apId);
    loginAction.setApId(apId);
    ForwardResolution resolution = (ForwardResolution) loginAction.performLogin();

    assertEquals("/WEB-INF/jsp/autherror.jsp", resolution.getPath());
  }

  @Test
  public void performLogin_withMissingUserIdParameter_redirectsToErrorPage() throws PersistenceException, NoSuchAlgorithmException, InvalidKeyException {
    String apId = "_543_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("courseId", "ITB101", "timestamp", "131234567",
        "auth", "invalidMACstring");
    Map<String, String[]> parameters = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", "", 2600);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    useAuthenticationProvider(apId);
    loginAction.setApId(apId);

    ForwardResolution resolution = (ForwardResolution) loginAction.performLogin();

    assertEquals("/WEB-INF/jsp/autherror.jsp", resolution.getPath());
    verify(securityUtil, never()).calculateMac(anyMap(),anyString(),any(MacAlgorithm.class));
  }

  @Test
  public void performLogin_withNullParameters_redirectsToErrorPage() throws InvalidKeyException, NoSuchAlgorithmException, PersistenceException {
    String apId = "_543_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("userId", null, "courseId", "ITB101", "timestamp", "131234567",
        "auth", "invalidMACstring");
    Map<String, String[]> parameters = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", "", 2600);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    useAuthenticationProvider(apId);
    loginAction.setApId(apId);

    ForwardResolution resolution = (ForwardResolution) loginAction.performLogin();

    assertEquals("/WEB-INF/jsp/autherror.jsp", resolution.getPath());
    verify(securityUtil, never()).calculateMac(anyMap(),anyString(),any(MacAlgorithm.class));
  }

  @Test
  public void performLogin_withInvalidApId_redirectsToErrorPage() throws InvalidKeyException, NoSuchAlgorithmException, PersistenceException {
    String apId = "_54XXX_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("userId", "wfuller", "courseId", "ITB101", "timestamp", "131234567",
        "auth", "invalidMACstring");
    Map<String, String[]> parameters = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", "", 2600);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    useAuthenticationProvider(apId);
    loginAction.setApId(apId);

    ForwardResolution resolution = (ForwardResolution) loginAction.performLogin();

    assertEquals("/WEB-INF/jsp/autherror.jsp", resolution.getPath());
    verify(securityUtil, never()).calculateMac(anyMap(),anyString(),any(MacAlgorithm.class));
  }


  @Test
  public void performLogin_withExtraMacParameters_completesSuccessfully() throws InvalidKeyException, NoSuchAlgorithmException, PersistenceException {
    String apId = "_1234_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("userId", "wfuller", "courseId", "ITB101", "timestamp", Long.toString(timestamp), "auth", "validMACstring");
    Map<String, String[]> parameters = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", "courseId", 2500);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    AuthenticationProvider ap = useAuthenticationProvider("_1234_1");

    when(securityUtil.calculateMac(anyMap(), anyString(), any(MacAlgorithm.class))).thenReturn("validMACstring");
    when(securityUtil.timestampIsValid(anyLong(), anyInt())).thenReturn(true);

    Course course = new Course();
    course.setId(new DummyId(Course.DATA_TYPE, "_1_1"));
    User user = new User();
    user.setStudentId("wfuller");
    when(courseDbLoader.loadByCourseId("ITB101")).thenReturn(course);
    when(apService.userCanAccessCourse(any(Course.class), any(User.class))).thenReturn(true);
    when(authMgr.findUser(eq("wfuller"), any(AuthenticationProvider.class))).thenReturn(user);

    loginAction.setApId(apId);
    RedirectResolution resolution = (RedirectResolution) loginAction.performLogin();

    assertTrue("Path should end with the Course pkId", resolution.getPath().endsWith("_1_1"));
    verify(courseDbLoader, atLeastOnce()).loadByCourseId("ITB101");
    verify(authMgr, atLeastOnce()).findUser(eq("wfuller"), any(AuthenticationProvider.class));
    Map<String,String> reqParams = getRequiredParams(simpleParams, "userId", "courseId", "timestamp");
    verify(securityUtil, atLeastOnce()).calculateMac(reqParams, "mysecret", MacAlgorithm.HMAC_SHA1);
    verify(securityUtil, atLeastOnce()).timestampIsValid(timestamp, 2500);
  }

  @Test
  public void performLogin_withNullExtraParameters_completesSuccessfully() throws InvalidKeyException, NoSuchAlgorithmException, PersistenceException {
    String apId = "_1234_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("userId", "wfuller", "courseId", "ITB101", "timestamp", Long.toString(timestamp), "auth", "validMACstring");
    Map<String, String[]> parameters = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", null, 2500);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    AuthenticationProvider ap = useAuthenticationProvider("_1234_1");

    when(securityUtil.calculateMac(anyMap(), anyString(), any(MacAlgorithm.class))).thenReturn("validMACstring");
    when(securityUtil.timestampIsValid(anyLong(), anyInt())).thenReturn(true);

    Course course = new Course();
    course.setId(new DummyId(Course.DATA_TYPE, "_1_1"));
    User user = new User();
    user.setStudentId("wfuller");
    when(courseDbLoader.loadByCourseId("ITB101")).thenReturn(course);
    when(apService.userCanAccessCourse(any(Course.class), any(User.class))).thenReturn(true);
    when(authMgr.findUser(eq("wfuller"), any(AuthenticationProvider.class))).thenReturn(user);

    loginAction.setApId(apId);
    RedirectResolution resolution = (RedirectResolution) loginAction.performLogin();

    assertTrue("Path should end with the Course pkId", resolution.getPath().endsWith("_1_1"));
    verify(courseDbLoader, atLeastOnce()).loadByCourseId("ITB101");
    verify(authMgr, atLeastOnce()).findUser(eq("wfuller"), any(AuthenticationProvider.class));
    Map<String,String> reqParams = getRequiredParams(simpleParams, "userId", "timestamp");
    verify(securityUtil, atLeastOnce()).calculateMac(reqParams, "mysecret", MacAlgorithm.HMAC_SHA1);
    verify(securityUtil, atLeastOnce()).timestampIsValid(timestamp, 2500);
  }

  @Test
  public void performLogin_withInvalidUserId_redirectsToRoot() throws InvalidKeyException, NoSuchAlgorithmException, PersistenceException {
    String apId = "_1234_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("userId", "wfuller", "courseId", "ITB101", "timestamp", Long.toString(timestamp), "auth", "validMACstring");
    Map<String, String[]> parameters = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", "courseId", 2500);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    AuthenticationProvider ap = useAuthenticationProvider("_1234_1");

    when(securityUtil.calculateMac(anyMap(), anyString(), any(MacAlgorithm.class))).thenReturn("validMACstring");
    when(securityUtil.timestampIsValid(anyLong(), anyInt())).thenReturn(true);

    Course course = new Course();
    course.setId(new DummyId(Course.DATA_TYPE, "_1_1"));
    when(courseDbLoader.loadByCourseId("ITB101")).thenReturn(course);
    when(apService.userCanAccessCourse(any(Course.class), any(User.class))).thenReturn(true);
    when(authMgr.findUser(eq("wfuller"), any(AuthenticationProvider.class))).thenReturn(null);

    loginAction.setApId(apId);
    RedirectResolution resolution = (RedirectResolution) loginAction.performLogin();

    assertEquals("/", resolution.getPath());
    verify(authMgr, atLeastOnce()).findUser(eq("wfuller"), any(AuthenticationProvider.class));
    Map<String,String> reqParams = getRequiredParams(simpleParams, "userId", "courseId", "timestamp");
    verify(securityUtil, atLeastOnce()).calculateMac(reqParams, "mysecret", MacAlgorithm.HMAC_SHA1);
    verify(securityUtil, atLeastOnce()).timestampIsValid(timestamp, 2500);
  }


  public Map<String, String> getRequiredParams(AutosignonProviderSettings settings, Map<String, String> parameters) {
    Map<String, String> requiredParams = new HashMap<>();
    String extraParamNames = settings.getExtraParameters();
    requiredParams.put(settings.getUserIdParamName(), parameters.get(settings.getUserIdParamName()));
    requiredParams.put(settings.getTimestampParamName(), parameters.get(settings.getTimestampParamName()));
    if (extraParamNames != null && !extraParamNames.isEmpty()) {
      for (String paramName : extraParamNames.split(",")) {
        requiredParams.put(paramName, parameters.get(paramName));
      }
    }
    return requiredParams;
  }

  public Map<String, String> getRequiredParams(Map<String, String> parameters, String... paramNames) {
    Map<String, String> requiredParams = new HashMap<>();
    for (String paramName : paramNames) {
      requiredParams.put(paramName, parameters.get(paramName));
    }
    return requiredParams;
  }


  private void useRequestParamNames(AutosignonProviderSettings settings, String macParamName,
                                    String userIdParamName, String timestampParamName, String
      forwardParamName, String courseIdParamName) {

    settings.setMacParamName(macParamName);
    settings.setUserIdParamName(userIdParamName);
    settings.setTimestampParamName(timestampParamName);
    settings.setForwardParamName(forwardParamName);
    settings.setCourseIdParamName(courseIdParamName);
  }



  @Test
  public void performLogin_withUnavailableCourse_redirectsToRoot() throws InvalidKeyException, NoSuchAlgorithmException, PersistenceException {
    String apId = "_1234_1";
    long timestamp = new Date().getTime() - 2000;
    Map<String, String> simpleParams = mapifyStrings("userId", "wfuller", "courseId", "ITB101", "timestamp", Long.toString(timestamp), "auth", "validMACstring");
    Map<String, String[]> reqParams = useRequestParameters(simpleParams);
    AutosignonProviderSettings settings = useSettings(apId, MacAlgorithm.HMAC_SHA1, "mysecret", "", 2500);
    useRequestParamNames(settings, "auth", "userId", "timestamp", "forward", "courseId");
    AuthenticationProvider ap = useAuthenticationProvider("_1234_1");

    when(securityUtil.calculateMac(anyMap(), anyString(), any(MacAlgorithm.class))).thenReturn("validMACstring");
    when(securityUtil.timestampIsValid(anyLong(), anyInt())).thenReturn(true);

    Course course = new Course();
    course.setId(new DummyId(Course.DATA_TYPE, "_1_1"));
    User user = new User();
    user.setStudentId("wfuller");
    when(courseDbLoader.loadByCourseId("ITB101")).thenReturn(course);
    when(apService.userCanAccessCourse(any(Course.class), any(User.class))).thenReturn(false);
    when(authMgr.findUser(eq("wfuller"), any(AuthenticationProvider.class))).thenReturn(user);

    loginAction.setApId(apId);
    RedirectResolution resolution = (RedirectResolution) loginAction.performLogin();

    assertEquals("/", resolution.getPath());
    verify(courseDbLoader, atLeastOnce()).loadByCourseId("ITB101");
    verify(authMgr, atLeastOnce()).findUser(eq("wfuller"), any(AuthenticationProvider.class));

    verify(securityUtil, atLeastOnce()).calculateMac(getRequiredParams(settings, simpleParams), "mysecret", MacAlgorithm.HMAC_SHA1);
    verify(securityUtil, atLeastOnce()).timestampIsValid(timestamp, 2500);
  }



  private AutosignonProviderSettings useSettings(String apId, MacAlgorithm macAlgorithm, String secret, String extraParameters, int timestampDelta) {
    AutosignonProviderSettings settings = new AutosignonProviderSettings();
    settings.setTimestampDelta(timestampDelta);
    settings.setSharedSecret(secret);
    settings.setExtraParameters(extraParameters);
    settings.setMacAlgorithm(macAlgorithm.name());

    when(settingsService.retrieveSettings(apId)).thenReturn(settings);

    return settings;
  }

  private Map<String, String[]> useRequestParameters(Map<String, String> params) {
    Map parameters = new HashMap<>();
    for (String key : params.keySet()) {
      String[] value = null;
      if (params.get(key) != null) {
        value = new String[1];
        value[0] = params.get(key);
      }
      parameters.put(key, value);
    }
    when(request.getParameterMap()).thenReturn(parameters);

    return parameters;
  }


  private Map<String, String> mapifyStrings(String... paramList) {
    Map parameters = new HashMap<>();
    for (int i = 0; i < paramList.length; i = i + 2) {
      String key = paramList[i];
      String value = paramList[i + 1];
      parameters.put(key, value);
    }
    return parameters;
  }

}

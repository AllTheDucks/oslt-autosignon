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

import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.platform.authentication.AuthenticationProvider;
import blackboard.platform.authentication.AuthenticationProviderHandler;
import blackboard.platform.authentication.AuthenticationProviderManager;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import org.oscelot.bb.auth.autosignon.api.MacAlgorithm;
import org.oscelot.bb.auth.autosignon.api.SecurityUtil;
import org.oscelot.bb.auth.autosignon.provider.AutosignonProviderSettings;
import org.oscelot.bb.auth.autosignon.service.AutosignonSettingsService;
import org.oscelot.bb.auth.autosignon.util.BuildingBlockHelper;
import org.oscelot.bb.stripes.BlackboardActionBeanContext;
import org.oscelot.bb.stripes.EntitlementRestrictions;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: wfuller
 * Date: 7/01/14
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */
@EntitlementRestrictions(entitlements={"system.authentication.manager.MODIFY"}, errorPage="/noaccess.jsp")
@UrlBinding("/service/settings/{apId}")
public class SettingsAction implements ActionBean {

  BlackboardActionBeanContext context;
  private AutosignonSettingsService settingsService;

  private String apId;

  @ValidateNestedProperties({
      @Validate(field = "macAlgorithm", required = true),
      @Validate(field = "sharedSecret", required = true),
      @Validate(field = "extraParameters"),
      @Validate(field = "timestampDelta", required = true),
      @Validate(field = "macParamName", required = true),
      @Validate(field = "userIdParamName", required = true),
      @Validate(field = "timestampParamName", required = true),
      @Validate(field = "forwardParamName", required = true),
      @Validate(field = "courseIdParamName", required = true)
  })
  private AutosignonProviderSettings settings;
  private BuildingBlockHelper helper;


  @Before(stages = LifecycleStage.EventHandling, on = "displaySettings")
  public void setupData() {
    settings = settingsService.retrieveSettings(apId);
  }

  @DontValidate
  @DefaultHandler
  public Resolution displaySettings() {
    return new ForwardResolution("/WEB-INF/jsp/settings.jsp");

  }


  public Resolution saveSettings() {
    settingsService.persistSettings(apId, settings);

    return new RedirectResolution("/webapps/blackboard/execute/authentication/manager?cmd=display", false);
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


  public AutosignonProviderSettings getSettings() {
    return settings;
  }

  public void setSettings(AutosignonProviderSettings settings) {
    this.settings = settings;
  }

  public BuildingBlockHelper getHelper() {
    return helper;
  }

  @SpringBean
  public void injectSettingsService(AutosignonSettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @SpringBean
  public void injectBuildingBlockHelper(BuildingBlockHelper helper) {
    this.helper = helper;
  }

}

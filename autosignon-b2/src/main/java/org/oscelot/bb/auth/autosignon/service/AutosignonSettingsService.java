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

package org.oscelot.bb.auth.autosignon.service;

import blackboard.data.ExtendedData;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.platform.authentication.AuthenticationProvider;
import blackboard.platform.authentication.AuthenticationProviderManager;
import blackboard.platform.authentication.impl.AuthProviderSettingsWrapper;
import org.oscelot.bb.auth.autosignon.provider.AutosignonProviderSettings;

/**
 * Created with IntelliJ IDEA.
 * User: wfuller
 * Date: 14/01/14
 * Time: 11:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class AutosignonSettingsService {

  AuthenticationProviderManager mgr;

  public AutosignonProviderSettings retrieveSettings(String pkId) {
    AuthenticationProviderManager authMgr = getAuthProviderManager();
    Id authProviderId = null;
    try {
      authProviderId = Id.generateId(AuthenticationProvider.DATA_TYPE, pkId);
    } catch (PersistenceException e) {
      throw new RuntimeException("Error generating key for AuthProvider", e);
    }
    AuthenticationProvider ap = authMgr.loadAuthenticationProvider(authProviderId);
    SettingsWrapperWrapper settingsWrapper = new SettingsWrapperWrapper(ap);

    AutosignonProviderSettings settings = new AutosignonProviderSettings();

    ExtendedData settingsData = ap.getExtendedData();

    return unpackSettings(settingsWrapper);
  }

  public void persistSettings(String pkId, AutosignonProviderSettings settings) {
    AuthenticationProviderManager authMgr = getAuthProviderManager();
    Id authProviderId = null;
    try {
      authProviderId = Id.generateId(AuthenticationProvider.DATA_TYPE, pkId);
    } catch (PersistenceException e) {
      throw new RuntimeException("Error generating key for AuthProvider", e);
    }
    AuthenticationProvider ap = authMgr.loadAuthenticationProvider(authProviderId);

    SettingsWrapperWrapper settingsWrapper = new SettingsWrapperWrapper(ap);

    packSettings(settings, settingsWrapper);

    authMgr.persist(ap);
  }

  private void packSettings(AutosignonProviderSettings settings, SettingsWrapperWrapper settingsWrapper) {

    settingsWrapper.setString(AutosignonProviderSettings.MAC_ALGORITHM_KEY, settings.getMacAlgorithm());
    settingsWrapper.setString(AutosignonProviderSettings.SHARED_SECRET_KEY, settings.getSharedSecret());
    String extraParams = settings.getExtraParameters() == null ? "" : settings.getExtraParameters();
    settingsWrapper.setString(AutosignonProviderSettings.EXTRA_PARAMS_KEY, extraParams);
    settingsWrapper.setString(AutosignonProviderSettings.TIMESTAMP_DELTA_KEY, Integer.toString(settings.getTimestampDelta()));

    settingsWrapper.setString(AutosignonProviderSettings.MAC_PARAM_KEY, settings.getMacParamName());
    settingsWrapper.setString(AutosignonProviderSettings.USERID_PARAM_KEY, settings.getUserIdParamName());
    settingsWrapper.setString(AutosignonProviderSettings.TIMESTAMP_PARAM_KEY, settings.getTimestampParamName());
    settingsWrapper.setString(AutosignonProviderSettings.FORWARD_PARAM_KEY, settings.getForwardParamName());
    settingsWrapper.setString(AutosignonProviderSettings.COURSEID_PARAM_KEY, settings.getCourseIdParamName());
    settingsWrapper.setString(AutosignonProviderSettings.DEBUG_PARAM_KEY, Boolean.toString(settings.isDebug()));

  }


  private AutosignonProviderSettings unpackSettings(SettingsWrapperWrapper settingsWrapper) {
    AutosignonProviderSettings settings = new AutosignonProviderSettings();

    settings.setMacAlgorithm(settingsWrapper.getString(AutosignonProviderSettings.MAC_ALGORITHM_KEY, ""));
    settings.setExtraParameters(settingsWrapper.getString(AutosignonProviderSettings.EXTRA_PARAMS_KEY, ""));
    settings.setSharedSecret(settingsWrapper.getString(AutosignonProviderSettings.SHARED_SECRET_KEY, ""));
    settings.setTimestampDelta(Integer.parseInt(settingsWrapper.getString(AutosignonProviderSettings.TIMESTAMP_DELTA_KEY, "0")));

    settings.setMacParamName(settingsWrapper.getString(AutosignonProviderSettings.MAC_PARAM_KEY, "auth"));
    settings.setUserIdParamName(settingsWrapper.getString(AutosignonProviderSettings.USERID_PARAM_KEY, "userId"));
    settings.setTimestampParamName(settingsWrapper.getString(AutosignonProviderSettings.TIMESTAMP_PARAM_KEY, "timestamp"));
    settings.setForwardParamName(settingsWrapper.getString(AutosignonProviderSettings.FORWARD_PARAM_KEY, "forward"));
    settings.setCourseIdParamName(settingsWrapper.getString(AutosignonProviderSettings.COURSEID_PARAM_KEY, "courseId"));

    settings.setDebug(Boolean.parseBoolean(settingsWrapper.getString(AutosignonProviderSettings.DEBUG_PARAM_KEY, "false")));
    return settings;
  }

  private AuthenticationProviderManager getAuthProviderManager() {
    if (mgr == null) {
      mgr = AuthenticationProviderManager.Factory.getInstance();
    }
    return mgr;
  }


}

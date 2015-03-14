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

package org.oscelot.bb.auth.autosignon.provider;

import blackboard.platform.authentication.AbstractAuthenticationProviderHandler;
import blackboard.platform.authentication.AuthenticationProvider;
import blackboard.platform.authentication.AuthenticationProviderHandler;
import blackboard.platform.plugin.PlugInUtil;
import org.oscelot.bb.auth.autosignon.util.B2LocalizationUtil;
import org.oscelot.bb.auth.autosignon.util.BuildingBlockHelper;
import org.oscelot.bb.auth.autosignon.util.BuildingBlockHelperImpl;

/**
 * Created with IntelliJ IDEA.
 * User: wfuller
 * Date: 7/01/14
 * Time: 5:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class AutosignonAuthenticationProviderHandler extends AbstractAuthenticationProviderHandler {

  public static final String EXTENSION_ID = "org.oscelot.autosignonAuthProvider";
  public static final String LOGIN_URL = "service/login/";
  public static final String SETTINGS_URL = "service/settings/";

  @Override
  public String getExtensionId() {
    return EXTENSION_ID;
  }


  @Override
  public AuthenticationMode getAuthenticationMode() {
    return AuthenticationMode.REDIRECT;
  }

  @Override
  public boolean hasSettingsUrl() {
    return true;
  }


  @Override
  public String getLoginUrl(AuthenticationProvider ap, String returnUrl) {
    return buildUrl(LOGIN_URL+ap.getId().getExternalString());
  }

  @Override
  public String getLogoutUrl(AuthenticationProvider ap, String returnUrl) {
    return null;
  }


  @Override
  public String getSettingsUrl(AuthenticationProvider ap, boolean isEdit) {
    return buildUrl(SETTINGS_URL+ap.getId().getExternalString());
  }




  @Override
  public String getDisplayName() {
    return B2LocalizationUtil.get().getString("autosignon.provider.display.name");
  }

  private String buildUrl(String path) {
    return PlugInUtil.getUri(BuildingBlockHelperImpl.VENDOR_ID, BuildingBlockHelperImpl.HANDLE, path);
  }

}

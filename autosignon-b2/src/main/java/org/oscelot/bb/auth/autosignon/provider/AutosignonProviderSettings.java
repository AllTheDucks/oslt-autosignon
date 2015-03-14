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

import blackboard.platform.authentication.AuthenticationProvider;
import blackboard.platform.authentication.impl.AuthProviderSettingsWrapper;
import org.oscelot.bb.auth.autosignon.api.MacAlgorithm;

/**
 * Simple wrapper around an AuthenticationProvider's ExtendedData that handles
 * Facebook specific settings.
 *
 * @author Wiley Fuller
 */
public class AutosignonProviderSettings {

  public static final String MAC_ALGORITHM_KEY = "autosignon-algorithm";
  public static final String SHARED_SECRET_KEY = "autosignon-shared-secret";
  public static final String EXTRA_PARAMS_KEY = "autosignon-extra-params";
  public static final String TIMESTAMP_DELTA_KEY = "autosignon-timestamp-delta";
  public static final String MAC_PARAM_KEY = "autosignon-mac-param";
  public static final String USERID_PARAM_KEY = "autosignon-userid-param";
  public static final String TIMESTAMP_PARAM_KEY = "autosignon-timestamp-param";
  public static final String FORWARD_PARAM_KEY = "autosignon-forward-param";
  public static final String COURSEID_PARAM_KEY = "autosignon-courseid-param";
  public static final String DEBUG_PARAM_KEY = "autosignon-debug-param";

  private String macAlgorithm;
  private String sharedSecret;
  private String extraParameters;
  private int timestampDelta;
  private boolean debug;

  private String macParamName;
  private String userIdParamName;
  private String timestampParamName;
  private String forwardParamName;
  private String courseIdParamName;

  public String getMacAlgorithm() {
    return macAlgorithm;
  }

  public void setMacAlgorithm(String macAlgorithm) {
    this.macAlgorithm = macAlgorithm;
  }

  public String getSharedSecret() {
    return sharedSecret;
  }

  public void setSharedSecret(String sharedSecret) {
    this.sharedSecret = sharedSecret;
  }

  public String getExtraParameters() {
    return extraParameters;
  }

  public void setExtraParameters(String extraParameters) {
    this.extraParameters = extraParameters;
  }

  public int getTimestampDelta() {
    return timestampDelta;
  }

  public void setTimestampDelta(int timestampDelta) {
    this.timestampDelta = timestampDelta;
  }

  public String getMacParamName() {
    return macParamName;
  }

  public void setMacParamName(String macParamName) {
    this.macParamName = macParamName;
  }

  public String getUserIdParamName() {
    return userIdParamName;
  }

  public void setUserIdParamName(String userIdParamName) {
    this.userIdParamName = userIdParamName;
  }

  public String getTimestampParamName() {
    return timestampParamName;
  }

  public void setTimestampParamName(String timestampParamName) {
    this.timestampParamName = timestampParamName;
  }

  public String getForwardParamName() {
    return forwardParamName;
  }

  public void setForwardParamName(String forwardParamName) {
    this.forwardParamName = forwardParamName;
  }

  public String getCourseIdParamName() {
    return courseIdParamName;
  }

  public void setCourseIdParamName(String courseIdParamName) {
    this.courseIdParamName = courseIdParamName;
  }

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }
}


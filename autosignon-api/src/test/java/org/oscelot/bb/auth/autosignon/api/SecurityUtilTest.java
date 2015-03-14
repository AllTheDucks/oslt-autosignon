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

package org.oscelot.bb.auth.autosignon.api;

import org.junit.Before;
import org.junit.Test;
import org.oscelot.bb.auth.autosignon.api.MacAlgorithm;
import org.oscelot.bb.auth.autosignon.api.SecurityUtil;

import static org.junit.Assert.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: wfuller
 * Date: 10/01/14
 * Time: 10:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class SecurityUtilTest {


  private SecurityUtil securityUtil;

  @Before
  public void setup() {
    securityUtil = new SecurityUtil();
  }

  @Test
  public void calculateMD5Mac_withUnorderedParameters_returnsAValidMAC() throws NoSuchAlgorithmException, InvalidKeyException {
    Map<String, String> parameters = new HashMap<>();
    String secret = "blackboard";

    parameters.put("timestamp", "1268769454017");
    parameters.put("courseId", "TC-101");
    parameters.put("userId", "test01");

    String mac = securityUtil.calculateMac(parameters, secret, MacAlgorithm.MD5);

    assertEquals("8c4956a842e183659ea96478ba7671e2", mac);
  }

  @Test
  public void calculateSHA1Mac_withUnorderedParameters_returnsAValidMAC() throws NoSuchAlgorithmException, InvalidKeyException {
    Map<String, String> parameters = new HashMap<>();
    String secret = "blackboard";

    parameters.put("timestamp", "1268769454017");
    parameters.put("courseId", "TC-101");
    parameters.put("userId", "test01");

    String mac = securityUtil.calculateMac(parameters, secret, MacAlgorithm.SHA1);

    assertEquals("ae3ec50d0113ba7de4f17580115d825ebf76fd79", mac);
  }

  @Test
  public void calculateSHA1HMAC_withUnorderedParameters_returnsAValidMAC() throws NoSuchAlgorithmException, InvalidKeyException {
    Map<String, String> parameters = new HashMap<>();
    String secret = "blackboard";

    parameters.put("timestamp", "1268769454017");
    parameters.put("courseId", "TC-101");
    parameters.put("userId", "test01");

    String mac = securityUtil.calculateMac(parameters, secret, MacAlgorithm.HMAC_SHA1);

    assertEquals("6d00fd09160cea5ab27116e512554cf6c99be9c4", mac);
  }

  @Test
  public void calculateMD5HMAC_withUnorderedParameters_returnsAValidMAC() throws NoSuchAlgorithmException, InvalidKeyException {
    Map<String, String> parameters = new HashMap<>();
    String secret = "blackboard";

    parameters.put("timestamp", "1268769454017");
    parameters.put("courseId", "TC-101");
    parameters.put("userId", "test01");

    String mac = securityUtil.calculateMac(parameters, secret, MacAlgorithm.HMAC_MD5);

    assertEquals("141fad94c63698126746f9cf1a9e1d8a", mac);
  }


  @Test
  public void timestampIsValid_withTimestampAfterDelta_returnsFalse() {
    long timestamp = new Date().getTime() + 2500;

    boolean result = securityUtil.timestampIsValid(timestamp, 2000);

    assertEquals(false, result);
  }


  @Test
  public void timestampIsValid_withTimestampBeforeDelta_returnsFalse() {
    long timestamp = new Date().getTime() - 2500;

    boolean result = securityUtil.timestampIsValid(timestamp, 2000);

    assertEquals(false, result);
  }

  @Test
  public void timestampIsValid_withTimestampInsideDelta_returnsTrue() {
    long timestamp = new Date().getTime();

    boolean result = securityUtil.timestampIsValid(timestamp, 2000);

    assertEquals(true, result);
  }

  @Test
  public void timestampIsValid_withTimestampInSeconds_returnsTrue() {
    long timestamp = new Date().getTime() / 1000;

    boolean result = securityUtil.timestampIsValid(timestamp, 2000);

    assertEquals(true, result);
  }

  @Test
  public void timestampIsValid_withTimestampInHundredthsOfSeconds_returnsFalse() {
    long timestamp = new Date().getTime() / 10;

    boolean result = securityUtil.timestampIsValid(timestamp, 2000);

    assertEquals(false, result);
  }
  @Test
  public void timestampIsValid_withTimestampInTenthsOfSeconds_returnsFalse() {
    long timestamp = new Date().getTime() / 100;

    boolean result = securityUtil.timestampIsValid(timestamp, 2000);

    assertEquals(false, result);
  }
}

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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wfuller
 * Date: 10/01/14
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class SecurityUtil {

  public String calculateMac(Map<String, String> parameters, String secret, MacAlgorithm algorithm) throws NoSuchAlgorithmException, InvalidKeyException {

    List<String> parameterNames = new ArrayList<>(parameters.size());
    parameterNames.addAll(parameters.keySet());
    Collections.sort(parameterNames);

    StringBuilder paramList = new StringBuilder();
    for (String parameterName : parameterNames) {
      paramList.append(parameters.get(parameterName));
    }

    byte[] hashBytes = null;

    MessageDigest md = null;
    Mac mac = null;
    byte[] keyBytes = null;
    SecretKeySpec signingKey = null;
    switch (algorithm) {
      case MD5:
        paramList.append(secret);
        md = MessageDigest.getInstance("MD5");
        hashBytes = md.digest((paramList.toString()).getBytes());
        break;
      case SHA1:
        paramList.append(secret);
        md = MessageDigest.getInstance("SHA1");
        hashBytes = md.digest((paramList.toString()).getBytes());
        break;
      case HMAC_SHA1:
        mac = Mac.getInstance("HmacSHA1");
        keyBytes = secret.getBytes();
        signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
        mac.init(signingKey);
        hashBytes = mac.doFinal((paramList.toString()).getBytes());
        break;
      case HMAC_MD5:
        mac = Mac.getInstance("HmacMD5");
        keyBytes = secret.getBytes();
        signingKey = new SecretKeySpec(keyBytes, "HmacMD5");
        mac.init(signingKey);
        hashBytes = mac.doFinal((paramList.toString()).getBytes());
        break;
      default:
        throw new UnsupportedOperationException("This algorithm is not supported");
    }

    // convert to hex
    StringBuilder macBuilder = new StringBuilder();

    for (int k = 0; k < hashBytes.length; k++) {
      String hexByte = Integer.toHexString(hashBytes[k] < 0 ? hashBytes[k] + 256 : hashBytes[k]);
      if (hexByte.length() == 1) {
        macBuilder.append("0");
      }
      macBuilder.append(hexByte);
    }
    return macBuilder.toString();
  }


  public boolean timestampIsValid(long timestamp, int allowedDelta) {
    final long currentTimestamp = Calendar.getInstance().getTimeInMillis();
    if ((String.valueOf(currentTimestamp).length() - String.valueOf(timestamp).length()) >= 2) {
      timestamp = timestamp * 1000L;
    }

    // allow for either direction to include some room for subtle clock differences
    final long delta = Math.abs(currentTimestamp - timestamp);

    return delta < allowedDelta;
  }

}

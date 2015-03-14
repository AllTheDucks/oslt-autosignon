package org.oscelot.bb;

import org.oscelot.bb.auth.autosignon.api.MacAlgorithm;
import org.oscelot.bb.auth.autosignon.api.SecurityUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: wfuller
 * Date: 23/04/14
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class LoginUrlGenerator {

  public static void main(String[] args) {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String url = null;
    String secret = null;
    String userId = null;
    String courseId = null;
    String timestamp = null;
    try {
      System.out.print("Enter Base URL: ");
      url = br.readLine();
      System.out.print("Enter Secret key: ");
      secret = br.readLine();
      System.out.print("Enter username: ");
      userId = br.readLine();
      System.out.print("Enter course Id (<enter> to skip): ");
      courseId = br.readLine();
      System.out.print("Enter timestmap (<enter> for current time): ");
      timestamp = br.readLine();
    } catch (IOException e) {
      System.err.println("Error reading from STDIN");
    }
    System.out.println("You entered: '"+url+"'");
    SecurityUtil util = new SecurityUtil();
    if (timestamp == null || timestamp.trim().isEmpty()) {
      timestamp = Long.toString(new Date().getTime());
    }

    Map<String, String> params = new HashMap<>();
    params.put("timestamp", timestamp);
    params.put("userId", userId);
    if (courseId != null && !courseId.trim().isEmpty()) {
      params.put("courseId", courseId);
    }
    String mac = null;
    try {
      mac = util.calculateMac(params, secret, MacAlgorithm.MD5);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    System.out.println(String.format("%s?timestamp=%s&userId=%s&courseId=%s&auth=%s",
        url, timestamp, userId, courseId, mac));
  }
}

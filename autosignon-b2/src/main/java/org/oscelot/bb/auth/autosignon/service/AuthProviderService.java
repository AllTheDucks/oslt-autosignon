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

import blackboard.data.course.Course;
import blackboard.data.course.CourseMembership;
import blackboard.data.course.CourseUtil;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseMembershipDbLoader;
import blackboard.platform.authentication.AuthenticationEvent;
import blackboard.platform.authentication.AuthenticationProvider;
import blackboard.platform.authentication.AuthenticationProviderManager;
import blackboard.platform.authentication.EventType;
import blackboard.platform.context.Context;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: wfuller
 * Date: 16/01/14
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthProviderService {

  private AuthenticationProviderManager mgr;
  private CourseMembershipDbLoader courseMembershipDbLoader;

  public AuthenticationProvider loadAuthProvider(String pkStr) throws PersistenceException {
    Id apId = Id.generateId(AuthenticationProvider.DATA_TYPE, pkStr);
    AuthenticationProvider ap = mgr.loadAuthenticationProvider(apId);
    return ap;
  }

  /**
   * This method is a thin wrapper around Authentication Event so that we can test
   * loginAction more easily.
   *
   * @param type
   * @param userId
   * @param message
   * @param ap
   * @param context
   * @return
   */
  public AuthenticationEvent buildAuthenticationEvent(EventType type, String userId, String message,
                                                      AuthenticationProvider ap, Context context) {
    AuthenticationEvent event = new AuthenticationEvent(EventType.Info, new Date(),
        userId, message, ap.getId(), context.getSession());
    return event;
  }


  public boolean userCanAccessCourse(Course course, User user) {
    CourseMembership membership = null;
    try {
      membership = courseMembershipDbLoader.loadByCourseAndUserId(course.getId(), user.getId());
    } catch (KeyNotFoundException e) {
      return false;
    } catch (PersistenceException e) {
      throw new RuntimeException("Problem getting course availability information", e);
    }
    boolean accessible = CourseUtil.courseIsAvailableByDuration(true, course, new Date(), null, membership);
    return accessible;
  }

  public AuthenticationProviderManager getMgr() {
    return mgr;
  }

  public void setMgr(AuthenticationProviderManager mgr) {
    this.mgr = mgr;
  }

  public CourseMembershipDbLoader getCourseMembershipDbLoader() {
    return courseMembershipDbLoader;
  }

  public void setCourseMembershipDbLoader(CourseMembershipDbLoader courseMembershipDbLoader) {
    this.courseMembershipDbLoader = courseMembershipDbLoader;
  }


}

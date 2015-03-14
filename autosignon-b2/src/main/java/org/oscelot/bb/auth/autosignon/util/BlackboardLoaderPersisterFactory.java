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

package org.oscelot.bb.auth.autosignon.util;

import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.course.CourseMembershipDbLoader;
import blackboard.persist.user.UserDbLoader;
import blackboard.platform.authentication.AuthenticationManager;
import blackboard.platform.authentication.AuthenticationProviderManager;
import blackboard.platform.authentication.SessionManager;
import blackboard.platform.authentication.log.AuthenticationLogger;

/**
 * Created with IntelliJ IDEA.
 * User: wfuller
 * Date: 15/01/14
 * Time: 9:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class BlackboardLoaderPersisterFactory {

  public CourseDbLoader getCourseDbLoader() throws PersistenceException {
    return CourseDbLoader.Default.getInstance();
  }

  public CourseMembershipDbLoader getCourseMembershipDbLoader() throws PersistenceException {
    return CourseMembershipDbLoader.Default.getInstance();
  }

  public UserDbLoader getUserDbLoader() throws PersistenceException {
    return UserDbLoader.Default.getInstance();
  }

  public AuthenticationProviderManager getAuthenticationProviderManager() {
    return AuthenticationProviderManager.Factory.getInstance();
  }

  public AuthenticationLogger getAuthenticationLogger() {
    return AuthenticationLogger.Factory.getInstance();
  }


  public AuthenticationManager getAuthenticationManager() {
    return AuthenticationManager.Factory.getInstance();
  }

  public SessionManager getSessionManager() {
    return SessionManager.Factory.getInstance();
  }

}

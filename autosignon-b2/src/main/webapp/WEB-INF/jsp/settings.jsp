<%--

    Copyright (c) 2014 Swinburne University of Technology

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

    Created on : 03/08/2012, 7:11:34 PM
    Author     : wiley
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib uri="/bbNG" prefix="bbNG" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="stripes"
          uri="http://stripes.sourceforge.net/stripes-dynattr.tld" %>

<!DOCTYPE html>
<fmt:message var="pageTitle" key="autosignon.providerSettingsPage.title"/>
<fmt:message var="loginSourceStepTitle"
             key="autosignon.providerSettingsPage.loginSourceStepTitle"/>
<fmt:message var="sourceHostUrlLabel"
             key="autosignon.providerSettingsPage.sourceHostUrlLabel"/>
<fmt:message var="debugModeLabel"
             key="autosignon.providerSettingsPage.debugModeLabel"/>

<fmt:message var="securitySettingsStepTitle"
             key="autosignon.providerSettingsPage.securitySettingsStepTitle"/>
<fmt:message var="macAlgorithmLabel"
             key="autosignon.providerSettingsPage.macAlgorithmLabel"/>
<fmt:message var="extraParametersLabel"
             key="autosignon.providerSettingsPage.extraParametersLabel"/>
<fmt:message var="sharedSecretLabel"
             key="autosignon.providerSettingsPage.sharedSecretLabel"/>
<fmt:message var="sharedSecretPlaceholder"
             key="autosignon.providerSettingsPage.sharedSecretPlaceholder"/>
<fmt:message var="timestampDeltaLabel"
             key="autosignon.providerSettingsPage.timestampDeltaLabel"/>
<fmt:message var="requestParamStepTitle"
             key="autosignon.providerSettingsPage.requestParamStepTitle"/>
<fmt:message var="macRequestParamLabel"
             key="autosignon.providerSettingsPage.macRequestParamLabel"/>
<fmt:message var="userIdRequestParamLabel"
             key="autosignon.providerSettingsPage.userIdRequestParamLabel"/>
<fmt:message var="timestampRequestParamLabel"
             key="autosignon.providerSettingsPage.timestampRequestParamLabel"/>
<fmt:message var="forwardRequestParamLabel"
             key="autosignon.providerSettingsPage.forwardRequestParamLabel"/>
<fmt:message var="courseIdRequestParamLabel"
             key="autosignon.providerSettingsPage.courseIdRequestParamLabel"/>

<fmt:message var="connectionDetailsTitle"
             key="autosignon.providerSettingsPage.connectionDetailsStepTitle"/>

<bbNG:genericPage title="${pageTitle}">
    <bbNG:pageHeader>
        <bbNG:breadcrumbBar environment="SYS_ADMIN">
            <bbNG:breadcrumb title="${pageTitle}"/>
        </bbNG:breadcrumbBar>
        <bbNG:pageTitleBar title="${pageTitle}" showIcon="true"
                           showTitleBar="true"
                           iconUrl="/webapps/oslt-auth-provider-facebook-BBLEARN/images/f_icon.png"/>
    </bbNG:pageHeader>

    <stripes:form
            beanclass="org.oscelot.bb.auth.autosignon.stripes.SettingsAction"
            >
        <bbNG:dataCollection>
            <bbNG:step title="${loginSourceStepTitle}">
                <bbNG:dataElement label="${sourceHostUrlLabel}"
                                  isRequired="false" labelFor="applicationId">
                    <stripes:text id="sourceHostUrl"
                                  name="settings.sourceHostUrl"
                                  placeholder="https://portal.myinstitution.edu/"
                                  style="width:40em"></stripes:text>
                </bbNG:dataElement>
                <bbNG:dataElement label="${debugModeLabel}"
                                  isRequired="false" labelFor="debugMode">
                    <stripes:checkbox id="debugMode"
                                  name="settings.debug"></stripes:checkbox>
                </bbNG:dataElement>
            </bbNG:step>
            <bbNG:step title="${securitySettingsStepTitle}">
                <bbNG:dataElement label="${macAlgorithmLabel}"
                                  isRequired="true" labelFor="algorithm">
                    <stripes:select id="algorithm" name="settings.macAlgorithm">
                        <stripes:option value="MD5">MD5 (Legacy Compatibility)</stripes:option>
                        <stripes:option
                                value="HMAC_MD5">HMAC-MD5</stripes:option>
                        <stripes:option value="SHA1">SHA1</stripes:option>
                        <stripes:option
                                value="HMAC_SHA1">HMAC-SHA1</stripes:option>
                    </stripes:select>

                </bbNG:dataElement>
                <bbNG:dataElement label="${extraParametersLabel}"
                                  isRequired="false" labelFor="extraParameters">
                    <stripes:text id="extraParameters"
                                  name="settings.extraParameters"
                                  placeholder="courseId, extraInfo, etc"></stripes:text>
                </bbNG:dataElement>
                <bbNG:dataElement label="${sharedSecretLabel}"
                                  isRequired="true" labelFor="sharedSecret">
                    <stripes:text id="sharedSecret"
                                  name="settings.sharedSecret"
                                  placeholder="${sharedSecretPlaceholder}"></stripes:text>
                </bbNG:dataElement>
                <bbNG:dataElement label="${timestampDeltaLabel}"
                                  isRequired="true" labelFor="timestampDelta">
                    <stripes:text id="timestampDelta"
                                  name="settings.timestampDelta"></stripes:text>
                </bbNG:dataElement>
            </bbNG:step>
            <bbNG:step title="${requestParamStepTitle}">
                <bbNG:dataElement label="${macRequestParamLabel}"
                                  isRequired="true" labelFor="macParamName">
                    <stripes:text id="macParamName"
                                  name="settings.macParamName"></stripes:text>
                </bbNG:dataElement>
                <bbNG:dataElement label="${userIdRequestParamLabel}"
                                  isRequired="true" labelFor="userIdParamName">
                    <stripes:text id="userIdParamName"
                                  name="settings.userIdParamName"></stripes:text>
                </bbNG:dataElement>
                <bbNG:dataElement label="${timestampRequestParamLabel}"
                                  isRequired="true" labelFor="timestampParamName">
                    <stripes:text id="timestampParamName"
                                  name="settings.timestampParamName"></stripes:text>
                </bbNG:dataElement>
                <bbNG:dataElement label="${forwardRequestParamLabel}"
                                  isRequired="true" labelFor="forwardParamName">
                    <stripes:text id="forwardParamName"
                                  name="settings.forwardParamName"></stripes:text>
                </bbNG:dataElement>
                <bbNG:dataElement label="${courseIdRequestParamLabel}"
                                  isRequired="true" labelFor="courseIdParamName">
                    <stripes:text id="courseIdParamName"
                                  name="settings.courseIdParamName"></stripes:text>
                </bbNG:dataElement>

            </bbNG:step>
            <bbNG:step title="${connectionDetailsTitle}">
                <div style="background-color: beige; border: 1px solid goldenrod; border-radius: .25em; padding: .5em;">
                    <b>Autosignon URL:</b> <pre>${actionBean.helper.webAppRootUri}service/login/${actionBean.apId}</pre>
                </div>
            </bbNG:step>

            <bbNG:stepSubmit>

            </bbNG:stepSubmit>
        </bbNG:dataCollection>
        <stripes:hidden name="apId"/>
        <stripes:hidden name="saveSettings"/>
    </stripes:form>
</bbNG:genericPage>
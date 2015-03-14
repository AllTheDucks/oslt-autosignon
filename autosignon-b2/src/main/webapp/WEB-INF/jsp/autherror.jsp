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

<html>
<head><title>Authentication Error</title></head>
<body>
<h1>Authentication Error</h1>

<p>Something went wrong while trying to log you in. You might want to try the following:</p>
    <ol>
        <li>Return to the previous page and follow the link to Blackboard again.</li>
        <li>Login via the <a href="/">Blackboard front page</a></li>
        <li>Contact your Blackboard Administrator or Help Desk team if you still can't access Blackboard.</li>
    </ol>


<c:if test="${actionBean.debugMode}">

    <div style="border:1px solid silver; background-color: beige;">
        <h3>Debug Information</h3>
        <ul>
            <c:forEach items="${actionBean.debugMessages}" var="msg">
                <li>${msg}</li>
            </c:forEach>
        </ul>
    </div>

</c:if>

</body>
</html>
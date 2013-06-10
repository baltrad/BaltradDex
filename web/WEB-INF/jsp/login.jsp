<%------------------------------------------------------------------------------
Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW

This file is part of the BaltradDex software.

BaltradDex is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BaltradDex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
--------------------------------------------------------------------------------
Document   : Log in page
Created on : Apr 2, 2013, 9:32 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" 
    "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/WEB-INF/jsp/include.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="StyleSheet" href="includes/dex.css" type="text/css" 
              media="screen"/>
        <link rel="icon" type="image/png" href="includes/images/favicon.png"/>
        <script src="includes/js/load_clock.js" type="text/javascript" 
                language="Javascript">
        </script>
        <title>BALTRAD | Login</title>
	</head>
	<body onload="loadClock();">
		<div id="container">
			<t:header_login/>
			<div id="sidebar">
				<div id="logo-init"></div>
			</div>
			<div id="content">
                <div id="clock"></div>
				<form method="POST" id="login-form"
                      action="<c:url value='j_spring_security_check' />">
					<div class="logo"></div>
                    <c:choose>
                        <c:when test="${not empty init_error}">
                            <t:message_box errorHeader="System error"
                                           errorBody="${init_error}"
                                           email="${admin_email}"
                                           link="Report problem"/>
                        </c:when>
                        <c:otherwise>
                            <t:message_box msgHeader="Signed out"
                                           msgBody="${logout_msg}"
                                           errorHeader="Failed to sign in"
                                           errorBody="${login_error}"/>
                            <div class="leftcol">
                                <div class="row">
                                    Server Name:
                                </div>
                                <div class="row">
                                    User Name:
                                </div>
                                <div class="row">
                                    Password:
                                </div>
                            </div>
                            <div class="rightcol">
                                <div class="row">
                                    <c:out value="${node_name}"></c:out>
                                </div>
                                <div class="row">
                                    <input class="editbox" type="text" 
                                           name="j_username"
                                           title="User name">
                                    </input>
                                </div>
                                <div class="row">
                                    <input class="editbox" type="password" 
                                           name="j_password" 
                                           title="Password"></input>
                                </div>
                                <div class="buttons">
                                    <div class="button-wrap">
                                        <input class="button" type="reset" 
                                               value="Clear"></input>
                                    </div>
                                    <div class="button-wrap">
                                        <input class="button" type="submit" 
                                               value="Sign In"></input>
                                    </div>
                                </div>
                            </div>        
                        </c:otherwise>
                    </c:choose>
				</form>	
			</div>
			<div id="clearfooter"></div>
		</div>
		<t:footer/>    
	</body>
</html>

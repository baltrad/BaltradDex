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
Created on : Sep 22, 2010, 1:51 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="eu.baltrad.dex.config.manager.impl.ConfigurationManager"%> 

<jsp:useBean id="configurationManager" scope="request"
             class="eu.baltrad.dex.config.manager.impl.ConfigurationManager">
</jsp:useBean>

<%
    Date now = new Date();
    SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy, h:mm aa");
    String dateAndTime = format.format(now); 
    
    if (configurationManager.getAppConf() == null ) {
        request.getSession().setAttribute("init_status", 1);
    } else {
        request.getSession().setAttribute("init_status", 0);
    }
    String adminEmail = configurationManager.getAppConf().getAdminEmail();
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>BALTRAD | Login</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header_login.js"></script>
            </div>
            <div id="bltmain">
                <div class="login">
                    <c:choose>
                        <c:when test="${ init_status == 0 }">
                            <form method="post" action="<c:url value='j_spring_security_check' />">
                                <c:if test="${not empty login_error}">
                                    <div class="systemerror">
                                        <div class="header">
                                            Login failed.
                                        </div>
                                        <div class="message">
                                            Invalid user credentials.
                                        </div>
                                    </div>
                                    <c:set var="login_error" value="" scope="session" />
                                </c:if>
                                <c:if test="${not empty logout_message}">
                                    <div class="systemmessage">
                                        <div class="header">
                                            Logout successful.
                                        </div>
                                        <div class="message">
                                            User successfully logged out.
                                        </div>
                                    </div>
                                    <c:set var="logout_message" value="" scope="session" />
                                </c:if>  
                                <div class="left">
                                    <div class="date">
                                        <%= dateAndTime %>
                                    </div>
                                    <div class="username">
                                        User name
                                    </div>
                                    <div class="password">
                                        Password
                                    </div>
                                </div>
                                <div class="right">
                                    <div class="prompt">
                                        | Log on to BALTRAD
                                    </div>
                                    <div class="username">
                                        <input type="text" name="j_username"/>
                                        <div class="hint">
                                            Valid account name
                                        </div>
                                    </div>
                                    <div class="password">
                                        <input type="password" name="j_password"/>
                                        <div class="hint">
                                            Case-sensitive
                                        </div>
                                    </div>
                                    <div class="buttons">
                                        <button class="rounded" type="reset">
                                            <span>Clear</span>
                                        </button>
                                        <button class="rounded" type="submit">
                                            <span>Sign in</span>
                                        </button>
                                    </div>
                                </div>
                            </form>
                            <div class="passwdrecovery">
                                Forgot your user name or password?&nbsp; 
                                <a href="recovery.htm">Click here.</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="systemerror">
                                <div class="header">
                                    System failed to initialize.
                                </div>
                                <div class="message">
                                    System failed to initialize correctly. This may affect
                                    its basic functionality.<br>
                                    Please <a href="mailto:<%=adminEmail%>">report this problem</a>
                                    to node administrator.
                                </div>
                                <c:set var="error_message" value="" scope="session"/>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <div id="bltfooter">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>

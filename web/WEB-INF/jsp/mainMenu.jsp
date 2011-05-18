<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW

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
----------------------------------------------------------------------------------------------------
Document   : Main menu
Created on : Jun 22, 2010, 1:14:59 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ page import="eu.baltrad.dex.user.model.User" %>
<%@ page import="eu.baltrad.dex.log.model.LogEntry" %>
<%@ page import="java.util.Date" %>

<jsp:useBean id="applicationSecurityManager" scope="session"
                                    class="eu.baltrad.dex.util.ApplicationSecurityManager">
</jsp:useBean>
<jsp:useBean id="userManager" scope="session" class="eu.baltrad.dex.user.model.UserManager">
</jsp:useBean>
<jsp:useBean id="logManager" scope="session" class="eu.baltrad.dex.log.model.LogManager">
</jsp:useBean>

<%
    User sessionUser = ( User )applicationSecurityManager.getUser( request );
    User dbUser = userManager.getUserByName( sessionUser.getName() );
    if( !applicationSecurityManager.authenticateSessionUser( sessionUser, dbUser ) ) {

        logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_WARN,
                "User failed to access restricted system area" ) );
    } else {
        if( dbUser.getRoleName().equals( User.ROLE_ADMIN ) ) {
            request.getSession().setAttribute( "userRole", 0 );
        }
        if( dbUser.getRoleName().equals( User.ROLE_OPERATOR ) ) {
            request.getSession().setAttribute( "userRole", 1 );
        } 
        if( dbUser.getRoleName().equals( User.ROLE_PEER ) ) {
            request.getSession().setAttribute( "userRole", 2 );
        }
        if( dbUser.getRoleName().equals( User.ROLE_USER ) ) {
            request.getSession().setAttribute( "userRole", 3 );
        }
    }
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <c:choose>
            <c:when test="${userRole == 0}">
                <script type="text/javascript" src="includes/adminMenu.js"></script>
            </c:when>
            <c:when test="${userRole == 1}">
                <script type="text/javascript" src="includes/operatorMenu.js"></script>
            </c:when>
            <c:when test="${userRole == 2}">
                <script type="text/javascript" src="includes/peerMenu.js"></script>
            </c:when>
            <c:when test="${userRole == 3}">
                <script type="text/javascript" src="includes/userMenu.js"></script>
            </c:when>
        </c:choose>
    </body>
</html>

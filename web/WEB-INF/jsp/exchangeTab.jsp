<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW

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
Document   : Tabbed menu
Created on : Jun 3, 2011, 12:07 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="eu.baltrad.dex.user.model.User" %>
<%@ page import="eu.baltrad.dex.log.model.MessageLogger" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.util.Date" %>

<jsp:useBean id="applicationSecurityManager" scope="session"
                                    class="eu.baltrad.dex.util.ApplicationSecurityManager">
</jsp:useBean>
<jsp:useBean id="userManager" scope="session" class="eu.baltrad.dex.user.model.UserManager">
</jsp:useBean>
<%
    Logger log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    User sessionUser = ( User )applicationSecurityManager.getUser( request );
    User dbUser = userManager.getUserByName( sessionUser.getName() );
    if( !applicationSecurityManager.authenticateSessionUser( sessionUser, dbUser ) ) {
        log.warn( "User failed to access restricted system area" );
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

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <div id="tab">
            <a href="home.htm">
                Home
            </a>
        </div>
        <c:if test="${userRole == 0 || userRole == 1}">
            <div id="tab" class="active">
                <a href="exchange.htm">
                    Exchange
                </a>
            </div>
            <div id="tab">
                <a href="processing.htm">
                    Processing
                </a>
            </div>
        </c:if>
        <c:if test="${userRole == 0}">
            <div id="tab">
                <a href="settings.htm">
                    Settings
                </a>
            </div>
        </c:if>
    </body>
</html>

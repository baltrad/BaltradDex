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
Document   : Remove user account
Created on : Oct 5, 2010, 9:55 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@page import="java.util.List" %>
<%
    List users = ( List )request.getAttribute( "users" );
    if( users == null || users.size() <= 0 ) {
        request.getSession().setAttribute( "users_status", 0 );
    } else {
        request.getSession().setAttribute( "users_status", 1 );
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Remove account</title>
    </head>
    <body>
        <div id="container">
            <div id="header">
                <script type="text/javascript" src="includes/header.js"></script>
            </div>
            <div id="content">
                <div id="left">
                    <%@include file="/WEB-INF/jsp/mainMenu.jsp"%>
                </div>
                <div id="right">
                    <div id="page-title">
                        <div class="left">
                            Remove user account
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <c:choose>
                        <c:when test="${users_status == 1}">
                            <div id="text-box">
                                Select user accounts to be be removed. Confirm account removal
                                with Submit button.
                            </div>
                            <form action="showSelectedUsers.htm">
                                <div id="table">
                                    <div id="showusers">
                                        <div class="table-hdr">
                                            <div class="name">
                                                User name
                                            </div>
                                            <div class="role">
                                                Role
                                            </div>
                                            <div class="factory">
                                                Organization
                                            </div>
                                            <div class="check">
                                                Select
                                            </div>
                                        </div>
                                        <c:forEach var="user" items="${users}">
                                            <div class="table-row">
                                                <div class="name">
                                                   <c:out value="${user.name}"/>
                                                </div>
                                                <div class="role">
                                                    <c:out value="${user.roleName}"/>
                                                </div>
                                                <div class="factory">
                                                    <c:out value="${user.factory}"/>
                                                </div>
                                                <div class="check">
                                                    <input type="checkbox" name="selected_users"
                                                        value="${user.id}"/>
                                                </div>
                                            </div>
                                        </c:forEach>
                                        <div class="footer">
                                            <div class="right">
                                                <button class="rounded" type="button"
                                                    onclick="window.location='configuration.htm'">
                                                    <span>Back</span>
                                                </button>
                                                <button class="rounded" type="submit">
                                                    <span>Submit</span>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-alert.png"
                                         alt="no_radars"/>
                                </div>
                                <div class="text">
                                    No user accounts found. Use configuration options to add
                                    new user accounts.
                                </div>
                            </div>
                            <div class="footer">
                                <div class="right">
                                    <form action="configuration.htm">
                                        <button class="rounded" type="submit">
                                            <span>OK</span>
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <script type="text/javascript" src="includes/footer.js"></script>
        </div>
    </body>
</html>
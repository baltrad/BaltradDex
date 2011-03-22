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
Document   : Edit user account
Created on : Oct 4, 2010, 2:27 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="java.util.List" %>
<%
    List users = ( List )request.getAttribute( "registered_users" );
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
        <title>Baltrad | Edit account</title>
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
                            Edit user account
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <c:choose>
                        <c:when test="${users_status == 1}">
                            <div id="text-box">
                                List of user accounts. Click on user name in order to
                                modify account settings.
                            </div>
                            <div id="table">
                                <div id="edituser">
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
                                    </div>
                                    <c:forEach var="user" items="${registered_users}">
                                        <div class="table-row">
                                            <div class="name">
                                                <a href="saveUser.htm?userId=${user.id}">
                                                    <c:out value="${user.name}"/>
                                                </a>
                                            </div>
                                            <div class="role">
                                                <c:out value="${user.roleName}"/>
                                            </div>
                                            <div class="factory">
                                                <c:out value="${user.factory}"/>
                                            </div>
                                        </div>
                                    </c:forEach>
                                    <div class="footer">
                                        <div class="right">
                                            <form action="configuration.htm">
                                                <button class="rounded" type="submit">
                                                    <span>Back</span>
                                                </button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
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

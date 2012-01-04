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
Document   : Remove user account page
Created on : Oct 4, 2010, 2:27 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@ page import="java.util.List" %>
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
        <title>Baltrad | Remove user account</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/settingsTab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/settingsMenu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            Remove user account
                        </div>
                        <c:choose>
                            <c:when test="${users_status == 1}">
                                <div class="blttext">
                                    List of user accounts. Select user accounts to remove.
                                </div>
                                <div class="table">
                                    <div class="removeaccount">
                                        <form action="accountToRemove.htm" method="post">
                                            <div class="tableheader">
                                                <div id="cell" class="count">&nbsp;</div>
                                                <div id="cell" class="username">
                                                    User name
                                                </div>
                                                <div id="cell" class="rolename">
                                                    Role
                                                </div>
                                                <div id="cell" class="orgname">
                                                    Organization
                                                </div>
                                                <div id="cell" class="check">
                                                    Select
                                                </div>
                                            </div>
                                            <c:set var="count" scope="page" value="1"/>
                                            <c:forEach var="user" items="${users}">
                                                <div class="entry">
                                                    <div id="cell" class="count">
                                                        <c:out value="${count}"/>
                                                        <c:set var="count" value="${count + 1}"/>
                                                    </div>
                                                    <div id="cell" class="username">
                                                        <c:out value="${user.name}"/>
                                                    </div>
                                                    <div id="cell" class="rolename">
                                                        <c:out value="${user.roleName}"/>
                                                    </div>
                                                    <div id="cell" class="orgname">
                                                        <c:out value="${user.organizationName}"/>
                                                    </div>
                                                    <div id="cell" class="check">
                                                        <input type="checkbox" name="selected_users"
                                                            value="${user.id}"/>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                            <div class="tablefooter">
                                                <div class="buttons">
                                                    <button class="rounded" type="button"
                                                        onclick="window.location.href='settings.htm'">
                                                        <span>Back</span>
                                                    </button>
                                                    <button class="rounded" type="submit">
                                                        <span>Submit</span>
                                                    </button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="blttext">
                                    No user accounts have been found.
                                    Use add user account functionality in order to set new accounts.
                                </div>
                                <div class="table">
                                    <div class="tablefooter">
                                        <div class="buttons">
                                            <button class="rounded" type="button"
                                                onclick="window.location.href='saveAccount.htm'">
                                                <span>Add</span>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
        <div id="bltfooter">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>
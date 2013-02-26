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
Document   : Edit user account page
Created on : Oct 4, 2010, 2:27 PM
Author     : szewczenko
------------------------------------------------------------------------------%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Edit user account" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Edit user account
            </div>
            <c:choose>
                <c:when test="${not empty accounts}">
                    <div class="blttext">
                        List of user accounts. Click on user name in order to
                        modify account settings.
                    </div>
                    <div class="table">
                        <div class="editaccount">
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
                                <div id="cell" class="passwdchange">
                                    Password
                                </div>
                            </div>
                            <c:set var="count" scope="page" value="1"/>
                            <c:forEach var="account" items="${accounts}">
                                <c:if test="${account.role != 'node'}">
                                    <div class="entry">
                                        <div id="cell" class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
                                        </div>
                                        <div id="cell" class="username">
                                            <a href="save_user_account.htm?user_id=${account.id}">
                                                <c:out value="${account.name}"/>
                                            </a>
                                        </div>
                                        <div id="cell" class="rolename">
                                            <c:out value="${account.role}"/>
                                        </div>
                                        <div id="cell" class="orgname">
                                            <c:out value="${account.orgName}"/>
                                        </div>
                                        <c:if test="${account.role != 'peer'}">
                                            <div id="cell" class="passwdchange">
                                                <a href="change_user_password.htm?user_id=${account.id}">
                                                    Change
                                                </a>
                                            </div>
                                        </c:if>                      
                                    </div>
                                </c:if>                     
                            </c:forEach>
                            <div class="tablefooter">
                                <div class="buttons">
                                    <button class="rounded" type="button"
                                        onclick="window.location.href='settings.htm'">
                                        <span>Back</span>
                                    </button>
                                </div>
                            </div>
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
                                    onclick="window.location.href='save_user_account.htm'">
                                    <span>Add</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </jsp:body>
</t:page_tabbed>

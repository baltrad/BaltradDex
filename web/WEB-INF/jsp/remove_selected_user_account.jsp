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

<t:page_tabbed pageTitle="Remove user account" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Remove user account
            </div>
            <div class="blttext">
                The following user accounts will be removed from the system.
            </div>
            <div class="table">
                <div class="removeaccount">
                    <form action="remove_user_account_status.htm" method="post">
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
                        </div>
                        <c:set var="count" scope="page" value="1"/>
                        <c:forEach var="user" items="${selected_users}">
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
                                <div class="hidden">
                                    <input type="checkbox" name="removed_users"
                                        value="${user.id}" checked/>
                                </div>
                            </div>
                        </c:forEach>
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                    onclick="window.location.href='remove_user_account.htm'">
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
        </div>
    </jsp:body>
</t:page_tabbed>

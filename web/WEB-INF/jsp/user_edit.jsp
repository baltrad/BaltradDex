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
Created on : May 23, 2013, 10:15 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Edit user account">
    <jsp:body>
        <div class="users">
            <div class="table">
                <div class="header">
                    <div class="row">
                        Edit user account
                    </div>
                </div>
                <div class="header-text">
                    Click on user name in order to edit account 
                    settings. Click <i>Change</i> to modify user password.
                </div>
                <div class="body">
                    <div class="header-row">
                        <div class="count">&nbsp;</div>
                        <div class="name">User name</div>
                        <div class="role">Role</div>
                        <div class="org">Organization</div>
                        <div class="passwd">Password</div>
                    </div>
                    <c:set var="count" scope="page" value="1"/>
                    <c:forEach items="${accounts}" var="account">
                        <c:if test="${account.role != 'node'}">
                            <div class="row">
                                <div class="count">
                                    <c:out value="${count}"/>
                                    <c:set var="count" value="${count + 1}"/>
                                </div>
                                <div class="name">
                                    <a href="user_save.htm?user_id=${account.id}">
                                        <c:out value="${account.name}"/>
                                    </a>
                                </div>
                                <div class="role">
                                    <c:out value="${account.role}"/>
                                </div>
                                <div class="org">
                                    <c:out value="${account.orgName}"/>
                                </div>
                                <c:if test="${account.role != 'peer'}">
                                    <div class="passwd">
                                        <a href="user_change_password.htm?user_id=${account.id}">
                                            Change
                                        </a>
                                    </div>
                                </c:if>                      
                            </div>
                        </c:if> 
                    </c:forEach>
                </div>
                <div class="table-footer">
                    <div class="buttons">
                        <div class="button-wrap">
                            <input class="button" type="button" 
                                   value="Home"
                                   onclick="window.location.href='status.htm'"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>    
    </jsp:body>
</t:generic_page>

                
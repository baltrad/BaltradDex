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
Document   : Remove selected user account page
Created on : May 23, 2013, 3:11 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Remove user account">
    <jsp:body>
        <div class="users">
            <div class="table">
                <div class="header">
                    <div class="row">Remove user account</div>
                </div>
                <div class="header-text">
                    The following user accounts will be removed.
                </div>
                 <form action="user_remove_status.htm">
                    <div class="body">
                        <div class="header-row">
                            <div class="count">&nbsp;</div>
                            <div class="name">User name</div>
                            <div class="role">Role</div>
                            <div class="org">Organization</div>
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
                                        <c:out value="${account.name}"/>
                                    </div>
                                    <div class="role">
                                        <c:out value="${account.role}"/>
                                    </div>
                                    <div class="org">
                                        <c:out value="${account.orgName}"/>
                                    </div>
                                    <div class="hidden">
                                        <input type="checkbox" name="accounts"
                                            value="${account.id}" checked/>
                                    </div>
                                </div>
                            </c:if> 
                        </c:forEach>
                    </div>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="button" 
                                       value="Back"
                                       onclick="window.location.href='user_remove.htm'"/>
                            </div>
                            <div class="button-wrap">
                                <input class="button" type="submit" 
                                       value="OK"/>
                            </div>
                        </div>
                    </div>                                 
                </form>                            
            </div>
        </div>    
    </jsp:body>
</t:generic_page>

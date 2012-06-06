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
Document   : Save data source users page
Created on : Apr 27, 2011, 8:05 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Configure data source" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Configure data source <div class="stepno">Step 4</div>
            </div>
            <div class="blttext">
                Select users
                <div class="hint">
                    This data source will be available for the selected users.
                </div>
            </div>
            <div class="table">
                <div class="dssave">
                    <form method="post" action="save_datasource_summary.htm">
                        <div class="rightcol">
                            <c:forEach items="${selectedUsers}" var="user">
                                <div class="dsparam">
                                    Name:&nbsp;<c:out value="${user.name}"></c:out>&nbsp;
                                    Role: <c:out value="${user.roleName}"></c:out>
                                    &nbsp;Organization:
                                    <c:out value="${user.organizationName}"></c:out>
                                </div>
                            </c:forEach>
                            <div class="row">
                                <c:if test="${numSelectedUsers < numAvailableUsers}">
                                    <select name="usersList"
                                            title="Select user from the list">
                                        <option value="select">
                                            <c:out value="-- Select user --"/>
                                        </option>
                                        <c:forEach items="${availableUsers}"
                                                    var="user">
                                            <option value="${user.name}">
                                                <c:out value="${user.name}"/>
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <div class="dscontrol">
                                        <input type="submit" name="addUser"
                                            title="Add user" value="+">
                                    </div>
                                </c:if>
                                <c:if test="${numSelectedUsers > 0}">
                                    <div class="dscontrol">
                                        <input type="submit" name="removeUser"
                                            title="Remove user" value="-">
                                    </div>
                                </c:if>
                            </div>
                            <c:if test="${not empty dsSelectUsersError}">
                                <div class="error">
                                    <c:out value="${dsSelectUsersError}"/>
                                </div>
                            </c:if>
                        </div>
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="submit" name="backButton">
                                    <span>Back</span>
                                </button>
                                <button class="rounded" type="submit" name="nextButton">
                                    <span>Next</span>
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </jsp:body>    
</t:page_tabbed>

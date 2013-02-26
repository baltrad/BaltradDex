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
Document   : User settings page
Created on : Oct 3, 2012, 1:33 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="System settings" activeTab="settings">
    <div class="left">
        <t:menu_user_settings/>
    </div>
    <div class="right">
        <div class="blttitle">
            Change user's password
        </div>
        <div class="blttext">
            Change password for current user account
        </div>
        <div class="table">
            <div class="changepasswd">
                <c:if test="${not empty message}">
                    <div class="systemmessage">
                        <div class="header">
                            Success.
                        </div>
                        <div class="message">
                            <c:out value="${message}"/>
                            <c:set var="message" value="" scope="session" />
                        </div>
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="systemerror">
                        <div class="header">
                            Problems encountered.
                        </div>
                        <div class="message">
                            <c:out value="${error}"/>
                            <c:set var="error" value="" scope="session" />
                        </div>
                    </div>
                </c:if>                
                <form:form method="POST" commandName="user_account">
                    <div class="leftcol">
                        <div class="row">User name</div>
                        <div class="row">New password</div>
                        <div class="row">Confirm new password</div>
                    </div>
                    <div class="rightcol">
                        <div class="row">
                            <div class="username">
                                <form:input path="name" readonly="true"
                                            title="Current user name"/>
                                <div class="hint">
                                    User name
                                </div>
                            </div>
                            <form:errors path="name" cssClass="error"/>
                        </div>
                        <div class="row">
                            <div class="password">
                                <form:password path="password"
                                               title="Enter new password"/>
                                <div class="hint">
                                    New password
                                </div>
                            </div>
                            <form:errors path="password" cssClass="error"/>
                        </div>
                        <div class="row">
                            <div class="password">
                                <div class="password">
                                    <input type="password" 
                                           name="repeat_password"
                                           title="Repeat password"/>
                                    <div class="hint">
                                        Repeat password
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tablefooter">
                        <div class="buttons">
                            <button class="rounded" type="button"
                                onclick="window.location.href='home.htm'">
                                <span>Cancel</span>
                            </button>
                            <button class="rounded" type="submit">
                                <span>Save</span>
                            </button>
                        </div>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</t:page_tabbed>

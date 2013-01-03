<%------------------------------------------------------------------------------
Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW

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
Document   : Change user password page
Created on : Jul 13, 2010, 11:58 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Change user password" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Change password
            </div>
            <div class="blttext">
                Change password for a selected user account
            </div>
            <div class="table">
                <div class="changepasswd">
                    <form:form method="POST" commandName="user_account">
                        <div class="leftcol">
                            <div class="row">User name</div>
                            <div class="row">New password</div>
                            <div class="row">Confirm new password</div>
                        </div>
                        <div class="rightcol">
                            <div class="row">
                                <div class="username">
                                    <form:input path="name" readonly="true"/>
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
                                    <form:password path="repeatPassword"
                                                   title="Repeat new password"/>
                                    <div class="hint">
                                        Repeat new password here
                                    </div>
                                </div>
                                <form:errors path="repeatPassword"
                                             cssClass="error"/>
                            </div>
                        </div>
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                        onclick="window.location.href='edit_user_account.htm'">
                                    <span>Back</span>
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
    </jsp:body>
</t:page_tabbed>

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
Document   : Change user password page
Created on : May 23, 2013, 12:43 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Change password">
    <jsp:body>
        <div class="user-passwd">
            <div class="table">
                <div class="header">
                    <div class="row">Change password</div>
                </div>
                <div class="header-text">
                    Changing password for user account: 
                    <span><c:out value="${user_account.name}"/></span>
                </div>
                <form:form method="POST" commandName="user_account">
                    <div class="body">
                        <div class="row">
                            <div class="leftcol">New password:</div>
                            <div class="rightcol">
                                <form:password path="password"
                                               title="Enter new password"/>
                                <form:errors path="password" cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">Repeat password:</div>
                            <div class="rightcol">
                                <input type="password" 
                                       name="repeat_password"
                                       title="Repeat password"/>
                            </div>
                        </div>
                    </div>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="button" 
                                       value="Back"
                                       onclick="window.location.href='user_edit.htm'"/>
                            </div>
                            <div class="button-wrap">
                                <input class="button" type="submit" 
                                       value="Save"/>
                            </div>
                        </div>
                    </div>  
                </form:form>
            </div>
        </div>
    </jsp:body>
</t:generic_page>

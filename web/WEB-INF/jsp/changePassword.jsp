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
Document   : Change user password page
Created on : Jul 13, 2010, 11:58 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Change password</title>
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
                            Change password
                        </div>
                        <div class="blttext">
                            Change password for a selected user account
                        </div>
                        <div class="table">
                            <div class="changepasswd">
                                <%@include file="/WEB-INF/jsp/formMessages.jsp"%>
                                <form method="post">
                                    <div class="leftcol">
                                        <div class="row">User name</div>
                                        <div class="row">New password</div>
                                        <div class="row">Confirm new password</div>
                                    </div>
                                    <div class="rightcol">
                                        <div class="row">
                                            <div class="username">
                                                <form:input path="command.userName" readonly="true"/>
                                                <div class="hint">
                                                   User name
                                                </div>
                                            </div>
                                            <form:errors path="command.userName" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="password">
                                                <form:password path="command.newPasswd"
                                                    title="Enter new password"/>
                                                <div class="hint">
                                                   New password
                                                </div>
                                            </div>
                                            <form:errors path="command.newPasswd" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="password">
                                                <form:password path="command.confirmNewPasswd"
                                                    title="Repeat new password"/>
                                                <div class="hint">
                                                   Repeat new password here
                                                </div>
                                            </div>
                                            <form:errors path="command.confirmNewPasswd"
                                                         cssClass="error"/>
                                        </div>
                                    </div>
                                    <div class="tablefooter">
                                       <div class="buttons">
                                           <button class="rounded" type="button"
                                               onclick="window.location.href='editAccount.htm'">
                                               <span>Back</span>
                                           </button>
                                           <button class="rounded" type="submit">
                                               <span>Save</span>
                                           </button>
                                       </div>
                                   </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div id="bltfooter">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>
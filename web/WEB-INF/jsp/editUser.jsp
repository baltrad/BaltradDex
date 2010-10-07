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
Document   : Edit user account
Created on : Oct 4, 2010, 2:27 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Edit account</title>
    </head>
    <body>
        <div id="container">
            <div id="header">
                <script type="text/javascript" src="includes/header.js"></script>
            </div>
            <div id="content">
                <div id="left">
                    <%@include file="/WEB-INF/jsp/mainMenu.jsp"%>
                </div>
                <div id="right">
                    <div id="page-title">
                        <div class="left">
                            Edit user account
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        List of user accounts. Click on user name in order to
                        modify account settings.
                    </div>
                    <div id="table">
                        <display:table name="registered_users" id="user" defaultsort="1"
                            requestURI="showUsers.htm" cellpadding="0" cellspacing="2"
                            export="false" class="tableborder">
                            <display:column sortable="true" sortProperty="name" title="User name"
                                paramId="id" paramProperty="id" href="saveUser.htm" class="tdcenter"
                                value="${user.name}">
                            </display:column>
                            <display:column sortable="true" title="Role" sortProperty="role"
                                class="tdcenter" value="${user.roleName}">
                            </display:column>
                            <display:column sortable="true" title="Organization"
                                sortProperty="factory" class="tdcenter"
                                value="${user.factory}">
                            </display:column>
                        </display:table>
                        <div class="footer">
                            <div class="right">
                                <form action="configuration.htm">
                                    <button class="rounded" type="submit">
                                        <span>Back</span>
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <script type="text/javascript" src="includes/footer.js"></script>
        </div>
    </body>
</html>

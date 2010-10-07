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
Document   : Remove node connection
Created on : Oct 6, 2010, 1:29 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Remove connection</title>
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
                            Remove node connection
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        Warning: The following node connection(s) will be removed from the system.
                    </div>
                    <div id="table">
                        <form method="post" action="showRemovedNodeConnections.htm">
                            <display:table name="selected_node_connections" id="connection"
                                defaultsort="1" requestURI="showNodeConnections.htm"
                                cellpadding="0" cellspacing="2" export="false"
                                class="tableborder">
                                <display:column sortable="true" title="Connection"
                                    sortProperty="connectionName" paramId="connectionName"
                                    paramProperty="connectionName"
                                    class="tdcenter" value="${connection.connectionName}">
                                </display:column>
                                <display:column sortable="true" title="Node address"
                                    sortProperty="nodeAddress" paramId="nodeAddress"
                                    paramProperty="nodeAddress" class="tdcenter"
                                    value="${connection.nodeAddress}">
                                </display:column>
                                <display:column sortable="true" title="User account"
                                    sortProperty="userName" paramId="userName"
                                    paramProperty="userName" class="tdcenter"
                                    value="${connection.userName}">
                                </display:column>
                            </display:table>
                            <div class="footer">
                                <div class="right">
                                    <button class="rounded" type="button"
                                        onclick="history.go(-1);">
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
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <script type="text/javascript" src="includes/footer.js"></script>
        </div>
    </body>
</html>

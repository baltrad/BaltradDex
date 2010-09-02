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
Document   : Node connection management page
Created on : Aug 31, 2010, 12:52:37 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%@ page import="java.util.List" %>
<%
    // Check if list of available node connections is not empty
    List avConns = ( List )request.getAttribute( "node_connections" );
    if( avConns == null || avConns.size() <= 0 ) {
        request.getSession().setAttribute( "av_node_cons_status", 0 );
    } else {
        request.getSession().setAttribute( "av_node_cons_status", 1 );
    }
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Manage node connections</title>
</head>

<body>
    <div id="container">
        <div id="header"></div>
        <div id="nav">
            <script type="text/javascript" src="includes/navigation.js"></script>
        </div>
        <div class="outer">
            <div class="inner">
                <div class="float-wrap">
                    <div id="main">
                        <h1>Manage node connections</h1>
                        <br/>
                        <c:choose>
                            <c:when test="${av_node_cons_status == 1}">
                            <h2>
                                <p>
                                Click on a check box to select node connection for removal.
                                </p>
                            </h2>
                            <form action="showSelectedNodeConnections.htm">
                                <display:table name="node_connections" id="connection"
                                    defaultsort="1" requestURI="showNodeConnections.htm"
                                    cellpadding="0" cellspacing="2" export="false"
                                    class="tableborder">
                                    <display:column sortable="true" title="Connection name"
                                        sortProperty="connectionName" paramId="connectionName"
                                        paramProperty="connectionName"
                                        class="tdcenter" value="${connection.connectionName}">
                                    </display:column>
                                    <display:column sortable="true" title="Node address"
                                        sortProperty="nodeAddress" paramId="nodeAddress"
                                        paramProperty="nodeAddress" class="tdcenter"
                                        value="${connection.nodeAddress}">
                                    </display:column>
                                    <display:column sortable="true" title="User name"
                                        sortProperty="userName" paramId="userName"
                                        paramProperty="userName" class="tdcenter"
                                        value="${connection.userName}">
                                    </display:column>
                                    <display:column sortable="false" title="Remove"
                                        class="tdcheck"> <input type="checkbox"
                                        name="selected_node_connections"
                                        value="${connection.id}"/>
                                    </display:column>
                                </display:table>
                            <div id="table-footer">
                                <input type="submit" value="Submit" name="submitButton"/>
                            </div>
                            </form>
                            </c:when>
                            <c:otherwise>
                                <div id="message-box">
                                    List of node connections is currently empty.
                                </div>
                                <div id="table-footer">
                                    <form action="adminControls.htm">
                                        <input type="submit" value="Back" name="admin_button"/>
                                    </form>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div id="left">
                        <%@ include file="/WEB-INF/jsp/mainMenu.jsp"%>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
    </div>
    <div id="footer">
        <script type="text/javascript" src="includes/footer.js"></script>
    </div>
</body>
</html>




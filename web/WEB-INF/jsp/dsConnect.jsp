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
Document   : Displays list of remote data sources
Created on : Sep 29, 2010, 12:00 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="java.util.List" %>
<%
    // Check if remote data sources list is not null
    List remoteDataSources = ( List )request.getAttribute( "remoteDataSources" );
    if( remoteDataSources == null ) {
        // remote node is unavailable
        request.getSession().setAttribute( "remote_node_status", 0 );
    } else if( remoteDataSources.size() == 0 ) {
        // remote node is available, but no permissions on data sources have been set
        request.getSession().setAttribute( "remote_node_status", 1 );
    } else {
        // remote node is available
        request.getSession().setAttribute( "remote_node_status", 2 );
    }
    // Get remote node name
    String remoteNodeName = ( String )request.getAttribute( "sender_node_name" );
    if( remoteNodeName != null && !remoteNodeName.isEmpty() ) {
        remoteNodeName = " at " + remoteNodeName;
    } else {
        remoteNodeName = "";
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Connect to data source</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/exchangeTab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/exchangeMenu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            Remote data sources available <%= remoteNodeName %>
                        </div>
                        <c:choose>
                            <c:when test="${remote_node_status == 2}">
                                <div class="blttext">
                                    List of remote data sources. Subscribe a desired data source
                                    by selecting a corresponding check box.
                                </div>
                                <div class="table">
                                    <div class="dsconnect">
                                        <form action="dsToConnect.htm" method="post">
                                            <div class="tableheader">
                                                <div id="cell" class="count">&nbsp;</div>
                                                <div id="cell" class="name">
                                                    Name
                                                </div>
                                                <div id="cell" class="description">
                                                    Description
                                                </div>
                                                <div id="cell" class="check">
                                                    Select
                                                </div>
                                            </div>
                                            <c:set var="count" scope="page" value="1"/>
                                            <c:forEach items="${remoteDataSources}" var="dataSource">
                                                <div class="entry">
                                                    <div id="cell" class="count">
                                                        <c:out value="${count}"/>
                                                        <c:set var="count" value="${count + 1}"/>
                                                    </div>
                                                    <div id="cell" class="name">
                                                        <c:out value="${dataSource.name}"/>
                                                    </div>
                                                    <div id="cell" class="description">
                                                        <c:out value="${dataSource.description}"/>
                                                    </div>
                                                    <div id="cell" class="check">
                                                        <input type="checkbox" name="selectedDataSources"
                                                            value="${dataSource.name}"/>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                            <div class="tablefooter">
                                                <div class="buttons">
                                                    <button class="rounded" type="submit">
                                                        <span>OK</span>
                                                    </button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </c:when>
                            <c:when test="${remote_node_status == 1}">
                                <div class="blttext">
                                    You have successfully connected to the remote node, but you
                                    don't seem to be allowed to subscribe any data sources at
                                    this node.
                                    <br><br>
                                    Ask remote node administrator to make data sources available for
                                    you to subscribe.
                                </div>
                                <div class="table">
                                    <div class="tablefooter">
                                        <div class="buttons">
                                            <button class="rounded" type="button"
                                                onclick="window.location.href='connectToNode.htm'">
                                                <span>Back</span>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="blttext">
                                    Failed to access remote node. Check your user name
                                    and/or password. In case the problem persists,
                                    contact remote node administrator.
                                </div>
                                <div class="table">
                                    <div class="tablefooter">
                                        <div class="buttons">
                                            <button class="rounded" type="button"
                                                onclick="window.location.href='connectToNode.htm'">
                                                <span>Back</span>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
        <div id="bltfooter">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>
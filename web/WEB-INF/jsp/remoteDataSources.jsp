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
Document   : Page displays list of remote data sources
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
        <title>Baltrad | Remote data sources</title>
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
                            Remote data sources <%= remoteNodeName %>
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <c:choose>
                        <c:when test="${remote_node_status == 2}">
                            <div id="text-box">
                                List of remote data sources.
                                Subscribe a desired remote data source by selecting
                                a corresponding check box.
                            </div>
                            <form action="selectedRemoteDataSources.htm">
                                <div id="table">
                                    <div id="remoteDataSources">
                                        <div class="table-hdr">
                                            <div class="name">
                                                Data source
                                            </div>
                                            <div class="description">
                                                Description
                                            </div>
                                            <div class="check">
                                                Select
                                            </div>
                                        </div>
                                        <c:forEach var="dataSource" items="${remoteDataSources}">
                                            <div class="table-row">
                                                <div class="name">
                                                    <c:out value="${dataSource.name}"/>
                                                </div>
                                                <div class="description">
                                                    <c:out value="${dataSource.description}"/>
                                                </div>
                                                <div class="check">
                                                    <input type="checkbox" name="selectedDataSources"
                                                        value="${dataSource.name}"/>
                                                </div>
                                            </div>
                                        </c:forEach>
                                        <div class="footer">
                                            <div class="right">
                                                <button class="rounded" type="submit">
                                                    <span>Submit</span>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </c:when>
                        <c:when test="${remote_node_status == 1}">
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-alert.png"
                                         alt="no_data"/>
                                </div>
                                <div class="text">
                                    You have successfully connected to the remote node, but you 
                                    don't seem to be allowed to subscribe any data sources at
                                    this node.
                                    <br><br>
                                    Ask remote node administrator to make data sources available for
                                    you to subscribe.
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-minus.png"
                                         alt="no_data"/>
                                </div>
                                <div class="text">
                                    Failed to access remote node. Check your user name
                                    and/or password. In case the problem persists,
                                    contact remote node administrator.
                                </div>
                            </div>
                            <div class="footer">
                                <div class="right">
                                    <form action="connectToNode.htm">
                                        <button class="rounded" type="submit">
                                            <span>Back</span>
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>                        
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>




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
Document   : Selected remote radar stations
Created on : Sep 30, 2010, 11:55 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="java.util.List" %>
<%
    // Check selection list is not empty
    List selDataSources = ( List )request.getAttribute( "selectedDataSources" );
    // Subscription status is not changed
    if( selDataSources == null || selDataSources.size() <= 0 ) {
        request.getSession().setAttribute( "selection_status", 0 );
    } else {
        request.getSession().setAttribute( "selection_status", 1 );
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
                        <c:when test="${selection_status == 1}">
                            <div id="text-box">
                                Remote data sources selected for subscription.
                                Please confirm your selection.
                            </div>
                            <div id="table">
                                <form action="subscribedRemoteDataSources.htm">
                                    <div id="table">
                                        <div id="dataSourceTable">
                                            <div class="table-hdr">
                                                <div class="name">
                                                    Data source
                                                </div>
                                                <div class="description">
                                                    Description
                                                </div>
                                            </div>
                                            <c:forEach var="dataSource" items="${selectedDataSources}">
                                                <div class="table-row">
                                                    <div class="name">
                                                        <c:out value="${dataSource.name}"/>
                                                    </div>
                                                    <div class="description">
                                                        <c:out value="${dataSource.description}"/>
                                                    </div>
                                                    <%--div class="tdhidden">
                                                        <input type="checkbox" name="removed_channels"
                                                            value="${channel.id}" checked/>
                                                    </div--%>
                                                </div>
                                            </c:forEach>
                                            <div class="footer">
                                                <div class="right">
                                                    <button class="rounded" type="button"
                                                        onclick="window.location='connectToNode.htm'">
                                                        <span>Back</span>
                                                    </button>
                                                    <button class="rounded" type="submit">
                                                        <span>Submit</span>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-alert.png"
                                         alt="no_data"/>
                                </div>
                                <div class="text">
                                    No data sources have been selected.
                                    Click on the button to go back to node connection page.
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

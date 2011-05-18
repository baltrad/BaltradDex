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
Document   : Subscription management page
Created on : Sep 30, 2010, 16:34 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="java.util.List" %>
<%
    // Check if list of available subscriptions is not empty
    List avSubs = ( List )request.getAttribute( "subscriptions" );
    if( avSubs == null || avSubs.size() <= 0 ) {
        request.getSession().setAttribute( "av_subs_status", 0 );
    } else {
        request.getSession().setAttribute( "av_subs_status", 1 );
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Subscriptions</title>
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
                            Subscription management
                        </div>
                        <div class="right">
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${av_subs_status == 1}">
                            <div id="text-box">
                                Click on a check box to subscribe or unsubscribe a desired
                                data source.
                                Click on data source name in order to browse data from this
                                source data .
                            </div>
                            <form action="showSelectedSubscriptions.htm">
                                <div id="table">
                                    <div id="subscriptions">
                                        <div class="table-hdr">
                                            <div class="station">
                                                Data source
                                            </div>
                                            <div class="operator">
                                                Operator
                                            </div>
                                            <div class="synkronized">
                                                Status
                                            </div>
                                            <div class="active">
                                                Active
                                            </div>
                                        </div>
                                        <c:forEach var="sub" items="${subscriptions}">
                                            <div class="table-row">
                                                <div class="station">
                                                    <a href="dsFiles.htm?dsName=${sub.dataSourceName}">
                                                        <c:out value="${sub.dataSourceName}"/>
                                                    </a>
                                                </div>
                                                <div class="operator">
                                                    <c:out value="${sub.operatorName}"/>
                                                </div>
                                                <div class="synkronized">
                                                <c:choose>
                                                    <c:when test="${sub.synkronized == true}">
                                                        <img src="includes/images/green_bulb.png"
                                                                 alt="active"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="includes/images/red_bulb.png"
                                                                 alt="deactivated"/>
                                                    </c:otherwise>
                                                </c:choose>
                                                </div>
                                                <div class="active">
                                                <c:choose>
                                                    <c:when test="${sub.active == true}">
                                                        <input type="checkbox" name="selectedDataSources"
                                                            value="${sub.dataSourceName}" checked/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="checkbox" name="selectedDataSources"
                                                            value="${sub.dataSourceName}"/>
                                                    </c:otherwise>
                                                </c:choose>
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
                        <c:otherwise>
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-alert.png"
                                         alt="no_radars"/>
                                </div>
                                <div class="text">
                                    List of subscribed data sources is currently empty.
                                    Use node connection functionality to add new data sources.
                                </div>
                            </div>
                            <div class="footer">
                                <div class="right">
                                    <form action="connectToNode.htm">
                                        <button class="rounded" type="submit">
                                            <span>OK</span>
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


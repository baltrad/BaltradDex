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
Document   : Page shows subscriptions made by peers
Created on : Nov 16, 2010, 12:08 PM
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
        <title>Baltrad | Remove subscription</title>
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
                            Remove peer's subscription
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <c:choose>
                        <c:when test="${av_subs_status == 1}">
                            <div id="text-box">
                                Select peer's subscription to be removed.
                            </div>
                                <form action="showSelectedPeersSubscriptions.htm">
                                    <div id="table">
                                        <div id="peersubscriptions">
                                            <div class="table-hdr">
                                                <div class="station">
                                                    Radar station
                                                </div>
                                                <div class="user">
                                                    User name
                                                </div>
                                                <div class="check">
                                                    Select
                                                </div>
                                            </div>
                                            <c:forEach var="sub" items="${subscriptions}">
                                                <div class="table-row">
                                                    <div class="station">
                                                        <c:out value="${sub.channelName}"/>
                                                    </div>
                                                    <div class="user">
                                                        <c:out value="${sub.userName}"/>
                                                    </div>
                                                    <div class="check">
                                                        <input type="checkbox"
                                                            name="selected_channels"
                                                            value="${sub.channelName}"/>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                            <div class="footer">
                                                <div class="right">
                                                    <button class="rounded" type="button"
                                                        onclick="window.location='configuration.htm'">
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
                        </c:when>
                        <c:otherwise>
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-alert.png"
                                         alt="no_radars"/>
                                </div>
                                <div class="text">
                                    No peer's subscriptions have been found.
                                </div>
                            </div>
                            <div class="footer">
                                <div class="right">
                                    <form action="configuration.htm">
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

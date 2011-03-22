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
Document   : Local radar stations
Created on : Sep 24, 2010, 13:50 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="java.util.List" %>
<%
    List channels = ( List )request.getAttribute( "channels" );
    if( channels == null || channels.size() <= 0 ) {
        request.getSession().setAttribute( "channels_status", 0 );
    } else {
        request.getSession().setAttribute( "channels_status", 1 );
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Radars</title>
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
                            Radars
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <c:choose>
                        <c:when test="${channels_status == 1}">
                            <div id="text-box">
                                List of local radar stations. Click on the station name in order
                                to view data set available for this station.
                            </div>
                            <div id="table">
                                <div id="radartable">
                                    <div class="table-hdr">
                                        <div class="station">
                                            Radar station
                                        </div>
                                        <div class="wmo">
                                            WMO number
                                        </div>
                                    </div>
                                    <c:forEach var="channel" items="${channels}">
                                        <div class="table-row">
                                            <div class="station">
                                                <a href="radarData.htm?channelName=${channel.channelName}">
                                                    <c:out value="${channel.channelName}"/>
                                                </a>
                                            </div>
                                            <div class="wmo">
                                                <c:out value="${channel.wmoNumber}"/>
                                            </div>
                                        </div>
                                    </c:forEach>
                                    <div class="footer">
                                        <div class="right">
                                            <form action="home.htm">
                                                <button class="rounded" type="submit">
                                                    <span>Home</span>
                                                </button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-alert.png"
                                         alt="no_radars"/>
                                </div>
                                <div class="text">
                                    List of radar stations is currently empty.
                                    Use configuration options to add new radars.
                                </div>
                            </div>
                            <div class="footer">
                                <div class="right">
                                    <form action="home.htm">
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
            <script type="text/javascript" src="includes/footer.js"></script>
        </div>
    </body>
</html>
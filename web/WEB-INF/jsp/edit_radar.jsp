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
Document   : Edit local radar station
Created on : Oct 5, 2010, 11:49 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@ page import="java.util.List" %>
<%
    List channels = ( List )request.getAttribute( "registered_channels" );
    if( channels == null || channels.size() <= 0 ) {
        request.getSession().setAttribute( "channels_status", 0 );
    } else {
        request.getSession().setAttribute( "channels_status", 1 );
    }
%>

<t:page_tabbed pageTitle="Edit radar station" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Edit radar station
            </div>
            <c:choose>
                <c:when test="${channels_status == 1}">
                    <div class="blttext">
                        List of local radar stations. Click on station name in order to
                        modify radar settings.
                    </div>
                    <div class="table">
                        <div class="editradar">
                            <div class="tableheader">
                                <div id="cell" class="count">&nbsp;</div>
                                <div id="cell" class="station">
                                    Name
                                </div>
                                <div id="cell" class="wmonumber">
                                    WMO number
                                </div>
                            </div>
                            <c:set var="count" scope="page" value="1"/>
                            <c:forEach var="channel" items="${registered_channels}">
                                <div class="entry">
                                    <div id="cell" class="count">
                                        <c:out value="${count}"/>
                                        <c:set var="count" value="${count + 1}"/>
                                    </div>
                                    <div id="cell" class="station">
                                        <a href="save_radar.htm?channelId=${channel.id}">
                                            <c:out value="${channel.channelName}"/>
                                        </a>
                                    </div>
                                    <div id="cell" class="wmonumber">
                                        <c:out value="${channel.wmoNumber}"/>
                                    </div>
                                </div>
                            </c:forEach>
                            <div class="tablefooter">
                                <div class="buttons">
                                    <button class="rounded" type="button"
                                        onclick="window.location.href='settings.htm'">
                                        <span>Back</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="blttext">
                        No radar stations have been found.
                        Use add radar functionality in order to define
                        new radar stations.
                    </div>
                    <div class="table">
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                    onclick="window.location.href='save_radar.htm'">
                                    <span>Add</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </jsp:body>
</t:page_tabbed>


<%--html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>BALTRAD | Edit radar station</title>
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
                    
                                                
                                                
                                                
                                                
                </div>
            </div>
        </div>
        <div id="bltfooter">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html--%>
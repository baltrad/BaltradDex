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
Document   : Remove local radar station
Created on : Oct 5, 2010, 11:49 AM
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

<t:page_tabbed pageTitle="Remove radar station" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Remove radar station
            </div>
            <c:choose>
                <c:when test="${channels_status == 1}">
                    <div class="blttext">
                        Select radar stations to remove.
                    </div>
                    <div class="table">
                        <div class="removeradar">
                            <form action="radar_to_remove.htm">
                                <div class="tableheader">
                                    <div id="cell" class="count">&nbsp;</div>
                                    <div id="cell" class="station">
                                        Name
                                    </div>
                                    <div id="cell" class="wmonumber">
                                        WMO number
                                    </div>
                                    <div class="check">
                                        Select
                                    </div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach var="channel" items="${channels}">
                                    <div class="entry">
                                        <div id="cell" class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
                                        </div>
                                        <div id="cell" class="station">
                                            <c:out value="${channel.channelName}"/>
                                        </div>
                                        <div id="cell" class="wmonumber">
                                            <c:out value="${channel.wmoNumber}"/>
                                        </div>
                                        <div class="check">
                                            <input type="checkbox" name="selected_channels"
                                                value="${channel.id}"/>
                                        </div>
                                    </div>
                                </c:forEach>
                                <div class="tablefooter">
                                    <div class="buttons">
                                        <button class="rounded" type="button"
                                            onclick="window.location.href='settings.htm'">
                                            <span>Back</span>
                                        </button>
                                        <button class="rounded" type="submit">
                                            <span>OK</span>
                                        </button>
                                    </div>
                                </div>
                            </form>
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

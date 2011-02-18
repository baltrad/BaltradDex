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
Document   : Page displaying data from selected channel
Created on : Sep 24, 2010, 13:51 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@ page import="java.util.List" %>
<%@ page import="eu.baltrad.dex.bltdata.model.BltFile" %>

<%
    // get channel name
    String name = request.getParameter( "channelName" );
    // Check if list of available subscriptions is not empty
    List radarData = ( List )request.getAttribute( "data_from_radar" );
    if( radarData == null || radarData.size() <= 0 ) {
        request.getSession().setAttribute( "data_status", 0 );
    } else {
        request.getSession().setAttribute( "data_status", 1 );
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Browse data</title>
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
                            Data from <%= name %>
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <c:choose>
                        <c:when test="${data_status == 1}">
                            <div id="text-box">
                                Data files available for radar station <%= name %>.
                                Click on file name to download selected data file.
                            </div>
                            <div id="table">
                                <display:table name="data_from_radar" id="data" defaultsort="0"
                                    requestURI="radarData.htm" cellpadding="0" cellspacing="2"
                                    export="false" class="tableborder" pagesize="10">
                                    <display:column sortable="false" title="Date" paramId="timeStamp"
                                        paramProperty="timeStamp" class="tdcenter"
                                        value="${data.timeStamp}" format="{0,date,yyyy-MM-dd}">
                                    </display:column>
                                    <display:column sortable="false" title="Time" paramId="timeStamp"
                                        paramProperty="timeStamp" class="tdcenter"
                                        value="${data.timeStamp}" format="{0,date,HH:mm:ss}">
                                    </display:column>
                                    <display:column sortProperty="object" sortable="true"
                                        title="Data type" class="tdcenter" value="${data.type}">
                                    </display:column>
                                    <display:column sortable="false" title="Details"
                                        paramId="uuid" paramProperty="uuid"
                                        class="tdcheck" href="fileDetails.htm" value="View">
                                    </display:column>
                                    <display:column href="download.htm" paramId="path"
                                        paramProperty="path" class="tdimage"
                                        sortable="false" title="Download">
                                        <img src="${data.thumbPath}" alt="Download">
                                    </display:column>
                                </display:table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-alert.png"
                                         alt="no_data"/>
                                </div>
                                <div class="text">
                                    No data files found for the selected radar station.
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <div class="footer">
                        <div class="right">
                            <form action="radars.htm">
                                <button class="rounded" type="button"
                                    onclick="history.go(-1);">
                                    <span>Back</span>
                                </button>
                            </form>
                        </div>
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





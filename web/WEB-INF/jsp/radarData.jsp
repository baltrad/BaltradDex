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
Document   : Page displaying data from selected channel
Created on : Sep 24, 2010, 13:51 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="java.util.List" %>
<%@page import="eu.baltrad.dex.bltdata.model.BltFile" %>
<%@page import="eu.baltrad.dex.bltdata.model.BltFileManager" %>
<%@page import="eu.baltrad.dex.bltdata.controller.BltRadarDataController"%>

<%
    // determine channel name
    String name = null;
    if( request.getParameter( "channelName" ) != null ) {
        name = request.getParameter( "channelName" );
    } else {
        name = BltRadarDataController.getChannelName();
    }
    // Check if list of available subscriptions is not empty
    List radarData = ( List )request.getAttribute( "file_entries" );
    if( radarData == null || radarData.size() <= 0 ) {
        request.getSession().setAttribute( "data_status", 0 );
    } else {
        request.getSession().setAttribute( "data_status", 1 );
    }
    BltFileManager manager = new BltFileManager();
    BltRadarDataController controller = new BltRadarDataController();
    long numEntries = manager.countEntries( BltRadarDataController.getChannelName() );
    int numPages = ( int )Math.ceil( numEntries / BltFileManager.ENTRIES_PER_PAGE );
    if( ( numPages * BltFileManager.ENTRIES_PER_PAGE ) < numEntries ) {
        ++numPages;
    }
    if( numPages < 1 ) {
        numPages = 1;
    }
    int currentPage = controller.getCurrentPage();
    int scrollStart = ( BltFileManager.SCROLL_RANGE - 1 ) / 2;
    int firstPage = 1;
    int lastPage = BltFileManager.SCROLL_RANGE;
    if( numPages <= BltFileManager.SCROLL_RANGE && currentPage <= BltFileManager.SCROLL_RANGE ) {
        firstPage = 1;
        lastPage = numPages;
    }
    if( numPages > BltFileManager.SCROLL_RANGE && currentPage > scrollStart &&
            currentPage < numPages - scrollStart ) {
        firstPage = currentPage - scrollStart;
        lastPage = currentPage + scrollStart;
    }
    if( numPages > BltFileManager.SCROLL_RANGE && currentPage > scrollStart &&
            currentPage >= numPages - ( BltFileManager.SCROLL_RANGE - 1 ) ) {
        firstPage = numPages - ( BltFileManager.SCROLL_RANGE - 1 );
        lastPage = numPages;
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
                                <div id="table-control">
                                <c:set var="curPage" scope="page" value="<%=currentPage%>"/>
                                    <form action="radarData.htm" method="post">
                                        <input type="submit" name="pagenum" value="<<">
                                        <span></span>
                                        <input type="submit" name="pagenum" value="<">
                                        <span></span>
                                        <c:forEach var="i" begin="<%=firstPage%>" end="<%=lastPage%>"
                                                   step="1" varStatus ="status">
                                                <c:choose>
                                                    <c:when test="${curPage == i}">
                                                        <input style="background:#FFFFFF" type="submit"
                                                               name="pagenum" value="${i}">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="submit" name="pagenum" value="${i}">
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        <span></span>
                                        <input type="submit" name="pagenum" value=">">
                                        <span></span>
                                        <input type="submit" name="pagenum" value=">>">
                                    </form>
                                </div>
                                <div id="producttable">
                                    <div class="table-hdr">
                                        <div class="date">
                                            Date
                                        </div>
                                        <div class="time">
                                            Time
                                        </div>
                                        <div class="type">
                                            Type
                                        </div>
                                        <div class="details">
                                            Details
                                        </div>
                                        <div class="download">
                                            Download
                                        </div>
                                    </div>
                                    <c:forEach var="entry" items="${file_entries}">
                                        <div class="table-row">
                                            <div class="date">
                                                <fmt:formatDate pattern="yyyy-MM-dd"
                                                    value="${entry.timeStamp}"/>
                                            </div>
                                            <div class="date">
                                                <fmt:formatDate pattern="HH:mm:ss"
                                                    value="${entry.timeStamp}"/>
                                            </div>
                                            <div class="type">
                                                <c:out value="${entry.type}"></c:out>
                                            </div>
                                            <div class="details">
                                                <a href="fileDetails.htm?uuid=${entry.uuid}">
                                                    <c:out value="Show"/>
                                                </a>
                                            </div>
                                            <div class="download">
                                                <a href="download.htm?path=${entry.path}">
                                                    <img src="${entry.thumbPath}" alt="Download"/>
                                                </a>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
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
                                <button class="rounded" type="submit">
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
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>





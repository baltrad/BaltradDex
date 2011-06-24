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
Document   : Displays data from selected data source
Created on : Sep 24, 2010, 13:51 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="java.util.List" %>
<%@page import="eu.baltrad.dex.bltdata.model.BltFile" %>
<%@page import="eu.baltrad.dex.bltdata.model.BltFileManager" %>
<%@page import="eu.baltrad.dex.bltdata.controller.BltDataSourceController"%>

<%
    // determine channel name
    String name = null;
    if( request.getParameter( "dsName" ) != null ) {
        name = request.getParameter( "dsName" );
    } else {
        name = BltDataSourceController.getDSName();
    }
    // Check if list of available subscriptions is not empty
    List radarData = ( List )request.getAttribute( "fileEntries" );
    if( radarData == null || radarData.size() <= 0 ) {
        request.getSession().setAttribute( "dataStatus", 0 );
    } else {
        request.getSession().setAttribute( "dataStatus", 1 );
    }
    long numEntries = BltDataSourceController.getBltFileManager().countEntries(
            BltDataSourceController.getDSName() );
    int numPages = ( int )Math.ceil( numEntries / BltFileManager.ENTRIES_PER_PAGE );
    if( ( numPages * BltFileManager.ENTRIES_PER_PAGE ) < numEntries ) {
        ++numPages;
    }
    if( numPages < 1 ) {
        numPages = 1;
    }
    int currentPage = BltDataSourceController.getCurPage();
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
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/homeTab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/homeMenu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            Data from <%= name %>
                        </div>
                        <c:choose>
                            <c:when test="${dataStatus == 1}">
                                <div class="blttext">
                                    Data files available for data source <%= name %>.
                                </div>
                                <div class="table">
                                    <div class="dsfiles">
                                        <div id="tablecontrol">
                                            <c:set var="curPage" scope="page" value="<%=currentPage%>"/>
                                            <form action="dsFiles.htm" method="post">
                                                <input type="submit" name="pagenum" value="<<"
                                                       title="First page">
                                                <span></span>
                                                <input type="submit" name="pagenum" value="<"
                                                       title="Previous page">
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
                                                <input type="submit" name="pagenum" value=">"
                                                       title="Next page">
                                                <span></span>
                                                <input type="submit" name="pagenum" value=">>"
                                                       title="Last page">
                                            </form>
                                        </div>
                                        <div class="tableheader">
                                            <div id="cell" class="date">
                                                Date
                                            </div>
                                            <div id="cell" class="time">
                                                Time
                                            </div>
                                            <div id="cell" class="source">
                                                Source
                                            </div>
                                            <div id="cell" class="type">
                                                Type
                                            </div>
                                            <div id="cell" class="details">&nbsp;</div>
                                            <div id="cell" class="download">&nbsp;</div>
                                        </div>
                                        <c:forEach var="entry" items="${fileEntries}">
                                            <div class="entry">
                                                <div id="cell" class="date">
                                                    <fmt:formatDate pattern="yyyy-MM-dd"
                                                        value="${entry.timeStamp}"/>
                                                </div>
                                                <div id="cell" class="time">
                                                    <fmt:formatDate pattern="HH:mm:ss"
                                                        value="${entry.timeStamp}"/>
                                                </div>
                                                <div id="cell" class="source">
                                                    <c:out value="${entry.source}"></c:out>
                                                </div>
                                                <div id="cell" class="type">
                                                    <c:out value="${entry.type}"></c:out>
                                                </div>
                                                <div id="cell" class="details">
                                                    <a href="fileDetails.htm?uuid=${entry.uuid}">
                                                        <c:out value="Details"/>
                                                    </a>
                                                </div>
                                                <div id="cell" class="download">
                                                    <a href="download.htm?path=${entry.path}">
                                                        <c:out value="Download"/>
                                                    </a>
                                                </div>
                                            </div>
                                        </c:forEach>
                                        <div class="tablefooter">
                                            <div class="buttons">
                                                <button class="rounded" type="button"
                                                    onclick="window.location.href='dsShow.htm'">
                                                    <span>Back</span>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="blttext">
                                    No files found for the selected data source.
                                </div>
                                <div class="table">
                                    <div class="tablefooter">
                                        <div class="buttons">
                                            <button class="rounded" type="button"
                                                onclick="window.location.href='dsShow.htm'">
                                                <span>OK</span>
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
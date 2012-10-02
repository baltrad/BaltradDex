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
Document   : Home page
Created on : Aug 8, 2010, 2:13 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="java.util.List" %>
<%@page import="eu.baltrad.dex.db.model.BltFile" %>
<%@page import="eu.baltrad.dex.db.model.BltFileManager" %>
<%@page import="eu.baltrad.dex.db.controller.BltFileBrowserController" %>

<%
    // Check if file's list is not empty
    List fileEntries = ( List )request.getAttribute( "fileEntries" );
    if( fileEntries == null || fileEntries.size() <= 0 ) {
        request.getSession().setAttribute( "dataStatus", 0 );
    } else {
        request.getSession().setAttribute( "dataStatus", 1 );
    }
    long numEntries = BltFileBrowserController.getNumEntries();
    int numPages = ( int )Math.ceil( numEntries / BltFileManager.ENTRIES_PER_PAGE );
    if( ( numPages * BltFileManager.ENTRIES_PER_PAGE ) < numEntries ) {
        ++numPages;
    }
    if( numPages < 1 ) {
        numPages = 1;
    }
    int currentPage = BltFileBrowserController.getCurPage();
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
    request.getSession().setAttribute( "firstPage", firstPage );
    request.getSession().setAttribute( "lastPage", lastPage );
    request.getSession().setAttribute( "curPage", currentPage );
    request.getSession().setAttribute( "radarStation", BltFileBrowserController.getRadarStation() );
    request.getSession().setAttribute( "fileObject", BltFileBrowserController.getFileObject() );
    request.getSession().setAttribute( "startDate", BltFileBrowserController.getStartDate() );
    request.getSession().setAttribute( "endDate", BltFileBrowserController.getEndDate() );
    request.getSession().setAttribute( "startHour", BltFileBrowserController.getStartHour() );
    request.getSession().setAttribute( "startMinute", BltFileBrowserController.getStartMinute() );
    request.getSession().setAttribute( "startSecond", BltFileBrowserController.getStartSecond() );
    request.getSession().setAttribute( "endHour", BltFileBrowserController.getEndHour() );
    request.getSession().setAttribute( "endMinute", BltFileBrowserController.getEndMinute() );
    request.getSession().setAttribute( "endSecond", BltFileBrowserController.getEndSecond() );
%>

<t:page_tabbed pageTitle="Browse files" activeTab="home">
  <jsp:body>
    <div class="left">
        <t:menu_home/>
    </div>
    <div class="right">
        <form method="post" name="fileBrowser">
            <div class="blttitle">
                Browse files
            </div>
            <div class="blttext">
                Use the following options to search and browse data files.
            </div>
            <div class="filebrowser">
                <div class="row">
                    <div class="leftcol">
                        <select name="radarsList" title="Select source radar station name">
                            <option value="select">
                                <c:out value="-- Select radar --"/>
                            </option>
                            <c:forEach items="${radar_stations}" var="station">
                                 <option value="${station}" <c:if test="${station == radarStation}">
                                        SELECTED</c:if>>
                                    <c:out value="${station}"/>
                                </option>
                            </c:forEach>
                        </select>
                        <div class="hint">
                            Radar station name
                        </div>
                    </div>
                    <div class="midcol">
                        <select name="fileObjectsList" title="Select type of file object">
                            <option value="select">
                                <c:out value="-- Select file object --"/>
                            </option>
                            <c:forEach items="${file_objects}" var="fobject">
                                <option value="${fobject.fileObject}"
                                    <c:if test="${fobject.fileObject == fileObject}">
                                        SELECTED</c:if>>
                                    <c:out value="${fobject.description}"/>
                                </option>
                            </c:forEach>
                        </select>
                        <div class="hint">
                            File object type
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="leftcol">
                        <input type="text" name="startDate" id="startDate"
                               title="Start date of dataset's timespan" value="${startDate}">
                        <img src="includes/images/cal.gif" onclick="javascript:NewCssCal(
                            'startDate','yyyyMMdd','arrow',false,'24',false)" style="cursor:pointer"/>
                        <div class="hint">
                            Start date
                        </div>
                    </div>
                    <div class="midcol">
                        <input type="text" name="startHour" id="start_hour"
                               title="Start hour (24 hour format)"
                               value="${startHour}" 
                               onchange="validateHour('start_hour')">: 
                        <input type="text" name="startMinute" 
                               id="start_minutes" title="Start minutes"
                               value="${startMinute}" 
                               onchange="validateMinSec('start_minutes')">: 
                        <input type="text" name="startSecond" 
                               id="start_seconds" title="Start seconds"
                               value="${startSecond}" 
                               onchange="validateMinSec('start_seconds')">
                        <div class="hint">
                            Start time
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="leftcol">
                        <input type="text" name="endDate" id="endDate"
                               title="End date of dataset's timespan" value="${endDate}">
                        <img src="includes/images/cal.gif" onclick="javascript:NewCssCal(
                            'endDate','yyyyMMdd','arrow',false,'24',false)" style="cursor:pointer"/>
                        <div class="hint">
                            End date
                        </div>
                    </div>
                    <div class="midcol">
                        <input type="text" name="endHour" id="end_hour"
                               title="End hour (24 hour format)"
                               value="${endHour}" 
                               onchange="validateHour('end_hour')">: 
                        <input type="text" name="endMinute" id="end_minutes"
                               title="End minutes" value="${endMinute}" 
                               onchange="validateMinSec('end_minutes')">: 
                        <input type="text" name="endSecond" id="end_seconds"
                               title="End seconds" value="${endSecond}" 
                               onchange="validateMinSec('end_seconds')">
                        <div class="hint">
                            End time
                        </div>
                    </div>
                    <div class="rightcol">
                        <div class="buttons">
                            <button class="rounded" type="submit">
                                <span>Select</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="bltseparator"></div>
                <c:choose>
                    <c:when test="${dataStatus == 1}">
                        <div class="table">
                            <div class="dsfiles">
                                <div id="tablecontrol">
                                    <input type="submit" name="pagenum" value="<<"
                                           title="First page">
                                    <span></span>
                                    <input type="submit" name="pagenum" value="<"
                                           title="Previous page">
                                    <span></span>
                                    <c:forEach var="i" begin="${firstPage}" end="${lastPage}"
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
                                </div>
                                <div class="tableheader">
                                    <div id="cell" class="date">
                                        <input name="sortByDate" type="submit" value="Date"
                                               title="Sort result set by date">
                                    </div>
                                    <div id="cell" class="time">
                                        <input name="sortByTime" type="submit" value="Time"
                                               title="Sort result set by time">
                                    </div>
                                    <div id="cell" class="source">
                                        <input name="sortBySource" type="submit" value="Source"
                                               title="Sort result set by data source">
                                    </div>
                                    <div id="cell" class="type">
                                        <input name="sortByType" type="submit" value="Type"
                                               title="Sort result set by file object type">
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
                                            <a href="file_details.htm?uuid=${entry.uuid}">
                                                <c:out value="Details"/>
                                            </a>
                                        </div>
                                        <div id="cell" class="download">
                                            <a href="download.htm?uuid=${entry.uuid}">
                                                <c:out value="Download"/>
                                            </a>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="blttext">
                            Result set is empty.
                        </div>
                    </c:otherwise>
                </c:choose>
            </form>
        </div>
    </jsp:body>
</t:page_tabbed>

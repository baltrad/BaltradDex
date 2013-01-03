<%------------------------------------------------------------------------------
Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW

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
--------------------------------------------------------------------------------
Document   : Browse files
Created on : Aug 8, 2010, 2:13 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Browse files" activeTab="home">
  <jsp:body>
    <div class="left">
        <t:menu_home/>
    </div>
    <div class="right">
        <div class="blttitle">
            Browse files
        </div>
        <c:choose>
            <c:when test="${not empty file_catalog_error}">
                <div class="table">
                    <div class="systemerror">
                        <div class="header">
                            Problems encountered.
                        </div>
                        <div class="message">
                            <c:out value="${file_catalog_error}"/>
                            <c:set var="error" value="" scope="session" />
                        </div>
                    </div>
                </div>        
            </c:when>
            <c:otherwise>
                <div class="blttext">
                    Use the following options to search and browse data files.
                </div>
                <form:form method="POST" commandName="query_param">
                    <div class="filebrowser">
                        <div class="row">
                            <div class="leftcol">
                                <form:select path="radar" 
                                             title="Select source radar">
                                    <form:option value="" 
                                                 label="-- Select radar --"/>
                                    <form:options items="${radars}"/>
                                </form:select>  
                                <div class="hint">
                                    Source radar station 
                                </div>
                            </div>
                            <div class="midcol">
                                <form:select path="fileObject"
                                             title="Select file object type">
                                    <form:option value="" 
                                                 label="-- Select file object --"/>
                                    <form:options items="${file_objects}" 
                                                  itemValue="name"
                                                  itemLabel="description"/>
                                </form:select>  
                                <div class="hint">
                                    File object type 
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                <form:input path="startDate" id="startDate"
                                            title="Start date of dataset's timespan"/>
                                <img src="includes/images/cal.gif" 
                                     onclick="javascript:NewCssCal('startDate',
                                         'yyyyMMdd','arrow',false,'24',false)" 
                                     style="cursor:pointer"/>
                                <div class="hint">
                                    Start date
                                </div>
                            </div>
                            <div class="midcol">
                                <form:input path="startHour" id="start_hour"
                                            title="Start hour (24 hour format)" 
                                            onchange="validateHour('start_hour')"/>: 
                                <form:input path="startMinute" 
                                            id="start_minutes" title="Start minutes"
                                            onchange="validateMinSec('start_minutes')"/>: 
                                <form:input path="startSecond" 
                                            id="start_seconds" title="Start seconds" 
                                            onchange="validateMinSec('start_seconds')"/>
                                <div class="hint">
                                    Start time
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                <form:input path="endDate" id="endDate"
                                    title="End date of dataset's timespan"/>
                                <img src="includes/images/cal.gif" 
                                     onclick="javascript:NewCssCal('endDate',
                                         'yyyyMMdd','arrow',false,'24',false)" 
                                     style="cursor:pointer"/>
                                <div class="hint">
                                    End date
                                </div>
                            </div>
                            <div class="midcol">
                                <form:input path="endHour" id="end_hour"
                                            title="End hour (24 hour format)" 
                                            onchange="validateHour('end_hour')"/>: 
                                <form:input path="endMinute" id="end_minutes"
                                            title="End minutes"
                                            onchange="validateMinSec('end_minutes')"/>: 
                                <form:input path="endSecond" id="end_seconds"
                                            title="End seconds"
                                            onchange="validateMinSec('end_seconds')"/>
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
                        <c:when test="${not empty file_entries}">
                            <div class="table">
                                <div class="dsfiles">
                                    <div id="tablecontrol">
                                        <input type="submit" name="selected_page" value="<<"
                                               title="First page">
                                        <span></span>
                                        <input type="submit" name="selected_page" value="<"
                                               title="Previous page">
                                        <span></span>
                                        <c:forEach var="i" begin="${first_page}" end="${last_page}"
                                            step="1" varStatus ="status">
                                            <c:choose>
                                                <c:when test="${current_page == i}">
                                                    <input style="background:#FFFFFF" type="submit"
                                                           name="selected_page" value="${i}">
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="submit" name="selected_page" value="${i}">
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                        <span></span>
                                        <input type="submit" name="selected_page" value=">"
                                               title="Next page">
                                        <span></span>
                                        <input type="submit" name="selected_page" value=">>"
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
                                            <input name="sortByObject" type="submit" value="Type"
                                                   title="Sort result set by file object type">
                                        </div>
                                        <div id="cell" class="details">&nbsp;</div>
                                        <div id="cell" class="download">&nbsp;</div>
                                    </div>
                                    <c:forEach var="entry" items="${file_entries}">
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
                </form:form>                    
            </c:otherwise>            
        </c:choose>                    
        </div>
    </jsp:body>
</t:page_tabbed>

<%------------------------------------------------------------------------------
Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW

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
Document   : File browser
Created on : May 10, 2013, 1:35 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Browse files">
    <jsp:body>
        <div class="file-browser">
            <div class="table">
                <div class="header">
                    <div class="row">
                        Browse data files
                    </div>
                </div>
                <c:choose>
                    <c:when test="${not empty fc_error}">
                        <div id="message-box">
                            <t:message_box errorHeader="Problems encountered"
                                           errorBody="${fc_error}"/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="header-text">
                            Use parameters below to define search criteria 
                            and browse files.
                        </div>
                        <form:form method="POST" commandName="query_param">
                            <div class="row">
                                <div class="leftcol">
                                    <div class="label">
                                        Radar station
                                    </div>
                                    <form:select path="radar" 
                                                 title="Select radar station">
                                        <form:option value="" label="..."/>
                                        <form:options items="${radars}"/>
                                    </form:select>  
                                </div>
                                <div class="rightcol">
                                    <div class="label">
                                        File object
                                    </div>
                                    <form:select path="fileObject" 
                                                 title="Select file object type">
                                        <form:option value="" label="..."/>
                                        <form:options items="${file_objects}" 
                                                  itemValue="name"
                                                  itemLabel="description"/>
                                    </form:select>   
                                </div>    
                            </div>
                            <div class="row">
                                <div class="leftcol">
                                    <div class="label">
                                        Start date
                                    </div>
                                    <form:input path="startDate" id="start_date"
                                            title="Start date of dataset timespan" 
                                            readonly="true"/>
                                    <img src="includes/images/cal.gif" 
                                         onclick="javascript:NewCssCal(
                                         'start_date','yyyyMMdd','arrow',false,'24',false)" 
                                         style="cursor:pointer"/>
                                </div>
                                <div class="rightcol">
                                    <div class="label">
                                        Start time
                                    </div>
                                    <form:input path="startHour" id="start_hour"
                                                title="Start hour (24 hour format)"
                                                onchange="validateHour('start_hour')"/>
                                    <div id="separator">:</div> 
                                    <form:input path="startMinute" id="start_minute"
                                                title="Start minutes"
                                                onchange="validateMinSec('start_minute')"/>
                                    <div id="separator">:</div> 
                                    <form:input path="startSecond" id="start_second"
                                                title="Start seconds"
                                                onchange="validateMinSec('start_second')"/>
                                </div>
                            </div>                           
                            <div class="row">  
                                <div class="leftcol">
                                    <div class="label">
                                        End date
                                    </div>
                                    <form:input path="endDate" id="end_date"
                                            title="End date of dataset timespan"
                                            readonly="true"/>
                                    <img src="includes/images/cal.gif" 
                                         onclick="javascript:NewCssCal(
                                         'end_date','yyyyMMdd','arrow',false,'24',false)" 
                                         style="cursor:pointer"/>
                                </div>
                                <div class="rightcol">
                                    <div class="label">
                                        End time
                                    </div>
                                    <form:input path="endHour" id="end_hour" 
                                                title="End hour (24 hour format)"
                                                onchange="validateHour('end_hour')"/>
                                    <div id="separator">:</div>
                                    <form:input path="endMinute" id="end_minute"
                                                title="End minutes" 
                                                onchange="validateMinSec('end_minute')"/>
                                    <div id="separator">:</div>
                                    <form:input path="endSecond" id="end_second"
                                                title="End seconds"
                                                onchange="validateMinSec('end_second')"/>
                                </div>
                            </div>    
                            <div class="row">
                                <div class="buttons">
                                    <div class="button-wrap">
                                        <input class="button" type="reset" 
                                               value="Clear"></input>
                                    </div>
                                    <div class="button-wrap">
                                        <input class="button" type="submit" 
                                               value="Search"></input>
                                    </div>
                                </div>
                            </div>                    
                            <c:choose>
                                <c:when test="${not empty files}">
                                    <div id="scroll">
                                        <div class="leftcol">
                                            <input type="submit" name="selected_page" value="<<"
                                                   title="First page">
                                            <input type="submit" name="selected_page" value="<"
                                                   title="Previous page">
                                        </div>
                                        <div class="midcol">
                                            <c:forEach var="i" begin="${first_page}" 
                                                       end="${last_page}" step="1" 
                                                       varStatus ="status">
                                                <c:choose>
                                                    <c:when test="${current_page == i}">
                                                        <input style="font-weight: bold;
                                                                      font-size: 14px;" 
                                                               type="submit" 
                                                               name="selected_page" 
                                                               value="${i}">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="submit" 
                                                               name="selected_page" 
                                                               value="${i}">
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </div>
                                        <div class="rightcol">
                                            <input type="submit" name="selected_page" value=">"
                                                   title="Next page">
                                            <input type="submit" name="selected_page" value=">>"
                                                   title="Last page">
                                        </div>
                                    </div>
                                    <div class="body">    
                                        <div class="header-row">
                                            <div class="count">&nbsp;</div>
                                            <div class="date">
                                                <input name="sortByDate" type="submit" 
                                                       value="Date" 
                                                       title="Sort result set by date">                                        
                                            </div>
                                            <div class="time">
                                                <input name="sortByTime" type="submit" 
                                                       value="Time"
                                                       title="Sort result set by time">
                                            </div>
                                            <div class="source">
                                                <input name="sortBySource" type="submit" 
                                                       value="Source"
                                                       title="Sort result set by data source">
                                            </div>
                                            <div class="type">
                                                <input name="sortByObject" type="submit" 
                                                       value="Type"
                                                       title="Sort result set by file object type">
                                            </div>
                                            <div class="details">&nbsp;</div>
                                            <div class="download">&nbsp;</div>
                                        </div>
                                        <c:set var="count" scope="page" value="1"/>
                                        <c:forEach var="file" items="${files}">
                                            <div class="row">
                                                <div class="count">
                                                    <c:out value="${count}"/>
                                                    <c:set var="count" value="${count + 1}"/>
                                                </div>
                                                <div class="date">
                                                    <fmt:formatDate pattern="yyyy-MM-dd"
                                                        value="${file.timeStamp}"/>
                                                </div>
                                                <div class="time">
                                                    <fmt:formatDate pattern="HH:mm:ss"
                                                        value="${file.timeStamp}"/>
                                                </div>
                                                <div class="source">
                                                    <span title="${file.source}">
                                                        <c:out value="${file.source}"/>
                                                    </span>
                                                </div>
                                                <div class="type">
                                                    <c:out value="${file.type}"/>
                                                </div>
                                                <div class="details">
                                                    <a href="file_details.htm?uuid=${file.uuid}">
                                                        <c:out value="Details"/>
                                                    </a>
                                                </div>
                                                <div class="download">
                                                    <a href="file_download.htm?uuid=${file.uuid}">
                                                        <c:out value="Download"/>
                                                    </a>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when> 
                                <c:otherwise>
                                    No matching data files found.
                                </c:otherwise>
                            </c:choose>                           
                        </form:form>                                                                               
                    </c:otherwise>
                </c:choose>
            </div>                      
        </div>                                    
    </jsp:body>
</t:generic_page>

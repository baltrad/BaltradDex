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
Document   : All system messages
Created on : Sep 30, 2010, 14:45 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="System messages" activeTab="home">
  <jsp:body>
    <div class="left">
        <t:menu_home/>
    </div>
    <div class="right">
        <div class="blttitle">
            All system messages
        </div>
        <div class="blttext">
            Use options below to search for messages.
        </div>
        <form:form method="POST" commandName="log_parameter">
            <div class="messagebrowser">
                <div class="row">
                    <div class="leftcol">
                        <form:select path="logger" title="Select message logger">
                            <form:option value="ALL" label="ALL"/>
                            <form:options items="${loggers}"/>
                        </form:select>        
                        <div class="hint">
                            Message logger
                        </div>
                    </div>
                    <div class="midcol">
                        <form:select path="flag" title="Select message flag">
                            <form:option value="ALL" label="ALL"/>
                            <form:options items="${flags}"/>
                        </form:select>    
                        <div class="hint">
                            Message flag
                        </div>
                    </div>
                    <div class="leftcol">
                        <form:input path="startDate" 
                                    title="Start date of messages' timespan" 
                                    readonly="true"/>
                        <img src="includes/images/cal.gif" 
                            onclick="javascript:NewCssCal(
                                'startDate','yyyyMMdd','arrow',false,'24',false)" 
                                style="cursor:pointer"/>
                        <div class="hint">
                            Start date
                        </div>
                    </div>
                    <div class="midcol">
                        <form:input path="startHour" id="start_hour"
                                    title="Start hour (24 hour format)"
                                    onchange="validateHour('start_hour')"/>: 
                        <form:input path="startMinutes" id="start_minutes"
                                    title="Start minutes"
                                    onchange="validateMinSec('start_minutes')"/>:
                        <form:input path="startSeconds" id="start_seconds"
                                    title="Start seconds"
                                    onchange="validateMinSec('start_seconds')"/>
                        <div class="hint">
                            Start time
                        </div>
                    </div>  
                </div>                    
                <div class="row">
                    <div class="leftcol">
                        <form:input path="endDate" 
                                    title="End date of messages' timespan"
                                    readonly="true"/>
                        <img src="includes/images/cal.gif" 
                            onclick="javascript:NewCssCal(
                                'endDate','yyyyMMdd','arrow',false,'24',false)" 
                                style="cursor:pointer"/>
                        <div class="hint">
                            End date
                        </div>
                    </div>
                    <div class="midcol">
                        <form:input path="endHour" id="end_hour" 
                                    title="End hour (24 hour format)"
                                    onchange="validateHour('end_hour')"/>:
                        <form:input path="endMinutes" id="end_minutes"
                                    title="End minutes" 
                                    onchange="validateMinSec('end_minutes')"/>: 
                        <form:input path="endSeconds" id="end_seconds"
                                    title="End seconds"
                                    onchange="validateMinSec('end_seconds')"/>
                        <div class="hint">
                            End time
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="phrase">
                        <form:input path="phrase" 
                                    title="Phrase to look for in message body"/>  
                        <div class="hint">
                            Message phrase
                        </div>
                    </div>
                    <div class="submit">
                        <div class="buttons">
                            <button class="rounded" type="button"
                                    onclick="window.location.href='system_messages.htm'">
                                <span>Reset</span>
                            </button>
                            <button class="rounded" type="submit">
                                <span>Select</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="bltseparator"></div>
            <div class="table">
                <div class="log">
                    <c:choose>
                        <c:when test="${not empty log_entries}">
                            <div id="tablecontrol">
                                <c:set var="curPage" scope="page" value="${current_page}"/>                        
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
                                            <input type="submit" name="selected_page" 
                                                value="${i}">
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
                            <div id="logtable">
                                <div class="tableheader">
                                    <div id="cell" class="logdate">
                                        Date
                                    </div>
                                    <div id="cell" class="logtime">
                                        Time
                                    </div>
                                    <div id="cell" class="logflag">
                                        Flag
                                    </div>
                                    <div id="cell" class="logmsg">
                                        Message
                                    </div>
                                </div>
                                <c:forEach var="entry" items="${log_entries}">
                                    <c:choose>
                                        <c:when test="${entry.type == 'INFO'}">
                                            <c:set var="style" value="info-entry" 
                                                scope="page"/>
                                        </c:when>
                                        <c:when test="${entry.type == 'WARN'}">
                                            <c:set var="style" value="warning-entry" 
                                                scope="page"/>
                                        </c:when>
                                        <c:when test="${entry.type == 'ERROR'}">
                                            <c:set var="style" value="error-entry" 
                                                scope="page"/>
                                        </c:when>
                                    </c:choose>
                                    <div class="entry">
                                        <div id="cell" class="logdate">
                                            <div class="${style}">
                                                <c:out value="${fn:substring(entry.timeStamp, 
                                                                0, 10)}"/>
                                            </div>
                                        </div>
                                        <div id="cell" class="logtime">
                                            <div class="${style}">
                                                <c:out value="${fn:substring(entry.timeStamp, 
                                                                10, 19)}"/>
                                            </div>
                                        </div>
                                        <c:choose>
                                            <c:when test="${entry.type == 'ERROR'}">
                                                <div id="cell" class="logflag">
                                                    <img src="includes/images/icons/error.png"
                                                        alt="error"/>
                                                </div>
                                            </c:when>
                                            <c:when test="${entry.type == 'WARN'}">
                                                <div id="cell" class="logflag">
                                                    <img src="includes/images/icons/warning.png"
                                                        alt="warn"/>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div id="cell" class="logflag">
                                                    <img src="includes/images/icons/info.png"
                                                        alt="ok"/>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                        <div id="cell" class="logmsg">
                                            <div class="${style}">
                                                <c:out value="${entry.message}"/>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="blttext">
                                No messages found.
                            </div>
                        </c:otherwise>
                    </c:choose>            
                </div>                                
            </div>              
        </form:form>                                      
    </div>
  </jsp:body>
</t:page_tabbed>

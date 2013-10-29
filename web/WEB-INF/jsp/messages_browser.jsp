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
Document   : System log
Created on : May 6, 2013, 1:28 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Browse messages">
    <jsp:body>
        <div class="system-log">
            <div class="table">
                <div class="header">
                    <div class="row">
                        Browse system messages
                        <a href="messages_live.htm">
                            Show latest
                        </a>
                    </div>
                </div>
                <div class="header-text">
                    Use parameters below to define search criteria and browse
                    messages.
                </div>
                <form:form method="POST" commandName="log_parameter">
                    <div class="row">
                        <div class="leftcol">
                            <div class="label">
                                Message logger
                            </div>
                            <form:select path="logger" 
                                         title="Select message logger">
                                <form:option value="ALL" label="ALL"/>
                                <form:options items="${loggers}"/>
                            </form:select>  
                        </div>
                        <div class="midcol">
                            <div class="label">
                                Start date
                            </div>
                            <form:input path="startDate" id="start_date"
                                    title="Start date of message timespan" 
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
                            <form:input path="startMinutes" id="start_minute"
                                        title="Start minutes"
                                        onchange="validateMinSec('start_minute')"/>
                            <div id="separator">:</div> 
                            <form:input path="startSeconds" id="start_second"
                                        title="Start seconds"
                                        onchange="validateMinSec('start_second')"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            <div class="label">
                                Log level
                            </div>
                            <form:select path="level" 
                                         title="Select log level">
                                <form:option value="ALL" label="ALL"/>
                                <form:options items="${levels}"/>
                            </form:select>    
                        </div>
                        <div class="midcol">
                            <div class="label">
                                End date
                            </div>
                            <form:input path="endDate" id="end_date"
                                    title="End date of message timespan"
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
                            <form:input path="endMinutes" id="end_minute"
                                        title="End minutes" 
                                        onchange="validateMinSec('end_minute')"/>
                            <div id="separator">:</div>
                            <form:input path="endSeconds" id="end_second"
                                        title="End seconds"
                                        onchange="validateMinSec('end_second')"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="label" id="phrase">
                                Phrase
                        </div>    
                        <form:input path="phrase" id="phrase"
                                    title="Phrase to search for in message body"/>
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
                </form:form>  
                <div class="body">
                    <div class="header-row">
                        <div class="date">Date</div>
                        <div class="time">Time</div>
                        <div class="flag">&nbsp;</div>
                        <div class="message">Message</div>
                    </div>
                    <c:choose>
                        <c:when test="${not empty messages}">
                            <c:forEach begin="0" end="19" varStatus="i">
                                <c:choose>
                                    <c:when test="${not empty messages[i.index]}">
                                        <c:set var="msg" value="${messages[i.index]}"></c:set> 
                                        <c:choose>
                                            <c:when test="${msg.level == 'ERROR'}">
                                                <div class="row" id="error">
                                                    <div class="date">
                                                        <fmt:formatDate value="${msg.date}" 
                                                                        pattern="yyyy/dd/MM"/>
                                                    </div>
                                                    <div class="time">
                                                        <fmt:formatDate value="${msg.date}" 
                                                                        pattern="HH:mm:ss"/>  
                                                    </div>
                                                    <div class="flag">
                                                        <img src="includes/images/log-error.png" 
                                                             alt="error"/>
                                                    </div>    
                                                    <div class="message">
                                                        <c:out value="${msg.message}"/>
                                                    </div>
                                                </div>
                                            </c:when>
                                            <c:when test="${msg.level == 'WARN'}">
                                                <div class="row" id="warning">
                                                    <div class="date">
                                                        <fmt:formatDate value="${msg.date}" 
                                                                        pattern="yyyy/dd/MM"/>
                                                    </div>
                                                    <div class="time">
                                                        <fmt:formatDate value="${msg.date}" 
                                                                        pattern="HH:mm:ss"/>   
                                                    </div>
                                                    <div class="flag">
                                                        <img src="includes/images/log-alert.png" 
                                                             alt="error"/>
                                                    </div>                    
                                                    <div class="message">
                                                        <c:out value="${msg.message}"/>
                                                    </div>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="row" id="info">
                                                    <div class="date">
                                                        <fmt:formatDate value="${msg.date}" 
                                                                        pattern="yyyy/dd/MM"/>
                                                    </div>
                                                    <div class="time">
                                                        <fmt:formatDate value="${msg.date}" 
                                                                        pattern="HH:mm:ss"/>   
                                                    </div>
                                                    <div class="flag">
                                                        <img src="includes/images/log-info.png" 
                                                             alt="error"/>
                                                    </div>                    
                                                    <div class="message">
                                                        <c:out value="${msg.message}"/>
                                                    </div>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="row" id="empty"></div> 
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <c:forEach begin="0" end="19" varStatus="i">
                                <c:choose>
                                    <c:when test="${i.index == 0}">
                                        <div class="row" id="empty">
                                            <div class="full-row">
                                                <c:out value="No messages found in system log."/>
                                            </div>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="row" id="empty"></div>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>                        
                </div>        
            </div>
        </div>
    </jsp:body>
</t:generic_page>

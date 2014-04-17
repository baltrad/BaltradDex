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
Create scheduled job
@date 2010-08-23
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="defun" uri="/WEB-INF/tags/functions.tld" %>

<t:generic_page pageTitle="Schedule">
    <jsp:body>
        <div class="schedule">
            <div class="table">
                <div class="header">
                    <div class="row">Create job</div>
                </div>
                <div class="header-text">
                    Create a scheduled job.
                </div>
                <form name="createScheduledJobForm" 
                      action="schedule_create_job.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>
                    <div class="body">
                        <div class="row2">
                            <div class="leftcol">Seconds:</div>
                            <div class="rightcol">
                                <select multiple size="4" name="seconds" 
                                        title="Seconds">
                                    <c:forEach var="entry" items="${selectableSeconds}">
                                        <option value="${entry.value}" <c:if test="${ defun:listContains(seconds, entry.value) }">selected</c:if> >${entry.name}</option>
                                    </c:forEach>
                                </select>
                            </div>        
                        </div>
                        <div class="row2">
                            <div class="leftcol">Minutes:</div>
                            <div class="rightcol">
                                <select multiple size="4" name="minutes"
                                        title="Minutes">
                                    <c:forEach var="entry" items="${selectableMinutes}">
                                        <option value="${entry.value}" <c:if test="${ defun:listContains(minutes, entry.value) }">selected</c:if> >${entry.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Hours:</div>
                            <div class="rightcol">
                                <select multiple size="4" name="hours"
                                        title="Hours">
                                    <c:forEach var="entry" items="${selectableHours}">
                                        <option value="${entry.value}" <c:if test="${ defun:listContains(hours, entry.value) }">selected</c:if> >${entry.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Days of month:</div>
                            <div class="rightcol">
                                <select multiple size="4" name="daysOfMonth"
                                        title="Days of month">
                                    <c:forEach var="entry" items="${selectableDaysOfMonth}">
                                        <option value="${entry.value}" <c:if test="${ defun:listContains(daysOfMonth, entry.value) }">selected</c:if> >${entry.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Months:</div>
                            <div class="rightcol">
                                <select multiple size="4" name="months"
                                        title="Months">
                                    <c:forEach var="entry" items="${selectableMonths}">
                                        <option value="${entry.value}" <c:if test="${ defun:listContains(months, entry.value) }">selected</c:if> >${entry.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Days of week:</div>
                            <div class="rightcol">
                                <select multiple size="4" name="daysOfWeek"
                                        title="Days of week">
                                    <c:forEach var="entry" items="${selectableDaysOfWeek}">
                                        <option value="${entry.value}" <c:if test="${ defun:listContains(daysOfWeek, entry.value) }">selected</c:if> >${entry.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Job name:</div>
                            <div class="rightcol">
                                <select size="4" name="jobname" title="Select a job to execute">
                                    <c:forEach var="job" items="${jobnames}">
                                        <option value="${job}" <c:if test="${job == jobname}">selected</c:if> >${job}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>         
                    </div>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="submit" 
                                       value="Add"/>
                            </div>
                        </div>
                    </div>                      
                </form>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>

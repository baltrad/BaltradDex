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
Create scheduled job
@date 2010-08-23
@author Anders Henja
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Create job" activeTab="processing">
    <jsp:body>
        <div class="left">
            <t:menu_processing/>
        </div>
        <div class="right">
            <div class="blttitle">
                Create job
            </div>
            <div class="blttext">
                Create a scheduled job.
            </div>
            <div class="table">
              <t:error_message message="${emessage}"/>
              <div class="modifyjob">
                <form name="createScheduledJobForm" action="createscheduledjob.htm">
                  <div class="leftcol">
                    <div class="row4">Seconds</div>
                    <div class="row4">Minutes</div>
                    <div class="row4">Hours</div>
                    <div class="row4">Days of month</div>
                    <div class="row4">Months</div>
                    <div class="row4">Days of week</div>
                    <div class="row">Job name</div>                  
                  </div>
                  <div class="rightcol">
                    <div class="row4">
                      <select multiple size="4" name="seconds">
                        <c:forEach var="entry" items="${selectableSeconds}">
                          <option value="${entry.value}" <c:if test="${ fn:contains(seconds, entry.value) }">selected</c:if> >${entry.name}</option>
                        </c:forEach>
                      </select>
                      <div class="hint">
                        Seconds
                      </div>
                    </div>
                    <div class="row4">
                      <select multiple size="4" name="minutes">
                        <c:forEach var="entry" items="${selectableMinutes}">
                          <option value="${entry.value}" <c:if test="${ fn:contains(minutes, entry.value) }">selected</c:if> >${entry.name}</option>
                        </c:forEach>
                      </select>
                      <div class="hint">
                        Minutes
                      </div>
                    </div>
                    <div class="row4">
                      <select multiple size="4" name="hours">
                        <c:forEach var="entry" items="${selectableHours}">
                          <option value="${entry.value}" <c:if test="${ fn:contains(hours, entry.value) }">selected</c:if> >${entry.name}</option>
                        </c:forEach>
                      </select>
                      <div class="hint">
                        Hours
                      </div>
                    </div>
                    <div class="row4">
                      <select multiple size="4" name="daysOfMonth">
                        <c:forEach var="entry" items="${selectableDaysOfMonth}">
                          <option value="${entry.value}" <c:if test="${ fn:contains(daysOfMonth, entry.value) }">selected</c:if> >${entry.name}</option>
                        </c:forEach>
                      </select>
                      <div class="hint">
                        Days of month
                      </div>
                    </div>
                    <div class="row4">
                      <select multiple size="4" name="months">
                        <c:forEach var="entry" items="${selectableMonths}">
                          <option value="${entry.value}" <c:if test="${ fn:contains(months, entry.value) }">selected</c:if> >${entry.name}</option>
                        </c:forEach>
                      </select>
                      <div class="hint">
                        Months
                      </div>
                    </div>   
                    <div class="row4">
                      <select multiple size="4" name="daysOfWeek">
                        <c:forEach var="entry" items="${selectableDaysOfWeek}">
                          <option value="${entry.value}" <c:if test="${ fn:contains(daysOfWeek, entry.value) }">selected</c:if> >${entry.name}</option>
                        </c:forEach>
                      </select>
                      <div class="hint">
                        Days of week
                      </div>
                    </div>   
                    <div class="row">
                      <select name="jobname">
                        <c:forEach var="job" items="${jobnames}">
                          <option value="${job}" <c:if test="${job == jobname}">selected</c:if> >${job}</option>
                        </c:forEach>
                      </select>
                      <div class="hint">
                        Select a job to execute
                      </div>
                    </div>   
                 </div>
                 <div class="tablefooter">
                   <div class="buttons">
                     <button class="rounded" type="submit">
                       <span>Add</span>
                     </button>
                   </div>
                 </div>
                </form>
              </div>
            </div>      
        </div>
    </jsp:body>
</t:page_tabbed>

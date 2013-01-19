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
List of adaptors
@date 2010-03-23
@author Anders Henja
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="java.util.List" %>
<%
    // Check if there are schedules available to display
    List schedules = ( List )request.getAttribute( "schedule" );
    if( schedules == null || schedules.size() <= 0 ) {
        request.getSession().setAttribute( "schedules_status", 0 );
    } else {
        request.getSession().setAttribute( "schedules_status", 1 );
    }
%>

<t:page_tabbed pageTitle="Schedule" activeTab="processing">
    <jsp:body>
        <div class="left">
            <t:menu_processing/>
        </div>
        <div class="right">
            <div class="blttitle">
                Schedule
            </div>
            <div class="blttext">
                Schedule. Click job ID to modify or delete a job or press Create to
                create a new job.
            </div>
            <div class="table">
                <t:error_message message="${emessage}"/>
                <form name="createJobForm" action="createscheduledjob.htm">
                    <c:choose>
                        <c:when test="${schedules_status == 1}">
                            <div class="schedules">
                                <div class="tableheader">
                                  <div id="cell" class="count">&nbsp;</div>
                                  <div id="cell" class="id">
                                    Job ID
                                  </div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach var="job" items="${schedule}">
                                  <div class="entry">
                                    <div id="cell" class="count">
                                      <c:out value="${count}"/>
                                      <c:set var="count" value="${count + 1}"/>
                                    </div>
                                    <div id="cell" class="id">
                                      <a href="showscheduledjob.htm?id=${job.id}">
                                        <c:out value="${job.id}"/>
                                      </a>                                        
                                    </div>
                                  </div>
                                </c:forEach>
                            </div>
                        </c:when>
                    </c:choose>
                    <br>
                    <div class="tablefooter">
                        <div class="buttons">
                            <button class="rounded" type="button"
                                    onclick="window.location.href='processing.htm'">
                                <span>Back</span>
                            </button>
                            <button class="rounded" type="submit">
                                <span>Create</span>
                            </button>
                        </div>
                    </div>
                </form>
            </div>      
        </div>
    </jsp:body>
</t:page_tabbed>

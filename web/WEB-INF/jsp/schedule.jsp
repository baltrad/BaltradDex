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
Schedule
@date 2010-03-23
@author Anders Henja
------------------------------------------------------------------------------%>

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

<t:generic_page pageTitle="Schedule">
    <jsp:body>
        <div class="schedule">
            <div class="table">
                <div class="header">
                    <div class="row">Schedule</div>
                </div>
                <div class="header-text">
                    Click job ID to modify or delete a job or press 
                    <i>Create</i> to create a new job.
                </div>
                <form name="createJobForm" action="schedule_create_job.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>
                    <c:choose>
                        <c:when test="${schedules_status == 1}">
                            <div class="body">
                                <div class="header-row">
                                    <div class="count">&nbsp;</div>
                                    <div class="id">Job ID</div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach var="job" items="${schedule}">
                                    <div class="row">
                                        <div class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
                                        </div>
                                        <div class="id">
                                            <a href="schedule_show_job.htm?id=${job.id}">
                                                <c:out value="${job.id}"/>
                                            </a>                                        
                                        </div>
                                  </div>
                                </c:forEach>
                            </div>  
                        </c:when>
                    </c:choose>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="submit" 
                                       value="Create"/>
                            </div>
                        </div>
                    </div>             
                </form>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>

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
List of anomaly detectors
@date 2011-09-22
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@ page import="java.util.List" %>

<%
    // Check if there are adaptors available to display
    List detectors = ( List )request.getAttribute("anomaly_detectors");
    if( detectors == null || detectors.size() <= 0 ) {
        request.getSession().setAttribute( "detectors_status", 0 );
    } else {
        request.getSession().setAttribute( "detectors_status", 1 );
    }
%>

<t:generic_page pageTitle="Quality controls">
    <jsp:body>
        <div class="quality-controls">
            <div class="table">
                <div class="header">
                    <div class="row">Quality controls</div>
                </div>
                <div class="header-text">
                    Click on control's name to modify or delete or click
                    <i>Create</i> to create a new quality control. 
                </div>
                <form name="createAnomalyDetectorForm" 
                      action="anomaly_detector_create.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>
                    <c:choose>
                        <c:when test="${detectors_status == 1}">
                            <div class="body">
                                <div class="header-row">
                                    <div class="count">&nbsp;</div>
                                    <div class="name">Name</div>
                                    <div class="description">Description</div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach var="detector" 
                                           items="${anomaly_detectors}">
                                    <div class="row">
                                        <div class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
                                        </div>
                                        <div class="name">
                                            <a href="anomaly_detector_show.htm?name=${detector.name}">
                                                <c:out value="${detector.name}"/>
                                            </a>
                                        </div>
                                        <div class="description">
                                            <c:out value="${detector.description}"/>
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

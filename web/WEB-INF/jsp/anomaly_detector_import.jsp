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
Create anomaly detectors
@date 2011-09-22
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@ page import="java.util.List" %>

<%
    // Check if there are routes available to display
    List qcs = ( List )request.getAttribute( "qcs" );
    if( qcs == null || qcs.size() <= 0 ) {
        request.getSession().setAttribute( "qcs_status", 0 );
    } else {
        request.getSession().setAttribute( "qcs_status", 1 );
    }
%>

<t:generic_page pageTitle="Anomaly Detector Import">
    <jsp:body>
        <div class="qc-import">
            <div class="table">
                <div class="header">
                    <div class="row">Anomaly detectors</div>
                </div>
                <form name="importAnomalyDetectors" action="anomaly_detector_import.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>
                    <c:choose>
                        <c:when test="${qcs_status == 1}">
                            <div class="header-text">
                                Click on route name in order to modify 
                                route settings.
                            </div>
                            <div class="body">
                                <div class="header-row">
                                    <div class="adaptorname">Adaptor</div>
                                    <div class="name">Name</div>
                                    <div class="description">Description</div>
                                    <div class="import">Import</div>
                                </div>
                                <c:forEach var="qc" items="${qcs}">
                                    <div class="row">
                                        <div class="adaptorname">
                                          <c:out value="${qc.adaptorName}" />
                                        </div>
                                        <div class="name">
                                          <c:out value="${qc.name}" />
                                        </div>
                                        <div class="description">
                                          <c:out value="${qc.description}"/>
                                        </div>    
                                        <div class="import">
                                          <input type="checkbox" name="imported" value="${qc.name}"/> 
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>  
                        </c:when>
                        <c:otherwise>
                            <div class="header-text">
                                No anomaly detectors to import.
                            </div>    
                        </c:otherwise>                            
                    </c:choose>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" name="submitButton" type="submit" value="Import"/>
                            </div>
                        </div>
                    </div>                      
                </form>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>


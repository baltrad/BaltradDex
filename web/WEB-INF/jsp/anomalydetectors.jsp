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
List of anomaly detectors
@date 2011-09-22
@author Anders Henja
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

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

<t:page_tabbed pageTitle="Quality controls" activeTab="processing">
    <jsp:body>
        <div class="left">
            <t:menu_processing/>
        </div>
        <div class="right">
            <div class="blttitle">
                Quality controls
            </div>
            <div class="blttext">
              List of quality controls. Click on name to modify or delete or click
              create to create a new quality control.
            </div>
            <div class="table">
                <t:error_message message="${emessage}"/>
                <form name="createAnomalyDetectorForm" action="create_anomaly_detector.htm">
                    <c:choose>
                        <c:when test="${detectors_status == 1}">
                            <div class="anomalydetectors">
                                <div class="tableheader">
                                  <div id="cell" class="count">&nbsp;</div>
                                  <div id="cell" class="name">Name</div>
                                  <div id="cell" class="description">Description</div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach var="detector" items="${anomaly_detectors}">
                                    <div class="entry">
                                        <div id="cell" class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
                                        </div>
                                        <div id="cell" class="name">
                                            <a href="show_anomaly_detector.htm?name=${detector.name}">
                                                <c:out value="${detector.name}"/>
                                            </a>
                                        </div>
                                        <div id="cell" class="description">
                                          <c:out value="${detector.description}"/>
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
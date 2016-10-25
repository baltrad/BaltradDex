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
List of routes
@date 2010-03-25
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@ page import="java.util.List" %>

<%
    // Check if there are routes available to display
    List routes = ( List )request.getAttribute( "routes" );
    if( routes == null || routes.size() <= 0 ) {
        request.getSession().setAttribute( "routes_status", 0 );
    } else {
        request.getSession().setAttribute( "routes_status", 1 );
    }
%>

<t:generic_page pageTitle="Routes">
    <jsp:body>
        <div class="routes">
            <div class="table">
                <div class="header">
                    <div class="row">Routes</div>
                </div>
                <form name="createRouteForm" action="routes.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>
                    <c:choose>
                        <c:when test="${routes_status == 1}">
                            <div class="header-text">
                                Click on route name in order to modify 
                                route settings.
                            </div>
                            <div class="body">
                                <div class="header-row">
                                    <div class="count">&nbsp;</div>
                                    <div class="name">
                                        <input name="sortBy"
                                            type="submit" value="Name" title="Sort routes by name">                                        
                                        </div>
                                    <div class="description">
                                        <input name="sortBy"
                                            type="submit" value="Description" title="Sort routes by description">                                        
                                        </div>
                                    <div class="type">
                                        <input name="sortBy"
                                            type="submit" value="Type" title="Sort routes by type">                                        
                                        </div>
                                    <div class="active">
                                        <input name="sortBy"
                                            type="submit" value="Active" title="Sort routes by actived/de-activated">                                        
                                        </div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach var="route" items="${routes}">
                                    <div class="row">
                                        <div class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
                                        </div>
                                        <c:url var="url" value="route.htm">
                                          <c:param name="name" value="${route.name}" />
                                        </c:url>                                        
                                        <div class="name">
                                            <a href="${url}">
                                                <c:out value="${route.name}"/>
                                            </a>
                                        <!--
                                            <a href="route.htm?name=${route.name}">
                                                <c:out value="${route.name}"/>
                                            </a>
                                        -->
                                        </div>
                                        <div class="description">
                                            <c:out value="${route.description}"/>
                                        </div>    
                                        <div class="type">
                                            <c:choose>
                                                <c:when test="${route.ruleType == 'groovy'}">
                                                    <c:out value="Script"/>
                                                </c:when>
                                                <c:when test="${route.ruleType == 'blt_volume'}">
                                                    <c:out value="Volume"/>
                                                </c:when>
                                                <c:when test="${route.ruleType == 'blt_composite'}">
                                                    <c:out value="Composite"/>
                                                </c:when>
                                                <c:when test="${route.ruleType == 'bdb_trim_age'}">
                                                    <c:out value="BdbTrimAge"/>
                                                </c:when>
                                                <c:when test="${route.ruleType == 'bdb_trim_count'}">
                                                    <c:out value="BdbTrimCount"/>
                                                </c:when>
                                                <c:when test="${route.ruleType == 'blt_gmap'}">
                                                    <c:out value="GoogleMap"/>
                                                </c:when>
                                                <c:when test="${route.ruleType == 'blt_acrr'}">
                                                    <c:out value="ACRR"/>
                                                </c:when>
                                                <c:when test="${route.ruleType == 'blt_gra'}">
                                                    <c:out value="GRA"/>
                                                </c:when>
                                                <c:when test="${route.ruleType == 'blt_wrwp'}">
                                                    <c:out value="WRWP"/>
                                                </c:when>
                                                <c:when test="${route.ruleType == 'blt_scansun'}">
                                                    <c:out value="ScanSun"/>
                                                </c:when>
                                                <c:when test="${route.ruleType == 'blt_site2d'}">
                                                    <c:out value="Site2D"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${route.ruleType}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="active">
                                            <c:choose>
                                                <c:when test="${route.ruleValid == false}">
                                                    <img src="includes/images/log-error.png" 
                                                         alt="Invalid">
                                                </c:when>
                                                <c:when test="${route.active == true}">
                                                    <img src="includes/images/log-info.png" 
                                                         alt="Active">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="includes/images/stop.png" 
                                                         alt="Inactive"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>  
                        </c:when>
                        <c:otherwise>
                            <div class="header-text">
                                No routes found. Use create route functionality 
                                to define routes.
                            </div>    
                        </c:otherwise>                            
                    </c:choose>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="button" value="Home"
                                       onclick="window.location.href='status.htm'"/>
                            </div>
                        </div>
                    </div>                      
                </form>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>

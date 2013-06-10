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
Document   : List of local radar stations
Created on : May 21, 2013, 11:16 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Radar stations">
    <jsp:body>
        <div class="show-radars">
            <div class="table">
                <div class="header">
                    <div class="row">Radar stations</div>
                </div>
                <c:choose>
                    <c:when test="${not empty radars}">
                        <div class="header-text">
                            List of local radar stations. 
                        </div>
                        <div class="body">
                            <div class="header-row">
                                <div class="count">&nbsp;</div>
                                <div class="country-code">
                                    Country code
                                </div>
                                <div class="center-code">
                                    Center code
                                </div>
                                <div class="radcode">
                                    Radar code
                                </div>
                                <div class="radplace">
                                    Place name
                                </div>
                            </div>
                            <c:set var="count" scope="page" value="1"/>
                            <c:forEach items="${radars}" var="radar">
                                <div class="row">
                                    <div class="count">
                                        <c:out value="${count}"/>
                                        <c:set var="count" value="${count + 1}"/>
                                    </div>
                                    <div class="country-code" 
                                         title="Country code">
                                        <c:out value="${radar.countryCode}"/>
                                    </div>
                                    <div class="center-code" 
                                         title="GTS code / Center number">
                                        <c:out value="${radar.centerCode} / 
                                               ${radar.centerNumber}"/>
                                    </div>
                                    <div class="radcode" 
                                         title="Radar code / WMO number">
                                        <c:out value="${radar.radarCode} / 
                                               ${radar.radarWmo}"/>
                                    </div>
                                    <div class="radplace" title="Radar location">
                                        <c:out value="${radar.radarPlace}"/>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="header-text">
                            No radar stations found.
                            Click <i>Add</i> button below to configure 
                            new radar station.
                        </div>
                    
                    </c:otherwise>
                </c:choose>      
                <div class="table-footer">
                    <div class="buttons">
                        <div class="button-wrap">
                            <input class="button" type="button" 
                                   value="Add" 
                                   onclick="window.location.href='radars_save.htm'"/>
                        </div>
                        <div class="button-wrap">
                            <input class="button" type="button" 
                                   value="Home"
                                   onclick="window.location.href='status.htm'"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:generic_page>

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
Document   : Radar stations selected for removal
Created on : May 22, 2013, 9:23 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Remove radar station">
    <jsp:body>
        <div class="show-radars">
            <div class="table">
                <div class="header">
                    <div class="row">Remove radar station</div>
                </div>
                <div class="header-text">
                    The following radar stations will be removed.
                </div>
                <form action="radars_remove_status.htm">
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
                                <div class="hidden">
                                    <input type="checkbox" name="radars"
                                           value="${radar.id}" checked/>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="button" 
                                       value="Back"
                                       onclick="window.location.href='radars_remove.htm'"/>
                            </div>
                            <div class="button-wrap">
                                <input class="button" type="submit" 
                                       value="Remove"/>
                            </div>
                        </div>
                    </div>    
                </form>                         
            </div>
        </div>
    </jsp:body>
</t:generic_page>

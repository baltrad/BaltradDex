<%------------------------------------------------------------------------------
Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW

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
Document   : Remove selected radar page
Created on : Nov 5, 2012, 12:51 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Remove radar station" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Remove radar station
            </div>
            <div class="blttext">
                The following radar stations will be removed.
            </div>
            <div class="table">
                <div class="showradar">
                    <form action="remove_radar_status.htm">
                        <div class="tableheader">
                            <div id="cell" class="count">&nbsp;</div>
                            <div id="cell" class="country_code">
                                Country
                            </div>
                            <div id="cell" class="center_code">
                                Center
                            </div>
                            <div id="cell" class="radar_place">
                                Place
                            </div>
                            <div id="cell" class="radar_code">
                                Radar code
                            </div>
                            <div id ="cell" class="select">
                                Select
                            </div>
                        </div>
                        <c:set var="count" scope="page" value="1"/>
                        <c:forEach var="radar" items="${radars}">
                            <div class="entry">
                                <div id="cell" class="count">
                                    <c:out value="${count}"/>
                                    <c:set var="count" value="${count + 1}"/>
                                </div>
                                <div id="cell" class="country_code" 
                                    title="Country code">
                                    <c:out value="${radar.countryCode}"/>
                                </div>
                                <div id="cell" class="center_code"
                                    title="GTS code / Center number">
                                    <c:out value="${radar.centerCode} / ${radar.centerNumber}"/>
                                </div>
                                <div id="cell" class="radar_place" 
                                    title="Radar place">
                                    <c:out value="${radar.radarPlace}"/>
                                </div>
                                <div id="cell" class="radar_code"
                                    title="Radar code / WMO number">
                                    <c:out value="${radar.radarCode} / ${radar.radarWmo}"/>
                                </div>
                                <div id="cell" class="hidden">
                                    <input type="checkbox" name="radars"
                                           value="${radar.id}" checked/>
                                </div>
                            </div>
                        </c:forEach>
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                    onclick="window.location.href='remove_radar.htm'">
                                    <span>Back</span>
                                </button>
                                <button class="rounded" type="submit">
                                    <span>OK</span>
                                </button>
                            </div>
                        </div>
                    </div>
            </div>
        </div>
    </jsp:body>
</t:page_tabbed>
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
Created on : May 21, 2013, 11:30 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:generic_page pageTitle="Save radar station">
    <jsp:body>
        <div class="save-radars">
            <div class="table">
                <div class="header">
                    <div class="row">Save radar station</div>
                </div>
                <div class="header-text">
                    Select center ID to access available radar stations.
                    Select radar station and click <i>Save</i> to store settings.  
                </div>
                <form:form method="POST" commandName="radar">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${odim_load_error}"/>    
                    <div class="row" id="label">
                        <div class="leftcol">Center ID</div>
                        <div class="rightcol">Radar station</div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            <select name="center_id" size="10" multiple 
                                    onchange="submitCenterId(this.form)">
                                <c:forEach items="${centers}" 
                                           var="center">
                                    <c:choose>
                                        <c:when test="${center.key == center_selected}">
                                            <option value="${center.key}" selected 
                                                    title="Country - GTS code - Center number">    
                                                <c:out value="${center.value}"/>
                                            </option>    
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${center.key}"
                                                    title="Country - GTS code - Center number">    
                                                <c:out value="${center.value}"/>
                                            </option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            <select>
                        </div>
                        <div class="rightcol">
                            <select name="radar_id" size="10" multiple>
                                <c:forEach items="${radars}" 
                                           var="radar">
                                    <option value="${radar.key}"
                                            title="Place - Code - WMO number">    
                                        <c:out value="${radar.value}"/>
                                    </option>
                                </c:forEach>
                            <select>
                        </div>
                    </div>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="submit" 
                                       value="Save"/>
                            </div>
                        </div>
                    </div>                  
                </form:form>
            </div>
        </div>
    </jsp:body>
</t:generic_page>



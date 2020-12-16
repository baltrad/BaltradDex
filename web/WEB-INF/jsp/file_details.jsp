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
Document   : Detailed file information
Created on : May 20, 2013, 10:49 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="File details">
    <jsp:body>
        <div class="file-details">
            <div class="table">	
                <div class="header">
                    <div class="row">Detailed file information</div>
                </div>
                <div class="body">
                    <div class="row">
                        <div class="leftcol">
                            File signature (UUID):
                        </div>
                        <div class="rightcol">
                            <c:out value="${file_name}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Date:
                        </div>
                        <div class="rightcol">
                            <c:out value="${date_str}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Time:
                        </div>
                        <div class="rightcol">
                            <c:out value="${time_str}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Source ID:
                        </div>
                        <div class="rightcol">
                            <c:out value="${source}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Data type:
                        </div>
                        <div class="rightcol">
                            <c:out value="${type}"></c:out>
                        </div>
                    </div>
                    <div class="row">
                        <div class="leftcol">
                            Storage time:
                        </div>
                        <div class="rightcol">
                            <c:out value="${storage_time}"></c:out>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="file-preview">
            <div class="table">	
                <div class="header">
                    <div class="row">File preview</div>
                </div>
                <c:choose>
                    <c:when test="${not empty blt_datasets}">
                        <div class="header-text">
                            Click thumbnail to display full size preview.
                        </div>
                        <div class="body">
                            <div class="header-row">
                                <div class="count">&nbsp;</div>
                                <c:if test="${h5_object == 'SCAN' 
                                                  || h5_object == 'PVOL'}">
                                    <div class="elangle">Elevation angle</div>
                                </c:if>
                                <c:if test="${h5_object == 'COMP'}">
                                    <div class="elangle">Nodes</div>
                                </c:if>  
                                <div class="data-type">Data type</div>
                                <div class="thumbnail">Preview</div>
                            </div>
                            <c:set var="count" scope="page" value="1"/>
                            <c:forEach var="dataset" items="${blt_datasets}">
                                <div class="row">
                                    <div class="count">
                                        <c:out value="${count}"/>
                                        <c:set var="count" value="${count + 1}"/>
                                    </div>
                                    <c:if test="${h5_object == 'SCAN' 
                                                  || h5_object == 'PVOL'}">
                                        <div class="elangle">
                                            <fmt:formatNumber type="number" 
                                                              maxFractionDigits="1"
                                                              minFractionDigits="1"
                                                              value="${dataset.elevationAngle}"/>
                                            &deg;
                                        </div>
                                    </c:if>
                                    <c:if test="${h5_object == 'COMP'}">
                                        <div class="elangle">
                                            <c:out value="${dataset.nodes}"/>
                                        </div>
                                    </c:if>         
                                    <div class="data-type">
                                        <c:out value="${dataset.quantity}"/>
                                    </div>
                                    <div class="thumbnail">
                                        <c:url var="imagePreviewURL" value="file_preview.htm">
                                            <c:param name="file_uuid"
                                                    value="${uuid}"/>
                                            <c:param name="file_object"
                                                    value="${type}"/>
                                            <c:param name="dataset_path"
                                                    value="${dataset.name}"/>
                                            <c:param name="dataset_where"
                                                    value="${dataset.where}"/>

                                            <c:param name="dataset_quantity"
                                                    value="${dataset.quantity}"/>
                                            <c:param name="dataset_width"
                                                    value="${dataset.width}"/>
                                            <c:param name="lat0" value="${dataset.lat0}"/>
                                            <c:param name="lon0" value="${dataset.lon0}"/>
                                            <c:param name="llLat" value="${dataset.llLat}"/>
                                            <c:param name="llLon" value="${dataset.llLon}"/>
                                            <c:param name="urLat" value="${dataset.urLat}"/>
                                            <c:param name="urLon" value="${dataset.urLon}"/>
                                        </c:url>
                                        <a href="#" onClick="window.open(
                                                '<c:out value="${imagePreviewURL}"/>',
                                                'mywindow','width=${dataset.width},\n\
                                                height=${dataset.height}, left=100, top=100,\n\
                                                screenX=100, screenY=100')">
                                            <img src="${dataset.thumbPath}" alt="no_thumb"/>
                                        </a>

                                    </div>
                                </div>    
                            </c:forEach>
                        </div>            
                    </c:when>
                    <c:otherwise>
                        <div class="header-text">
                            No previews are available.
                        </div>
                    </c:otherwise>
                </c:choose>        
                <div class="table-footer">
                    <div class="buttons">
                        <div class="button-wrap">
                            <input class="button" type="button" value="Back"
                                   onclick="window.location.href='file_browser.htm';"/>
                        </div>
                    </div>
                </div>                                                  
            </div>    
        </div>                
    </jsp:body>
</t:generic_page>

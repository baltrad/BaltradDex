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
Document   : Detailed information on a given data file
Created on : Nov 15, 2010, 9:53 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="File details" activeTab="home">
  <jsp:body>
    <div class="left">
        <t:menu_home/>
    </div>
    <div class="right">
            <div class="blttitle">
                File details
            </div>
            <div class="blttext">
                Detailed information about data file.
            </div>
            <div class="table">
                <div class="leftcol">
                    <div class="row">
                        File:
                    </div>
                    <div class="row">
                        Date:
                    </div>
                    <div class="row">
                        Time:
                    </div>
                    <div class="row">
                        Source:
                    </div>
                    <div class="row">
                        Data type:
                    </div>
                    <div class="row">
                        Storage time:
                    </div>
                </div>
                <div class="rightcol">
                    <div class="row">
                        ${file_name}
                    </div>
                    <div class="row">
                        ${date_str}
                    </div>
                    <div class="row">
                        ${time_str}
                    </div>
                    <div class="row">
                        ${source}
                    </div>
                    <div class="row">
                        ${type}
                    </div>
                    <div class="row">
                        ${storage_time}
                    </div>
                </div>
            </div>
            <div class="blttitle">
                <div class="break"></div>
                Data preview
            </div>
            <c:choose>
                <c:when test="${not empty blt_datasets}">
                    <div class="blttext">
                        Click thumbnail to view full-sized image.
                    </div>
                    <div class="table">
                        <div id="thumbnails">
                            <c:forEach var="dataset" items="${blt_datasets}">
                                <div class="thumb">
                                    <div class="image">
                                        <c:url var="imagePreviewURL" value="data_preview.htm">
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
                                    <div class="caption">
                                        <c:out value="${dataset.elevationAngle}"></c:out>&deg;
                                        <c:out value="${dataset.quantity}"></c:out>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="tablefooter">
                            <form action="radars.htm">
                                <div class="buttons">
                                    <button class="rounded" type="button" 
                                            onclick="window.location.href='browse_files.htm'">
                                        <span>Back</span>
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table">
                        <div class="blttext">
                            No image thumbnails found.
                        </div>
                        <div class="tablefooter">
                            <form action="radars.htm">
                                <div class="buttons">
                                    <button class="rounded" type="button" 
                                            onclick="window.location.href='browse_files.htm'">
                                        <span>Back</span>
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>                                   
    </div>
  </jsp:body>
</t:page_tabbed>

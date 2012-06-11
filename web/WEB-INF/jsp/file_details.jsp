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
Document   : Data file detailed information
Created on : Nov 15, 2010, 9:53 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@ page import="eu.baltrad.dex.db.model.BltFile" %>
<%@ page import="eu.baltrad.dex.db.model.BltDataset" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.File" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    HashMap model = ( HashMap )request.getAttribute( "file_details" );
    BltFile bltFile = ( BltFile )model.get( "blt_file" );
    List<BltDataset> bltDatasets = ( List )model.get( "blt_datasets" );
    request.setAttribute( "blt_datasets", bltDatasets );

    String uuid = bltFile.getUuid();
    String fileName = bltFile.getPath().substring( bltFile.getPath().lastIndexOf( File.separator )
            + 1, bltFile.getPath().length() );
    String source = bltFile.getSource();
    SimpleDateFormat dateTimeFormat = new SimpleDateFormat( "MMM d, yyyy HH:mm:ss" );
    String storageTime = dateTimeFormat.format( bltFile.getStorageTime() );
    SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
    SimpleDateFormat timeFormat = new SimpleDateFormat( "HH:mm:ss" );
    String dateStr = dateFormat.format( bltFile.getTimeStamp() );
    String timeStr = timeFormat.format( bltFile.getTimeStamp() );
    String type = bltFile.getType();
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>BALTRAD | File details</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/home_tab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/home_menu.jsp"%>
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
                                    <%=fileName%>
                                </div>
                                <div class="row">
                                    <%=dateStr%>
                                </div>
                                <div class="row">
                                    <%=timeStr%>
                                </div>
                                <div class="row">
                                    <%=source%>
                                </div>
                                <div class="row">
                                    <%=type%>
                                </div>
                                <div class="row">
                                    <%=storageTime%>
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
                                                                value="<%=bltFile.getUuid()%>"/>
                                                        <c:param name="file_object"
                                                                value="<%=bltFile.getType()%>"/>
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
                                               <button class="rounded" type="button" onclick="history.go(-1);">
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
                                               <button class="rounded" type="button" onclick="history.go(-1);">
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
            </div>
        </div>
        <div id="bltfooter">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>
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
Document   : Displays available data sources
Created on : Apr 28, 2011, 11:42 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@page import="java.util.List"%>
<%
    List dataSources = ( List )request.getAttribute( "dataSources" );
    if( dataSources == null || dataSources.size() <= 0 ) {
        request.getSession().setAttribute( "dsStatus", 0 );
    } else {
        request.getSession().setAttribute( "dsStatus", 1 );
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>BALTRAD | Edit data source</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/settingsTab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/settingsMenu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            Edit data source
                        </div>
                        <c:choose>
                            <c:when test="${dsStatus == 1}">
                                <div class="blttext">
                                    List of available data sources. Click on data source name
                                    in order to edit data source configuration.
                                </div>
                                <div class="table">
                                    <div class="dsshow">
                                        <div class="tableheader">
                                            <div id="cell" class="count">&nbsp;</div>
                                            <div id="cell" class="name">
                                                Name
                                            </div>
                                            <div id="cell" class="description">
                                                Description
                                            </div>
                                        </div>
                                        <c:set var="count" scope="page" value="1"/>
                                        <c:forEach items="${dataSources}" var="dataSource">
                                            <div class="entry">
                                                <div id="cell" class="count">
                                                    <c:out value="${count}"/>
                                                    <c:set var="count" value="${count + 1}"/>
                                                </div>
                                                <div id="cell" class="name">
                                                    <a href="dsSaveName.htm?dsName=${dataSource.name}">
                                                        <c:out value="${dataSource.name}"/>
                                                    </a>
                                                </div>
                                                <div id="cell" class="description">
                                                    <c:out value="${dataSource.description}"/>
                                                </div>
                                            </div>
                                        </c:forEach>
                                        <div class="tablefooter">
                                            <div class="buttons">
                                                <button class="rounded" type="button"
                                                    onclick="window.location.href='home.htm'">
                                                    <span>OK</span>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="blttext">
                                    No data sources have been found.
                                    Use configure data source functionality in order to define
                                    new data sources.
                                </div>
                                <div class="table">
                                    <div class="tablefooter">
                                        <div class="buttons">
                                            <button class="rounded" type="button"
                                                onclick="window.location.href='home.htm'">
                                                <span>OK</span>
                                            </button>
                                        </div>
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
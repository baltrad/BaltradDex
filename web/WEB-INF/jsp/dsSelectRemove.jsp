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
Document   : Page displays available data sources
Created on : Apr 4, 2011, 14:03 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="java.util.List" %>
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
        <title>Baltrad | Remove data source</title>
    </head>
    <body>
        <div id="container">
            <div id="header">
                <script type="text/javascript" src="includes/header.js"></script>
            </div>
            <div id="content">
                <div id="left">
                    <%@include file="/WEB-INF/jsp/mainMenu.jsp"%>
                </div>
                <div id="right">
                    <div id="page-title">
                        <div class="left">
                            Remove data source
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <c:choose>
                        <c:when test="${dsStatus == 1}">
                            <div id="text-box">
                                List of available data sources. Click on check box to select 
                                data source to be removed.
                            </div>
                            <div id="table">
                                <form method="post" action="dsRemove.htm">
                                    <div id="dsSelect">
                                        <div class="table-hdr">
                                            <div class="name">
                                                Name
                                            </div>
                                            <div class="description">
                                                Description
                                            </div>
                                            <div class="select">
                                                Remove
                                            </div>
                                        </div>
                                        <c:forEach items="${dataSources}" var="dataSource">
                                            <div class="table-row">
                                                <div class="name">
                                                    <c:out value="${dataSource.name}"/>
                                                </div>
                                                <div class="description">
                                                    <c:out value="${dataSource.description}"/>
                                                </div>
                                                <div class="select">
                                                    <input type="checkbox" name="selectedSources"
                                                           value="${dataSource.id}">
                                                </div>
                                            </div>
                                        </c:forEach>
                                        <div class="footer">
                                            <div class="right">
                                                <button class="rounded" type="button"
                                                        onclick="window.location='configuration.htm'">
                                                    <span>Back</span>
                                                </button>
                                                <button class="rounded" type="submit" name="submitButton">
                                                    <span>Submit</span>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-alert.png"
                                         alt="no_radars"/>
                                </div>
                                <div class="text">
                                    List of data sources is currently empty.
                                    Use configuration options to add new data sources.
                                </div>
                            </div>
                            <div class="footer">
                                <div class="right">
                                    <form action="configuration.htm">
                                        <button class="rounded" type="submit">
                                            <span>OK</span>
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>
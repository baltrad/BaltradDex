<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW

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
List of routes
@date 2010-03-25
@author Anders Henja
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="java.util.List"%>

<%
    // Check if there are routes available to display
    List routes = ( List )request.getAttribute( "routes" );
    if( routes == null || routes.size() <= 0 ) {
        request.getSession().setAttribute( "routes_status", 0 );
    } else {
        request.getSession().setAttribute( "routes_status", 1 );
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>BALTRAD | Routes</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/processing_tab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/processing_menu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            Routes
                        </div>
                        <div class="blttext">
                            List of routes. Click on route name in order to modify route settings.
                        </div>
                        <div class="table">
                            <%if (request.getAttribute("emessage") != null) {%>
                                <div class="systemerror">
                                    <div class="header">
                                        Problems encountered.
                                    </div>
                                    <div class="message">
                                        <%=request.getAttribute("emessage")%>
                                    </div>
                                </div>
                            <%}%>
                            <form name="createRouteForm" action="createroute.htm">
                                <c:choose>
                                    <c:when test="${routes_status == 1}">
                                        <div class="showroutes">
                                            <div class="tableheader">
                                                <div id="cell" class="count">&nbsp;</div>
                                                <div id="cell" class="name">
                                                    Name
                                                </div>
                                                <div id="cell" class="description">
                                                    Description
                                                </div>
                                                <div id="cell" class="type">
                                                    Type
                                                </div>
                                                <div id ="cell" class="active">
                                                    Active
                                                </div>
                                            </div>
                                            <c:set var="count" scope="page" value="1"/>
                                            <c:forEach var="route" items="${routes}">
                                                <div class="entry">
                                                    <div id="cell" class="count">
                                                        <c:out value="${count}"/>
                                                        <c:set var="count" value="${count + 1}"/>
                                                    </div>
                                                    <div id="cell" class="name">
                                                        <a href="showroute.htm?name=${route.name}">
                                                            <c:out value="${route.name}"/>
                                                        </a>
                                                    </div>
                                                    <div id="cell" class="description">
                                                        <c:out value="${route.description}"/>
                                                    </div>
                                                    <div id="cell" class="type">
                                                        <c:choose>
                                                            <c:when test="${route.ruleType == 'groovy'}">
                                                                <c:out value="Script"/>
                                                            </c:when>
                                                            <c:when test="${route.ruleType == 'blt_volume'}">
                                                                <c:out value="Volume"/>
                                                            </c:when>
                                                            <c:when test="${route.ruleType == 'composite'}">
                                                              <c:out value="Composite"/>
                                                            </c:when>
                                                            <c:when test="${route.ruleType == 'bdb_trim_age'}">
                                                                <c:out value="BdbTrimAge"/>
                                                            </c:when>
                                                            <c:when test="${route.ruleType == 'bdb_trim_count'}">
                                                                <c:out value="BdbTrimCount"/>
                                                            </c:when>
                                                            <c:when test="${route.ruleType == 'bdb_gmap'}">
                                                                <c:out value="GoogleMap"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:out value="${route.ruleType}"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div id="cell" class="active">
                                                        <c:choose>
                                                            <c:when test="${route.ruleValid == false}">
                                                              <img src="includes/images/icons/routes-warning.png"
                                                                       alt="Invalid">
                                                            </c:when>
                                                            <c:when test="${route.active == true}">
                                                                <img src="includes/images/icons/success.png"
                                                                     alt="Active">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <img src="includes/images/icons/stop.png"
                                                                     alt="Inactive"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                </c:choose>
                                <div class="tablefooter">
                                   <div class="buttons">
                                       <button class="rounded" type="button"
                                           onclick="window.location.href='processing.htm'">
                                           <span>Back</span>
                                       </button>
                                   </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div id="bltfooter">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>
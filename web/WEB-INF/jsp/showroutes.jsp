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
<%@ page import="java.util.List" %>
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
        <title>Baltrad | Routes</title>
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
                            Routes
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        List of routes. Create or choose a route.
                    </div>
                    <form name="createRouteForm" action="createroute.htm">
                        <div id="table">
                            <c:choose>
                                <c:when test="${routes_status == 1}">
                                    <div id="showroutes">
                                        <div class="table-hdr">
                                            <div class="active">
                                                Active
                                            </div>
                                            <div class="name">
                                                Name
                                            </div>
                                            <div class="type">
                                                Type
                                            </div>
                                            <div class="description">
                                                Description
                                            </div>
                                        </div>
                                        <c:forEach var="route" items="${routes}">
                                            <div class="table-row">
                                                <div class="active">
                                                    <c:choose>
                                                        <c:when test="${route.active == true}">
                                                            <img src="includes/images/green_bulb.png"
                                                                 width="12" height="12"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img src="includes/images/red_bulb.png"
                                                                 width="12" height="12"/>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="name">
                                                    <a href="showroute.htm?name=${route.name}">
                                                        <c:out value="${route.name}"/>
                                                    </a>
                                                </div>
                                                <div class="type">
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
                                                        <c:otherwise>
                                                            <c:out value="${route.ruleType}"/>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="description">
                                                    <c:out value="${route.description}"/>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                            </c:choose>
                            <div class="footer">
                                <div class="right">
                                    <button class="rounded" name="submitButton" type="submit"
                                            value="Script">
                                        <span>Script</span>
                                    </button>
                                    <button class="rounded" name="submitButton" type="submit"
                                            value="Composite">
                                        <span>Composite</span>
                                    </button>
                                    <button class="rounded" name="submitButton" type="submit"
                                            value="Volume">
                                        <span>Volume</span>
                                    </button>
                                </div>
				<div class="right">
                                    <button class="rounded" name="submitButton" type="submit"
                                            value="BdbTrimCount">
                                        <span>BdbTrimCount</span>
                                    </button>
                                    <button class="rounded" name="submitButton" type="submit"
                                            value="BdbTrimAge">
                                        <span>BdbTrimAge</span>
                                    </button>
				</div>
                            </div>
                        </div>
                      </form>
                    <%if (request.getAttribute("emessage") != null) {%>
                        <div class="routerrerror"><%=request.getAttribute("emessage")%></div>
                    <%}%>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>

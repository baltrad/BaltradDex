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
List of routes
@date 2010-03-25
@author Anders Henja
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

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
                    <div id="table">
                        <form name="createRouteForm" action="createroute.htm">
                            <display:table name="routes" id="route" defaultsort="1"
                                requestURI="showroutes.htm" cellpadding="5" cellspacing="0"
                                export="false" class="tableborder">
                              <c:choose>
                                <c:when test="${route.active == true}">
                                  <display:column sortable="false" title="Active" class="tdcenter">
                                    <img src="includes/images/green_bulb.png" width="12" height="12"/>
                                  </display:column>
                                </c:when>
                                <c:otherwise>
                                  <display:column sortable="false" title="Active" class="tdcenter">
                                    <img src="includes/images/red_bulb.png" width="12" height="12"/>
                                  </display:column>
                                </c:otherwise>
                              </c:choose>
                              <display:column sortable="true" title="Name"
                                sortProperty="name" href="showroute.htm" paramId="name" paramProperty="name"
                                class="tdcenter" value="${route.name}">
                              </display:column>
                              <c:choose>
                                <c:when test="${route.ruleType == 'groovy'}">
                                  <display:column sortable="true" title="Type"
                                    sortProperty="type" paramId="type" paramProperty="type"
                                    class="tdcenter" value="Script">
                                  </display:column>
                                </c:when>
                                <c:when test="${route.ruleType == 'blt_volume'}">
                                  <display:column sortable="true" title="Type"
                                    sortProperty="type" paramId="type" paramProperty="type"
                                    class="tdcenter" value="Volume">
                                  </display:column>
                                </c:when>
                                <c:when test="${route.ruleType == 'composite'}">
                                  <display:column sortable="True" title="Type"
                                    sortProperty="type" paramId="type" paramProperty="type"
                                    class="tdcenter" value="Composite">
                                  </display:column>
                                </c:when>
                                <c:when test="${route.ruleType == 'bdb_trim_age'}">
                                  <display:column sortable="True" title="Type"
                                    sortProperty="type" paramId="type" paramProperty="type"
                                    class="tdcenter" value="BdbTrimAge">
                                  </display:column>
                                </c:when>
                                <c:when test="${route.ruleType == 'bdb_trim_count'}">
                                  <display:column sortable="True" title="Type"
                                    sortProperty="type" paramId="type" paramProperty="type"
                                    class="tdcenter" value="BdbTrimCount">
                                  </display:column>
                                </c:when>
                                <c:otherwise>
                                  <display:column sortable="true" title="Type"
                                    sortProperty="type" paramId="type" paramProperty="type"
                                    class="tdcenter" value="${route.ruleType}">
                                  </display:column>
                                </c:otherwise>
                              </c:choose>
                              <display:column sortable="true" title="Description"
                                sortProperty="description" paramId="description" paramProperty="description"
                                class="tdcenter" value="${route.description}">
                              </display:column>
                            </display:table>
                            <div class="footer">
                                <div class="right">
                                    <button class="rounded" name="submitButton" type="submit" value="Script">
                                        <span>Script</span>
                                    </button>
                                    <button class="rounded" name="submitButton" type="submit" value="Composite">
                                        <span>Composite</span>
                                    </button>
                                    <button class="rounded" name="submitButton" type="submit" value="Volume">
                                        <span>Volume</span>
                                    </button>
                                </div>
				<div class="right">
                                    <button class="rounded" name="submitButton" type="submit" value="BdbTrimCount">
                                        <span>BdbTrimCount</span>
                                    </button>
                                    <button class="rounded" name="submitButton" type="submit" value="BdbTrimAge">
                                        <span>BdbTrimAge</span>
                                    </button>
				</div>
                            </div>
                          </form>
                        <%if (request.getAttribute("emessage") != null) {%>
                            <div class="routerrerror"><%=request.getAttribute("emessage")%></div>
                        <%}%>
                    </div>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <script type="text/javascript" src="includes/footer.js"></script>
        </div>
    </body>
</html>

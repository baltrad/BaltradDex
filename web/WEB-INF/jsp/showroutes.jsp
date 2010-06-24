<%--
Copyright (C) 2009-2010 Swedish Meteorological and Hydrological Institute, SMHI,

This file is part of the Beast library.

Beast library is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Beast library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the Beast library library.  If not, see <http://www.gnu.org/licenses/>.
-------------------------------------------------------------------
List of routes
@date 2010-03-25
@author Anders Henja
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<style type="text/css">
.routererror {
  font-weight:bold;
  color:#c80000;
}
</style>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Routes</title>
</head>

<body>
    <div id="container">
        <div id="header"></div>
        <div id="nav">
            <script type="text/javascript" src="includes/navigation.js"></script>
        </div>
        <div class="outer">
            <div class="inner">
                <div class="float-wrap">
                    <div id="main">
                        <h1>Routes</h1>
                          <br/>
                          <h2>
                            <p>List of routes. Create or choose a route.</p>
                          </h2>
                          <form name="createRouteForm" action="createroute.htm">
                            <display:table name="routes" id="route" defaultsort="1"
                                requestURI="showroutes.htm" cellpadding="5" cellspacing="0"
                                export="false" class="tableborder">
                              <c:choose>
                                <c:when test="${route.active == true}">
                                  <display:column sortable="false" title="Active" class="tdcenter">
                                    <img src="includes/images/Green_ball.jpg" width="12" height="12"/>
                                  </display:column>
                                </c:when>
                                <c:otherwise>
                                  <display:column sortable="false" title="Active" class="tdcenter">
                                    <img src="includes/images/Red_ball.jpg" width="12" height="12"/>
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
                                <c:otherwise>
                                  <display:column sortable="true" title="Type"
                                    sortProperty="type" paramId="type" paramProperty="type"
                                    class="tdcenter" value="Composite">
                                  </display:column>
                                </c:otherwise>
                              </c:choose>
                              <display:column sortable="true" title="Description"
                                sortProperty="description" paramId="description" paramProperty="description"
                                class="tdcenter" value="${route.description}">
                              </display:column>
                            </display:table>
                            <div id="table-footer">
                              <input type="submit" value="Script" name="submitButton"/>
                              <input type="submit" value="Composite" name="submitButton"/>
                            </div>
                          </form>
                          <%if (request.getAttribute("emessage") != null) {%>
                            <span class="routerrerror"><%=request.getAttribute("emessage")%></span>
                          <%}%>
                    </div>
                    <div id="left">
                        <%@ include file="/WEB-INF/jsp/mainMenu.jsp"%>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
    </div>
    <div id="footer">
        <script type="text/javascript" src="includes/footer.js"></script>
    </div>
</body>
</html>

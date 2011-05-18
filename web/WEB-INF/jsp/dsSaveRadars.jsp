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
Document   : Save data source radars parameter page
Created on : Apr 22, 2011, 11:37 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="java.util.List" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" language="javascript" src="includes/tooltip.js"></script>
        <script type="text/javascript">
            <!--
            var t1 = null;
            var l1 = "Allows to select radar stations for this data source";
            function initTooltips() {
                t1 = new ToolTip( "ttRadar", false );
            }
            -->
        </script>
        <title>Baltrad | Configure data source</title>
    </head>
    <body onload="initTooltips()">
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
                            Configure data source
                        </div>
                        <div class="right">
                        </div>
                    </div>
                        <div id="text-box">
                            Step 2. Select radar stations
                            <div class="helpIconRight" onmouseover="if(t1)t1.Show(event,l1)"
                                    onmouseout="if(t1)t1.Hide(event)">
                                <img src="includes/images/help-icon.png" alt="helpIcon"/>
                            </div>
                        </div>
                        <div id="table">
                            <form method="post" action="dsSaveUsers.htm">
                                <div id="dsConfig">
                                    <div class="row">
                                        <div class="left">
                                            Radar station
                                        </div>
                                        <div class="right">
                                            <c:forEach items="${selectedRadars}" var="radar">
                                                <div class="selectedItem">
                                                    <c:out value="${radar.channelName}"></c:out>
                                                    &nbsp;
                                                    WMO number: <c:out value="${radar.wmoNumber}"></c:out>
                                                    &nbsp;
                                                </div>
                                            </c:forEach>
                                            <div class="row">
                                                <c:if test="${numSelectedRadars < numAvailableRadars}">
                                                    <select name="radarsList">
                                                        <option value="select">
                                                            <c:out value="-- Select radar --"/>
                                                        </option>
                                                        <c:forEach items="${availableRadars}"
                                                                   var="radar">
                                                            <option value="${radar.channelName}">
                                                                <c:out value="${radar.channelName}"/>
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                        <input type="submit" name="addRadar"
                                                            title="Add radar station"
                                                            class="buttonInput" value="+">
                                                </c:if>
                                                <c:if test="${numSelectedRadars > 0}">
                                                    <input type="submit" name="removeRadar"
                                                        title="Remove radar station"
                                                        class="buttonInput" value="-">
                                                </c:if>
                                            </div>
                                            <c:if test="${not empty dsSelectRadarsError}">
                                                <div class="errors">
                                                    <c:out value="${dsSelectRadarsError}"/>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="footer">
                                        <div class="right">
                                            <button class="rounded" type="submit" name="backButton">
                                                <span>Back</span>
                                            </button>
                                            <button class="rounded" type="submit" name="nextButton">
                                                <span>Next</span>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
        <div id="ttRadar" class="tooltip" style="width:170px; height:52px;"></div>
    </body>
</html>
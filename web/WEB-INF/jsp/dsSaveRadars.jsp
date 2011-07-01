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

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Configure data source</title>
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
                            Configure data source <div class="stepno">Step 2</div>
                        </div>
                        <div class="blttext">
                            Select radar stations
                            <div class="hint">
                                Data from selected radar stations will be available with this
                                data source.
                            </div>
                        </div>
                        <div class="table">
                            <div class="dssave">
                                <form method="post" action="dsSaveFileObjects.htm">
                                    <div class="rightcol">
                                        <c:forEach items="${selectedRadars}" var="radar">
                                            <div class="dsparam">
                                                <c:out value="${radar.channelName}"></c:out>
                                                &nbsp;
                                                WMO number: <c:out value="${radar.wmoNumber}"></c:out>
                                                &nbsp;
                                            </div>
                                        </c:forEach>
                                        <div class="row">
                                            <c:if test="${numSelectedRadars < numAvailableRadars}">
                                                <select name="radarsList"
                                                        title="Select radar from the list">
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
                                                <div class="dscontrol">
                                                    <input type="submit" name="addRadar"
                                                        title="Add radar station" value="+">
                                                </div>
                                            </c:if>
                                            <c:if test="${numSelectedRadars > 0}">
                                                <div class="dscontrol">
                                                    <input type="submit" name="removeRadar"
                                                        title="Remove radar station" value="-">
                                                </div>
                                            </c:if>
                                        </div>
                                        <c:if test="${not empty dsSelectRadarsError}">
                                            <div class="error">
                                                <c:out value="${dsSelectRadarsError}"/>
                                            </div>
                                        </c:if>
                                    </div>
                                    <div class="tablefooter">
                                        <div class="buttons">
                                           <button class="rounded" type="submit" name="backButton">
                                                <span>Back</span>
                                            </button>
                                            <button class="rounded" type="submit" name="nextButton">
                                                <span>Next</span>
                                            </button>
                                        </div>
                                    </div>
                                </form>
                            </div>
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
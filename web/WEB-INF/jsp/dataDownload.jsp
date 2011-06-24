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
Document   : Data download status page
Created on : Apr 1, 2011, 12:23 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!--meta name="save" content="history" /-->
        <noscript>
            <style type="text/css"><!--.dspcont{display:block;}--></style>
        </noscript>
        <script type="text/javascript" language="javascript" src="includes/js/expandable.js"></script>
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Data download</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/homeTab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/homeMenu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            <img src="includes/images/icons/download.png" alt="">
                            Data download status
                        </div>
                        <div class="blttext">
                            Data incoming from subscribed data sources.
                        </div>
                        <c:choose>
                            <c:when test="${not empty operators}">
                                <div class="blttext">
                                    Click on node name to view detailed information
                                    on subscribed data sources.
                                </div>
                                <c:forEach var="operator" items="${operators}">
                                <c:set var="op" scope="page" value="${operator}"></c:set>
                                <div class="expandable">
                                    <div class="save">
                                        <div class="item">
                                            <a href="javascript:void(0)" class="dsphead"
                                            onclick="dsp(this)">
                                                <span class="dspchar">
                                                    <img src="includes/images/icons/expand.png"
                                                         alt="+" title="Show">
                                                </span>
                                                <div class="operator">
                                                    <c:out value="${operator}"/>
                                                </div>
                                            </a>
                                        </div>
                                        <div class="dspcont">
                                            <div class="statustable">
                                            <c:choose>
                                                <c:when test="${not empty local}">

                                                    <div class="header">
                                                        <div id="cell" class="station">
                                                            Data source
                                                        </div>
                                                        <div id="cell" class="timestamp">
                                                            Started on
                                                        </div>
                                                        <div id="cell" class="active">
                                                            Status
                                                        </div>
                                                    </div>
                                                    <c:forEach var="sub" items="${local}">
                                                        <c:if test="${sub.operatorName == op}">
                                                            <div class="entry">
                                                                <div id="cell" class="station">
                                                                    <c:out value="${sub.dataSourceName}"/>
                                                                </div>
                                                                <div id="cell" class="timestamp">
                                                                    <c:out value="${sub.dateStr}"/>, <c:out value="${sub.timeStr}"/>
                                                                </div>
                                                                <div id="cell" class="active">
                                                                <c:choose>
                                                                    <c:when test="${sub.active == true}">
                                                                        <img src="includes/images/icons/download.png"
                                                                            alt="active"/>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <img src="includes/images/icons/stop.png"
                                                                            alt="stopped"/>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                                </div>
                                                            </div>
                                                        </c:if>
                                                    </c:forEach>
                                                </c:when>
                                            </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="blttext">
                                    No subscribed data sources found.
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
                   
        
   

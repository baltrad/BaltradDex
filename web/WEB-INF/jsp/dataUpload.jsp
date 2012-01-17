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
Document   : Data upload status page
Created on : Apr 1, 2011, 12:13 PM
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
        <title>BALTRAD | Data upload</title>
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
                            <img src="includes/images/icons/upload.png" alt="">
                            Data upload status
                        </div>
                        <div class="blttext">
                            Data sent to users subscribing local data sources.
                        </div>
                        <c:choose>
                            <c:when test="${not empty users}">
                                <div class="blttext">
                                    Click on user name to view detailed information
                                    on subscriber.
                                </div>
                                <c:forEach var="user" items="${users}">
                                <c:set var="usr" scope="page" value="${user}"></c:set>
                                <div class="expandable">
                                    <div class="save">
                                        <div class="item">
                                            <a href="javascript:void(0)" class="dsphead"
                                            onclick="dsp(this)">
                                                <span class="dspchar">
                                                    <img src="includes/images/icons/expand.png"
                                                     alt="+" title="Show">
                                                </span>
                                                <div class="user">
                                                    <c:out value="${user}"/>
                                                </div>
                                            </a>
                                        </div>
                                        <div class="dspcont">
                                            <div class="statustable">
                                                <c:choose>
                                                    <c:when test="${not empty remote}">
                                                        <div class="header">
                                                            <div id="cell" class="station">
                                                                Data source
                                                            </div>
                                                            <div id="cell" class="timestamp">
                                                                Started on
                                                            </div>
                                                        </div>
                                                        <c:forEach var="sub" items="${remote}">
                                                            <c:if test="${sub.userName == usr}">
                                                                <div class="entry">
                                                                    <div id="cell" class="station">
                                                                        <c:out value="${sub.dataSourceName}"/>
                                                                    </div>
                                                                    <div id="cell" class="timestamp">
                                                                        <c:out value="${sub.dateStr}"/>,
                                                                        <c:out value="${sub.timeStr}"/>
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
                                    Your local data sources are currently not subscribed
                                    by peers.
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
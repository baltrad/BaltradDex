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
Document   : Remove local radar station
Created on : Oct 5, 2010, 11:49 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Remove radar station</title>
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
                            Remove radar station
                        </div>
                        <div class="blttext">
                            Radar stations selected for removal.
                        </div>
                        <div class="table">
                            <div class="removeradar">
                                <form method="post" action="removeRadarStatus.htm">
                                    <div class="tableheader">
                                        <div id="cell" class="count">&nbsp;</div>
                                        <div id="cell" class="station">
                                            Name
                                        </div>
                                        <div id="cell" class="wmonumber">
                                            WMO number
                                        </div>
                                    </div>
                                    <c:set var="count" scope="page" value="1"/>
                                    <c:forEach var="channel" items="${channels}">
                                        <div class="entry">
                                            <div id="cell" class="count">
                                                <c:out value="${count}"/>
                                                <c:set var="count" value="${count + 1}"/>
                                            </div>
                                            <div id="cell" class="station">
                                                <c:out value="${channel.channelName}"/>
                                            </div>
                                            <div id="cell" class="wmonumber">
                                                <c:out value="${channel.wmoNumber}"/>
                                            </div>
                                            <div class="hidden">
                                                <input type="checkbox" name="removed_channels"
                                                    value="${channel.id}" checked/>
                                            </div>
                                        </div>
                                    </c:forEach>
                                    <div class="tablefooter">
                                        <div class="buttons">
                                            <button class="rounded" type="button"
                                                onclick="history.go(-1);">
                                                <span>Back</span>
                                            </button>
                                            <button class="rounded" type="submit">
                                               <span>OK</span>
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
                
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
Document   : Save local radar station
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
        <title>BALTRAD | Add radar station</title>
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
                            Save radar station
                        </div>
                        <div class="blttext">
                            Save new local radar station or modify an existing one.
                        </div>
                        <div class="table">
                            <div class="addradar">
                                <%@include file="/WEB-INF/jsp/formMessages.jsp"%>
                                <form method="post">
                                    <div class="leftcol">
                                        <div class="row">Radar station name</div>
                                        <div class="row">WMO number</div>
                                    </div>
                                    <div class="rightcol">
                                        <div class="row">
                                            <div class="radarname">
                                                <form:input path="command.channelName"/>
                                                <div class="hint">
                                                   Name of the local radar station
                                                </div>
                                            </div>
                                            <form:errors path="command.channelName" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="wmonumber">
                                                <form:input path="command.wmoNumber"/>
                                                <div class="hint">
                                                   Radar station's WMO number as string
                                                </div>
                                            </div>
                                            <form:errors path="command.wmoNumber" cssClass="error"/>
                                        </div>
                                    </div>
                                    <div class="tablefooter">
                                       <div class="buttons">
                                           <button class="rounded" type="button"
                                               onclick="window.location.href='settings.htm'">
                                               <span>Back</span>
                                           </button>
                                           <button class="rounded" type="submit">
                                               <span>Save</span>
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

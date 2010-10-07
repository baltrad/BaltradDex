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
Document   : Edit local radar station
Created on : Oct 5, 2010, 11:41 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Edit radar</title>
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
                            Edit local radar station
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        List of local radar stations. Click on station name in order to
                        modify radar settings.
                    </div>
                    <div id="table">
                        <display:table name="registered_channels" id="channel" defaultsort="1"
                            cellpadding="0" cellspacing="2" export="false" class="tableborder">
                            <display:column sortable="true" title="Radar station"
                                href="saveLocalChannel.htm" sortProperty="channelName"
                                class="tdcenter" paramProperty="id" paramId="id"
                                value="${channel.channelName}">
                            </display:column>
                            <display:column sortable="true" title="WMO number"
                                sortProperty="wmoNumber" class="tdcenter"
                                value="${channel.wmoNumber}">
                            </display:column>
                        </display:table>
                        <div class="footer">
                            <div class="right">
                                <form action="configuration.htm">
                                    <button class="rounded" type="submit">
                                        <span>Back</span>
                                    </button>
                                </form>
                            </div>
                        </div>
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

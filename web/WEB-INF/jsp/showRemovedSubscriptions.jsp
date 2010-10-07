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
Document   : Remove subscribed radar station
Created on : Oct 5, 2010, 3:06 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Remove subscription</title>
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
                            Remove subscribed radar station
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        Warning: The following subscribed radar station(s) will be removed
                        from the system.
                    </div>
                    <div id="table">
                        <form method="post" action="showSubscriptionRemovalStatus.htm">
                            <display:table name="selected_subscriptions"
                                id="subscription" defaultsort="1" requestURI="submit.htm"
                                export="false" cellpadding="0" cellspacing="2"
                                class="tableborder">
                                <display:column sortable="true" title="Radar station"
                                    sortProperty="channelName"
                                    paramProperty="channelName" class="tdcenter"
                                    value="${subscription.channelName}">
                                </display:column>
                                <display:column sortable="true" title="Operator"
                                    sortProperty="operatorName"
                                    paramProperty="operatorName" class="tdcenter"
                                    value="${subscription.operatorName}">
                                </display:column>
                            </display:table>
                            <div class="footer">
                                <div class="right">
                                    <button class="rounded" type="button"
                                        onclick="history.go(-1);">
                                        <span>Back</span>
                                    </button>
                                    <button class="rounded" type="submit">
                                        <span>Submit</span>
                                    </button>
                                </div>
                            </div>
                        </form>
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

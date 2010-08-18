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
Document   : Page displaying subscription status
Created on : Jun 24, 2010, 8:57:42 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ page import="java.util.List" %>
<%
    // Check if subscription list is not empty
    List subsStatus = ( List )request.getAttribute( "subscribed_channels" );
    if( subsStatus == null || subsStatus.size() <= 0 ) {
        request.getSession().setAttribute( "subs_status", 0 );
    } else {
        request.getSession().setAttribute( "subs_status", 1 );
    }
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Subscription confirmation</title>
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
                        <h1>Subscription confirmation</h1>
                        <br/>
                        <c:choose>
                            <c:when test="${subs_status == 1}">
                            <h2>
                                <p>
                                Your subscription is now submitted.
                                </p>
                                <p>
                                Following data channels have been added to your subscriptions page:
                                </p>
                                <br/>
                            </h2>
                                <display:table name="subscribed_channels" id="channel"
                                    defaultsort="1" 
                                    requestURI="showRemoteChannelsSelectionStatus.htm"
                                    cellpadding="0" cellspacing="2" export="false"
                                    class="tableborder">
                                    <display:column sortProperty="wmoNumber" sortable="true"
                                        paramId="wmoNumber" title="Channel WMO number"
                                        class="tdcenter" value="${channel.wmoNumber}">
                                    </display:column>
                                    <display:column sortable="true" title="Channel name"
                                        sortProperty="channelName" paramId="channelName"
                                        paramProperty="channelName" class="tdcenter"
                                        value="${channel.channelName}">
                                    </display:column>
                                </display:table>
                            </c:when>
                            <c:otherwise>
                                <div id="message-box">
                                    The remote system failed to complete your subscription request.
                                    Try again or contact remote node administrator.
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <div id="table-footer">
                            <form action="showSubscriptions.htm">
                                <input type="submit" value="Subscriptions"
                                       name="show_subscriptions_button"/>
                            </form>
                        </div>
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

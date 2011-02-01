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
Document   : Subscription management page
Created on : Sep 30, 2010, 16:34 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="java.util.List" %>
<%
    // Check if list of available subscriptions is not empty
    List avSubs = ( List )request.getAttribute( "subscriptions" );
    if( avSubs == null || avSubs.size() <= 0 ) {
        request.getSession().setAttribute( "av_subs_status", 0 );
    } else {
        request.getSession().setAttribute( "av_subs_status", 1 );
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Subscriptions</title>
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
                            Subscription management
                        </div>
                        <div class="right">
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${av_subs_status == 1}">
                            <div id="text-box">
                                Click on a check box to subscribe or unsubscribe a desired
                                radar station.
                                Click on radar name in order to browse data from this radar.
                            </div>
                            <div id="table">
                                <form action="showSelectedSubscriptions.htm">
                                    <display:table name="subscriptions" id="subscription" 
                                        cellpadding="0" cellspacing="2" export="false"
                                        class="tableborder" requestURI="showSubscriptions.htm">
                                        <display:column sortable="true" title="Radar station"
                                            href="radarData.htm" sortProperty="channelName"
                                            paramId="channelName" paramProperty="channelName"
                                            class="tdcenter" value="${subscription.channelName}">
                                        </display:column>
                                         <c:choose>
                                            <c:when test="${subscription.synkronized == true}">
                                                <display:column sortable="false"
                                                    title="Status" class="tdcheck">
                                                    <img src="includes/images/green_bulb.png"
                                                         alt="green_bulb"/>
                                                </display:column>
                                            </c:when>
                                            <c:otherwise>
                                                <display:column sortable="false"
                                                    title="Status" class="tdcheck">
                                                    <img src="includes/images/red_bulb.png"
                                                         alt="red_bulb"/>
                                                </display:column>
                                            </c:otherwise>
                                        </c:choose>
                                        <display:column sortable="true" title="Operator"
                                            sortProperty="operatorName" paramId=""
                                            paramProperty="operatorName" class="tdcenter"
                                            value="${subscription.operatorName}">
                                        </display:column>
                                        <c:choose>
                                            <c:when test="${subscription.selected == true}">
                                                <display:column sortable="false"
                                                    title="Subscription" class="tdcheck">
                                                    <input type="checkbox" name="selected_channels"
                                                    value="${subscription.channelName}" checked/>
                                                </display:column>
                                            </c:when>
                                            <c:otherwise>
                                                <display:column sortable="false"
                                                    title="Subscription" class="tdcheck">
                                                    <input type="checkbox" name="selected_channels"
                                                    value="${subscription.channelName}"/>
                                                </display:column>
                                            </c:otherwise>
                                        </c:choose>
                                    </display:table>
                                    <div class="footer">
                                        <div class="right">
                                            <button class="rounded" type="submit">
                                                <span>Submit</span>
                                            </button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-alert.png"
                                         alt="no_radars"/>
                                </div>
                                <div class="text">
                                    List of radar stations available for subscription is
                                    currently empty.
                                    Use node connection functionality to add new radars.
                                </div>
                            </div>
                            <div class="footer">
                                <div class="right">
                                    <form action="connectToNode.htm">
                                        <button class="rounded" type="submit">
                                            <span>OK</span>
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <script type="text/javascript" src="includes/footer.js"></script>
        </div>
    </body>
</html>


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
Document   : System home page
Created on : Sep 23, 2010, 12:20 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<jsp:useBean id="initAppUtil" scope="session" class="eu.baltrad.dex.util.InitAppUtil">
</jsp:useBean>
<jsp:useBean id="securityManager" scope="session"
             class="eu.baltrad.dex.util.ApplicationSecurityManager"></jsp:useBean>

<%
    User user = ( User )securityManager.getUser( request );
    String userName = user.getName();
    String nodeName = initAppUtil.getNodeName();
    String operator = initAppUtil.getOrgName();
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Home</title>
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
                            Home
                        </div>
                        <div class="right">

                        </div>
                    </div>
                    <div id="text-box">
                        <div class="title">
                            Welcome to Baltrad Radar Data Exchange & Processing System!
                        </div>
                    </div>
                    <div id="text-box">
                        Baltrad is running on <%=nodeName%> operated by <%=operator%>.
                    </div>
                    <div id="text-box">
                        You are logged in as user <%=userName%>.
                    </div>
                    <div id="status-box">
                        <div class="bottom">
                            Data exchange status
                        </div>
                    </div>
                    <div id="status-box">
                        <div class="icon">
                            <img src="includes/images/icons/arrow-down.png" alt="remote_radars"/>
                        </div>
                        <div class="text">
                            Incoming data | Subscribed remote radar stations
                        </div>
                        <c:choose>
                            <c:when test="${not empty local_subscriptions}">
                                <div id="status-table">
                                    <display:table name="local_subscriptions" id="local_sub"
                                        defaultsort="1" cellpadding="0" cellspacing="2" 
                                        export="false" class="tableborder" requestURI="home.htm">
                                        <display:column sortable="true" title="Radar station"
                                            sortProperty="channelName" paramId="channelName"
                                            paramProperty="channelName"
                                            class="tdcenter" value="${local_sub.channelName}">
                                        </display:column>
                                        <display:column sortable="true" title="Operator"
                                            sortProperty="operatorName" paramId="operatorName"
                                            paramProperty="operatorName" class="tdcenter"
                                            value="${local_sub.operatorName}">
                                        </display:column>
                                        <c:choose>
                                            <c:when test="${local_sub.selected == true}">
                                                <display:column sortable="false" title="Status"
                                                    class="tdcheck">
                                                    <img src="includes/images/green_bulb.png"
                                                         alt="green_bulb"/>
                                                </display:column>
                                            </c:when>
                                            <c:otherwise>
                                                <display:column sortable="false" title="Status"
                                                    class="tdcheck">
                                                    <img src="includes/images/red_bulb.png"
                                                         alt="red_bulb"/>
                                                </display:column>
                                            </c:otherwise>
                                        </c:choose>
                                    </display:table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="message">
                                    <li type="circle">
                                        You currently have no peer radar stations subscribed.
                                    </li>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div id="status-box">
                        <div class="icon">
                            <img src="includes/images/icons/arrow-up.png" alt="remote_radars"/>
                        </div>
                        <div class="text">
                            Outgoing data | Local radar stations subscribed by the users
                        </div>
                        <c:choose>
                            <c:when test="${not empty remote_subscriptions}">
                                <div id="status-table">
                                    <display:table name="remote_subscriptions" id="remote_sub"
                                        defaultsort="1" cellpadding="0" cellspacing="2" 
                                        export="false" class="tableborder" requestURI="home.htm">
                                        <display:column sortable="true" title="Radar station"
                                            sortProperty="channelName" paramId="channelName"
                                            paramProperty="channelName"
                                            class="tdcenter" value="${remote_sub.channelName}">
                                        </display:column>
                                        <display:column sortable="true" title="User name"
                                            sortProperty="userName" paramId="userName"
                                            paramProperty="userName"
                                            class="tdcenter" value="${remote_sub.userName}">
                                        </display:column>
                                    </display:table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="message">
                                    <li type="circle">
                                        Your local radar stations are currently not subscribed
                                        by peers.
                                    </li>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="footer"></div>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <script type="text/javascript" src="includes/footer.js"></script>
        </div>
    </body>
</html>

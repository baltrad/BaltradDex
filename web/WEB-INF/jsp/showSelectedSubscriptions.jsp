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
Document   : Subscription slection page
Created on : Sep 30, 2010, 16:34 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="java.util.List" %>
<%
    int request_status = ( Integer )request.getAttribute( "request_status" );
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
                        <c:when test="${request_status == 1}">
                            <div id="text-box">
                                Please confirm your subscription request.
                            </div>
                            <div id="table-content">
                                <form action="showSubscriptionStatus.htm">
                                    <div id="table">
                                        <div id="selectedsubscriptions">
                                            <div class="table-hdr">
                                                <div class="station">
                                                    Data source
                                                </div>
                                                <div class="operator">
                                                    Operator
                                                </div>
                                                <div class="request">
                                                    Request
                                                </div>
                                            </div>
                                            <c:forEach var="sub" items="${selectedSubscriptions}">
                                                <div class="table-row">
                                                    <div class="station">
                                                        <c:out value="${sub.dataSourceName}"/>
                                                    </div>
                                                    <div class="operator">
                                                        <c:out value="${sub.operatorName}"/>
                                                    </div>
                                                    <div class="request">
                                                        <c:choose>
                                                            <c:when test="${sub.active == true}">
                                                                <div class="tdgreen">
                                                                    <c:out value="Activate"/>
                                                                </div>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="tdred">
                                                                    <c:out value="Deactivate"/>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                            <div class="footer">
                                                <div class="right">
                                                    <button class="rounded" type="button"
                                                        onclick="window.location='showSubscriptions.htm'">
                                                        <span>Back</span>
                                                    </button>
                                                    <button class="rounded" type="submit">
                                                        <span>Submit</span>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </c:when>
                        <c:when test="${request_status == 2}">
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-alert.png"
                                         alt="no_radars"/>
                                </div>
                                <div class="text">
                                    Confirm your request in order to cancel all your
                                    active subscriptions.
                                </div>
                            </div>
                            <div class="footer">
                                <div class="right">
                                    <form action="showSubscriptionStatus.htm">
                                        <button class="rounded" type="submit">
                                            <span>Submit</span>
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-alert.png"
                                         alt="no_radars"/>
                                </div>
                                <div class="text">
                                    Your subscription status was not changed.
                                    Click OK to go back to the selection page.
                                </div>
                            </div>
                            <div class="footer">
                                <div class="right">
                                    <form action="showSubscriptions.htm">
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
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>
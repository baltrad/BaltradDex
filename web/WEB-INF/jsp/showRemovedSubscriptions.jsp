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
Document   : Subscription selection page
Created on : Jun 24, 2010, 8:56:35 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ page import="java.util.List" %>
<%
    int request_status = ( Integer )request.getAttribute( "request_status" );
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Subscription request</title>
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
                        <h1>Subscription request</h1>
                        <br/>
                        <c:choose>
                            <c:when test="${request_status == 1}">
                                <h2>
                                    <p>
                                    Confirm your subscription request by clicking on OK
                                    button.
                                    </p>
                                </h2>
                                <div id="table-content">
                                    <form action="showSubscriptionStatus.htm">
                                        <display:table name="selected_subscriptions"
                                            id="subscription" defaultsort="1" requestURI="submit.htm"
                                            export="false" cellpadding="0" cellspacing="2"
                                            class="tableborder">
                                            <display:column sortable="true" title="Channel"
                                                sortProperty="channelName"
                                                paramProperty="channelName" class="tdcenter"
                                                value="${subscription.channelName}">
                                            </display:column>
                                            <display:column sortable="true" title="Operator"
                                                sortProperty="operatorName"
                                                paramProperty="operatorName" class="tdcenter"
                                                value="${subscription.operatorName}">
                                            </display:column>
                                            <display:column sortable="true" title="Node address"
                                                sortProperty="nodeAddress"
                                                paramProperty="nodeAddress" class="tdcenter"
                                                value="${subscription.nodeAddress}">
                                            </display:column>
                                            <c:choose>
                                                <c:when test="${subscription.selected == true}">
                                                    <display:column sortable="false"
                                                        title="Request status" class="tdcenter-green"
                                                        value="Online">
                                                    </display:column>
                                                </c:when>
                                                <c:otherwise>
                                                    <display:column sortable="false"
                                                        title="Request status" class="tdcenter-red"
                                                        value="Offline">
                                                    </display:column>
                                                </c:otherwise>
                                            </c:choose>
                                        </display:table>
                                        <div id="table-footer-leftcol">
                                            <input type="submit" value="OK" name="submit_button"/>
                                        </div>
                                    </form>
                                    <div id="table-footer-rightcol">
                                        <form action="showSubscriptions.htm">
                                            <input type="submit" value="Back" name="back_button"/>
                                        </form>
                                    </div>
                                </div>
                            </c:when>
                            <c:when test="${request_status == 2}">
                                <div id="message-box">
                                    Click on Submit button now to cancel all your
                                    active subscriptions.
                                </div>
                                <div id="table-footer">
                                    <div id="table-footer-leftcol">
                                        <form action="showSubscriptionStatus.htm">
                                            <input type="submit" value="Submit" name="submit_button"/>
                                        </form>
                                    </div>
                                    <div id="table-footer-rightcol">
                                        <form action="showSubscriptions.htm">
                                            <input type="submit" value="Cancel" name="cancel_button"/>
                                        </form>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div id="message-box">
                                    Your subscription status was not changed.
                                    Click on OK button to go back to the selection page.
                                </div>
                                <div id="table-footer">
                                    <div id="table-footer-rightcol">
                                        <form action="showSubscriptions.htm">
                                            <input type="submit" value="OK" name="cancel_button"/>
                                        </form>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
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

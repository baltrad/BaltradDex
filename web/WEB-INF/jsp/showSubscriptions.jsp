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
Document   : Data channel subscription page
Created on : Jun 24, 2010, 8:55:52 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Channel subscription</title>
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
                        <h1>Channel subscription status</h1>
                        <br/>
                        <h2>
                            <p>
                            List of active subscriptions.
                            Click on a check box to subscribe or unsubscribe a desired data channel.
                            </p>
                        </h2>
                        <form action="showSelectedSubscriptions.htm">
                            <display:table name="subscriptions" id="subscription" defaultsort="1"
                                requestURI="showSubscriptions.htm" cellpadding="0" cellspacing="2"
                                export="false" class="tableborder">
                                <display:column sortProperty="id" sortable="true"
                                    title="Channel ID" class="tdcenter">
                                    <fmt:formatNumber value="${subscription.id}"
                                    pattern="00" />
                                </display:column>
                                <display:column sortable="true" title="Channel name"
                                    sortProperty="name" paramId="name" paramProperty="name"
                                    class="tdcenter" value="${subscription.name}">
                                </display:column>
                                <c:choose>
                                    <c:when test="${subscription.selected == true}">
                                        <display:column sortable="false" title="Subscription status"
                                            class="tdcheck"> <input type="checkbox"
                                            name="selected_subscriptions" value="${subscription.name}"
                                            checked/>
                                        </display:column>
                                    </c:when>
                                    <c:otherwise>
                                        <display:column sortable="false" title="Subscription status"
                                            class="tdcheck"> <input type="checkbox"
                                            name="selected_subscriptions" value="${subscription.name}"/>
                                        </display:column>
                                    </c:otherwise>
                                </c:choose>
                            </display:table>
                            <div id="table-footer">
                                <input type="submit" value="Submit" name="submitButton"/>
                            </div>
                        </form>
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




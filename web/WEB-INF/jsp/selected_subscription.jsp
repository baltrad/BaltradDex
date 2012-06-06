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
<%
    int request_status = ( Integer )request.getAttribute( "request_status" );
%>

<t:page_tabbed pageTitle="Subscriptions" activeTab="exchange">
    <jsp:body>
        <div class="left">
            <t:menu_exchange/>
        </div>
        <div class="right">
            <div class="blttitle">
                Subscription management
            </div>
            <c:choose>
                <c:when test="${request_status == 1}">
                    <div class="blttext">
                        Confirm your subscription request.
                    </div>
                    <div class="table">
                        <div class="subscriptions">
                            <form action="subscription_status.htm" method="post">
                                <div class="tableheader">
                                    <div id="cell" class="count">&nbsp;</div>
                                    <div id="cell" class="name">
                                        Data source
                                    </div>
                                    <div id="cell" class="operator">
                                        Operator
                                    </div>
                                    <div id="cell" class="active">
                                        Request
                                    </div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach items="${selectedSubscriptions}" var="sub">
                                    <div class="entry">
                                        <div id="cell" class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
                                        </div>
                                        <div id="cell" class="name">
                                            <c:out value="${sub.dataSourceName}"/>
                                        </div>
                                        <div id="cell" class="operator">
                                            <c:out value="${sub.operatorName}"/>
                                        </div>
                                        <div id="cell" class="active">
                                            <c:choose>
                                                <c:when test="${sub.active == true}">
                                                    <img src="includes/images/icons/download.png" 
                                                         alt="Start" title="Start subscription">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="includes/images/icons/stop.png" 
                                                         alt="Stop" title="Stop subscription">
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </c:forEach>
                                <div class="tablefooter">
                                    <button class="rounded" type="button"
                                        onclick="window.location='subscription.htm'">
                                        <span>Back</span>
                                    </button>
                                    <button class="rounded" type="submit">
                                        <span>OK</span>
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </c:when>
                <c:when test="${request_status == 2}">
                    <div class="blttext">
                        Confirm your request in order to cancel all your
                        active subscriptions.
                    </div>
                    <div class="table">
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                    onclick="window.location.href='subscription_status.htm'">
                                    <span>OK</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="blttext">
                        Your subscription status was not changed.
                        Click OK to go back to subscription management page.
                    </div>
                    <div class="table">
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                    onclick="window.location.href='subscription.htm'">
                                    <span>OK</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </jsp:body>    
</t:page_tabbed>

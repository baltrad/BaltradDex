<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW

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
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/exchangeTab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/exchangeMenu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            Subscription management
                        </div>
                        <c:choose>
                            <c:when test="${av_subs_status == 1}">
                                <div class="blttext">
                                    Click on a check box to subscribe or unsubscribe a desired
                                    data source.
                                </div>
                                <div class="table">
                                    <div class="subscriptions">
                                        <form action="showSelectedSubscriptions.htm" method="post">
                                            <div class="tableheader">
                                                <div id="cell" class="count">&nbsp;</div>
                                                <div id="cell" class="name">
                                                    Data source
                                                </div>
                                                <div id="cell" class="operator">
                                                    Operator
                                                </div>
                                                <div id="cell" class="active">
                                                    Status
                                                </div>
                                            </div>
                                            <c:set var="count" scope="page" value="1"/>
                                            <c:forEach items="${subscriptions}" var="sub">
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
                                                                <input type="checkbox" name="selectedDataSources"
                                                                    value="${sub.dataSourceName}" checked/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <input type="checkbox" name="selectedDataSources"
                                                                    value="${sub.dataSourceName}"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                            <div class="tablefooter">
                                                <div class="buttons">
                                                    <button class="rounded" type="submit">
                                                        <span>OK</span>
                                                    </button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="blttext">
                                    List of subscribed data sources is currently empty.
                                    Use node connection functionality to add new data sources.
                                </div>
                                <div class="table">
                                    <div class="tablefooter">
                                        <div class="buttons">
                                            <button class="rounded" type="button"
                                                onclick="window.location.href='connectToNode.htm'">
                                                <span>OK</span>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
        <div id="bltfooter">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>
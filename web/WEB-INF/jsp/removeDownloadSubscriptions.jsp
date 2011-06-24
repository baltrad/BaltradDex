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
Document   : Subscription management page. Allows to remove data surce subscribed by local node.
Created on : Oct 5, 2010, 2:01 PM
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
        <title>Baltrad | Subscription management</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/settingsTab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/settingsMenu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            Subscription management - data download
                        </div>
                        <c:choose>
                            <c:when test="${av_subs_status == 1}">
                                <div class="blttext">
                                    List of data sources subscribed by this node.
                                    Select data sources to be removed from subscription list.
                                </div>
                                <div class="table">
                                    <div class="removesubscriptions">
                                        <%@include file="/WEB-INF/jsp/genericMessages.jsp"%>
                                        <form action="downloadSubscriptionsToRemove.htm" method="post">
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
                                                <div id="cell" class="check">
                                                    Select
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
                                                                <img src="includes/images/icons/download.png"
                                                                     alt="Active" title="Active subscription"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <img src="includes/images/icons/stop.png"
                                                                     alt="Stopped" title="Cancelled subscription"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div id="cell" class="check">
                                                        <input type="checkbox" name="selectedDataSources"
                                                            value="${sub.dataSourceName}"/>
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
                                </div>
                                <div class="table">
                                    <div class="tablefooter">
                                        <div class="buttons">
                                            <button class="rounded" type="button"
                                                onclick="window.location.href='settings.htm'">
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
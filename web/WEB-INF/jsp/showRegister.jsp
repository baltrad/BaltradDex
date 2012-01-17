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
Document   : Data delivery register
Created on : Oct 6, 2010, 10:49 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="eu.baltrad.dex.registry.model.DeliveryRegisterManager"%>
<%@page import="eu.baltrad.dex.registry.controller.RegisterController"%>
<%@page import="java.util.List"%>

<%
    // get delivery register
    List deliveryRegister = ( List )request.getAttribute( "entries" );
    if( deliveryRegister == null || deliveryRegister.size() <= 0 ) {
        request.getSession().setAttribute( "register_status", 0 );
    } else {
        request.getSession().setAttribute( "register_status", 1 );
    }
    DeliveryRegisterManager manager = new DeliveryRegisterManager();
    RegisterController controller = new RegisterController();
    long numEntries = manager.countEntries();
    int numPages = ( int )Math.ceil( numEntries / DeliveryRegisterManager.ENTRIES_PER_PAGE );
    if( numPages < 1 ) {
        numPages = 1;
    }
    int currentPage = controller.getCurrentPage();
    int scrollStart = ( DeliveryRegisterManager.SCROLL_RANGE - 1 ) / 2;
    int firstPage = 1;
    int lastPage = DeliveryRegisterManager.SCROLL_RANGE;
    if( numPages <= DeliveryRegisterManager.SCROLL_RANGE && currentPage <=
            DeliveryRegisterManager.SCROLL_RANGE ) {
        firstPage = 1;
        lastPage = numPages;
    }
    if( numPages > DeliveryRegisterManager.SCROLL_RANGE && currentPage > scrollStart &&
            currentPage < numPages - scrollStart ) {
        firstPage = currentPage - scrollStart;
        lastPage = currentPage + scrollStart;
    }
    if( numPages > DeliveryRegisterManager.SCROLL_RANGE && currentPage > scrollStart &&
            currentPage >= numPages - ( DeliveryRegisterManager.SCROLL_RANGE - 1 ) ) {
        firstPage = numPages - ( DeliveryRegisterManager.SCROLL_RANGE - 1 );
        lastPage = numPages;
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>BALTRAD | Data delivery registry</title>
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
                            Data delivery registry
                        </div>
                        <c:choose>
                            <c:when test="${register_status == 1}">
                                <div class="blttext">
                                    All data delivery registry entries.
                                </div>
                                <div class="table">
                                    <div class="register">
                                        <div id="tablecontrol">
                                            <c:set var="curPage" scope="page" value="<%=currentPage%>"/>
                                            <form action="showRegister.htm" method="post">
                                                <input type="submit" name="pagenum" value="<<"
                                                       title="First page">
                                                <span></span>
                                                <input type="submit" name="pagenum" value="<"
                                                       title="Previous page">
                                                <span></span>
                                                <c:forEach var="i" begin="<%=firstPage%>" end="<%=lastPage%>"
                                                           step="1" varStatus ="status">
                                                        <c:choose>
                                                            <c:when test="${curPage == i}">
                                                                <input style="background:#FFFFFF" type="submit"
                                                                       name="pagenum" value="${i}">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <input type="submit" name="pagenum" value="${i}">
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:forEach>
                                                <span></span>
                                                <input type="submit" name="pagenum" value=">"
                                                       title="Next page">
                                                <span></span>
                                                <input type="submit" name="pagenum" value=">>"
                                                       title="Last page">
                                            </form>
                                        </div>
                                        <div class="tableheader">
                                            <div id="cell" class="date">
                                                Date
                                            </div>
                                            <div id="cell" class="time">
                                                Time
                                            </div>
                                            <div id="cell" class="recipient">
                                                Recipient
                                            </div>
                                            <div id="cell" class="signature">
                                                File signature
                                            </div>
                                            <div id="cell" class="status">
                                                Status
                                            </div>
                                        </div>
                                        <c:forEach var="entry" items="${entries}">
                                            <div class="entry">
                                                <div id="cell" class="date">
                                                    <c:out value="${fn:substring(entry.timeStamp, 0, 10)}"/>
                                                </div>
                                                <div id="cell" class="time">
                                                    <c:out value="${fn:substring(entry.timeStamp, 10, 19)}"/>
                                                </div>
                                                <div id="cell" class="recipient">
                                                    <c:out value="${entry.userName}"/>
                                                </div>
                                                <div id="cell" class="signature">
                                                    <c:out value="${entry.uuid}"/>
                                                </div>
                                                <div id="cell" class="status">
                                                    <c:choose>
                                                        <c:when test="${entry.deliveryStatus == 'SUCCESS'}">
                                                            <img src="includes/images/icons/success.png"
                                                                 alt="Success" title="Delivery success">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img src="includes/images/icons/failure.png"
                                                                 alt="Failure" title="Delivery failure">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </c:forEach>
                                        <div class="tablefooter">
                                            <div class="buttons">
                                                <button class="rounded" type="button"
                                                    onclick="window.location.href='exchange.htm'">
                                                    <span>Back</span>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="blttext">
                                    Data delivery registry is empty.
                                </div>
                                <div class="table">
                                    <div class="tablefooter">
                                        <div class="buttons">
                                            <button class="rounded" type="button"
                                                onclick="window.location.href='exchange.htm'">
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
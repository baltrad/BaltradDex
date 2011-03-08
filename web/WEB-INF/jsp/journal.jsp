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
Document   : System messages
Created on : Sep 30, 2010, 14:45 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="eu.baltrad.dex.log.model.LogManager"%>
<%@page import="eu.baltrad.dex.log.controller.JournalController"%>

<%
    LogManager manager = new LogManager();
    JournalController controller = new JournalController();
    int numEntries = manager.countEntries();
    int numPages = ( int )Math.ceil( numEntries / LogManager.ENTRIES_PER_PAGE );
    if( numPages < 1 ) {
        numPages = 1;
    }
    int currentPage = controller.getCurrentPage();
    int scrollStart = ( LogManager.SCROLL_RANGE - 1 ) / 2;
    int firstPage = 1;
    int lastPage = LogManager.SCROLL_RANGE;
    if( numPages <= LogManager.SCROLL_RANGE && currentPage <= LogManager.SCROLL_RANGE ) {
        firstPage = 1;
        lastPage = numPages;
    }
    if( numPages > LogManager.SCROLL_RANGE && currentPage > scrollStart && currentPage < numPages
            - scrollStart ) {
        firstPage = currentPage - scrollStart;
        lastPage = currentPage + scrollStart;
    }
    if( numPages > LogManager.SCROLL_RANGE && currentPage > scrollStart && currentPage >= numPages
            - ( LogManager.SCROLL_RANGE - 1 ) ) {
        firstPage = numPages - ( LogManager.SCROLL_RANGE - 1 );
        lastPage = numPages;
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | System messages</title>
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
                            Full system log
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        Full system log. Use control bar below to display pages.
                    </div>
                    <div id="table">
                        <div id="table-control">
                            <c:set var="curPage" scope="page" value="<%=currentPage%>"/>
                            <form action="journal.htm" method="post">
                                <input type="submit" name="pagenum" value="First">
                                <span></span>
                                <input type="submit" name="pagenum" value="<">
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
                                <input type="submit" name="pagenum" value=">">
                                <span></span>
                                <input type="submit" name="pagenum" value="Last">
                            </form>
                        </div>
                        <div id="logtable">
                            <div class="hdr">
                                <div class="date">
                                    Date
                                </div>
                                <div class="time">
                                    Time
                                </div>
                                <div class="flag">
                                    Flag
                                </div>
                                <div class="msg">
                                    Message
                                </div>
                            </div>
                            <c:forEach var="entry" items="${entries}">
                                <% String style = ""; %>
                                <c:choose>
                                    <c:when test="${entry.type == 'INFO'}">
                                        <%
                                            style = "info";
                                        %>
                                    </c:when>
                                    <c:when test="${entry.type == 'WARNING'}">
                                        <%
                                            style = "warning";
                                        %>
                                    </c:when>
                                    <c:when test="${entry.type == 'ERROR'}">
                                        <%
                                            style = "error";
                                        %>
                                    </c:when>
                                </c:choose>
                                <div class="row">
                                    <div class="date">
                                        <div class="<%=style%>">
                                            <c:out value="${fn:substring(entry.timeStamp, 0, 10)}"/>
                                        </div>
                                    </div>
                                    <div class="time">
                                        <div class="<%=style%>">
                                            <c:out value="${fn:substring(entry.timeStamp, 10, 19)}"/>
                                        </div>
                                    </div>
                                    <c:choose>
                                        <c:when test="${entry.type == 'ERROR'}">
                                            <div class="flag">
                                                <img src="includes/images/red_bulb.png"
                                                     alt="error"/>
                                            </div>
                                        </c:when>
                                        <c:when test="${entry.type == 'WARNING'}">
                                            <div class="flag">
                                                <img src="includes/images/blue_bulb.png"
                                                     alt="warn"/>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="flag">
                                                <img src="includes/images/green_bulb.png"
                                                     alt="ok"/>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="msg">
                                        <div class="<%=style%>">
                                            <c:out value="${entry.message}"/>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach> 
                        </div>
                        <div class="footer">
                            <div class="right">
                                <form action="log.htm">
                                    <button class="rounded" type="submit">
                                        <span>Show latest</span>
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <script type="text/javascript" src="includes/footer.js"></script>
        </div>
    </body>
</html>
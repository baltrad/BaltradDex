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

<%@page import="eu.baltrad.dex.register.model.DeliveryRegisterManager"%>
<%@page import="eu.baltrad.dex.register.controller.RegisterController"%>
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
        <title>Baltrad | Delivery register</title>
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
                            Data delivery register
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <c:choose>
                        <c:when test="${register_status == 1}">
                            <div id="text-box">
                                Data delivery register.
                            </div>
                            <div id="table">
                                <div id="table-control">
                                    <c:set var="curPage" scope="page" value="<%=currentPage%>"/>
                                    <form action="showRegister.htm" method="post">
                                        <input type="submit" name="pagenum" value="<<">
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
                                        <input type="submit" name="pagenum" value=">>">
                                    </form>
                                </div>
                                <div id="register">
                                    <div class="table-hdr">
                                        <div class="date">
                                            Date
                                        </div>
                                        <div class="time">
                                            Time
                                        </div>
                                        <div class="recipient">
                                            Recipient
                                        </div>
                                        <div class="uuid">
                                            File signature
                                        </div>
                                        <div class="status">
                                            Delivery status
                                        </div>
                                    </div>
                                    <c:forEach var="entry" items="${entries}">
                                        <div class="table-row">
                                            <div class="date">
                                                <c:out value="${fn:substring(entry.timeStamp, 0, 10)}"/>
                                            </div>
                                            <div class="time">
                                                <c:out value="${fn:substring(entry.timeStamp, 10, 19)}"/>
                                            </div>
                                            <div class="recipient">
                                                <c:out value="${entry.userName}"/>
                                            </div>
                                            <div class="uuid">
                                                <c:out value="${entry.uuid}"/>
                                            </div>
                                            <div class="status">
                                                <c:out value="${entry.deliveryStatus}"/>
                                            </div> 
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-alert.png"
                                         alt="no_entries"/>
                                </div>
                                <div class="text">
                                    No entries found in data delivery register.
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>  
                    <div class="footer">
                        <div class="right">
                            <button class="rounded" type="button"
                                onclick="window.location='configuration.htm'">
                                <span>Back</span>
                            </button>
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

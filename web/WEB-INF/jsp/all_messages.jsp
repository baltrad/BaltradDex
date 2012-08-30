<%------------------------------------------------------------------------------
Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW

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
--------------------------------------------------------------------------------
Document   : All system messages
Created on : Sep 30, 2010, 14:45 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>BALTRAD | System messages</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/home_tab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/home_menu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            All system messages
                        </div>
                        <div class="blttext">
                            Full system log listing.
                        </div>
                        <div class="table">
                            <div class="log">
                                <div id="tablecontrol">
                                    <c:set var="curPage" scope="page" value="${current_page}"/>
                                    <form action="all_messages.htm" method="post">
                                        <input type="submit" name="pagenum" value="<<"
                                               title="First page">
                                        <span></span>
                                        <input type="submit" name="pagenum" value="<"
                                               title="Previous page">
                                        <span></span>
                                        <c:forEach var="i" begin="${first_page}" end="${last_page}"
                                                   step="1" varStatus ="status">
                                            <c:choose>
                                                <c:when test="${current_page == i}">
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
                                <div id="logtable">
                                    <div class="tableheader">
                                        <div id="cell" class="logdate">
                                            Date
                                        </div>
                                        <div id="cell" class="logtime">
                                            Time
                                        </div>
                                        <div id="cell" class="logflag">
                                            Flag
                                        </div>
                                        <div id="cell" class="logmsg">
                                            Message
                                        </div>
                                    </div>
                                    <c:forEach var="entry" items="${entries}">
                                        <% String style = ""; %>
                                        <c:choose>
                                            <c:when test="${entry.type == 'INFO'}">
                                                <%
                                                    style = "info-entry";
                                                %>
                                            </c:when>
                                            <c:when test="${entry.type == 'WARN'}">
                                                <%
                                                    style = "warning-entry";
                                                %>
                                            </c:when>
                                            <c:when test="${entry.type == 'ERROR'}">
                                                <%
                                                    style = "error-entry";
                                                %>
                                            </c:when>
                                        </c:choose>
                                        <div class="entry">
                                            <div id="cell" class="logdate">
                                                <div class="<%=style%>">
                                                    <c:out value="${fn:substring(entry.timeStamp, 0, 10)}"/>
                                                </div>
                                            </div>
                                            <div id="cell" class="logtime">
                                                <div class="<%=style%>">
                                                    <c:out value="${fn:substring(entry.timeStamp, 10, 19)}"/>
                                                </div>
                                            </div>
                                            <c:choose>
                                                <c:when test="${entry.type == 'ERROR'}">
                                                    <div id="cell" class="logflag">
                                                        <img src="includes/images/icons/error.png"
                                                             alt="error"/>
                                                    </div>
                                                </c:when>
                                                <c:when test="${entry.type == 'WARN'}">
                                                    <div id="cell" class="logflag">
                                                        <img src="includes/images/icons/warning.png"
                                                             alt="warn"/>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div id="cell" class="logflag">
                                                        <img src="includes/images/icons/info.png"
                                                             alt="ok"/>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                            <div id="cell" class="logmsg">
                                                <div class="<%=style%>">
                                                    <c:out value="${entry.message}"/>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div id="bltfooter">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>
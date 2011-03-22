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
Document   : Message table page
Created on : Jun 22, 2010, 11:57:02 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<!html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB"/>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Logtable</title>
    </head>
    <body>
        <div id="logtable">
            <div class="table-hdr">
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
            <c:forEach var="entry" items="${log_entry_list}">
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
                <div class="table-row">
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
    </body>
</html>

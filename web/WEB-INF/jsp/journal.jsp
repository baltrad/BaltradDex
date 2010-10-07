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
                            All system messages
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        All system messages. Click the button below to switch to latest messages.
                    </div>
                    <div id="table">
                        <div id="logtable">
                            <display:table name="full_log_entry_list" id="logEntry"
                                requestURI="journal.htm" cellpadding="0" cellspacing="2"
                                export="false" class="tableborder" pagesize="10" sort="list">
                                <%! String cell_style = ""; %>
                                <c:choose>
                                    <c:when test="${logEntry.type == 'INFO'}">
                                        <%
                                            cell_style = "info";
                                        %>
                                    </c:when>
                                    <c:when test="${logEntry.type == 'WARNING'}">
                                        <%
                                            cell_style = "warning";
                                        %>
                                    </c:when>
                                    <c:when test="${logEntry.type == 'ERROR'}">
                                        <%
                                            cell_style = "error";
                                        %>
                                    </c:when>
                                </c:choose>
                                <display:column sortable="true" title="Date" sortProperty="date"
                                paramId="date" paramProperty="date" class="<%= cell_style %>"
                                    value="${logEntry.date}">
                                </display:column>
                                <display:column sortable="true" title="Time" sortProperty="time"
                                    paramId="time" paramProperty="time" class="<%= cell_style %>"
                                    value="${logEntry.time}">
                                </display:column>
                                <c:choose>
                                    <c:when test="${logEntry.type == 'ERROR'}">
                                        <display:column sortable="true" sortProperty="type"
                                            paramId="type" paramProperty="type" title="Flag"
                                            class="tdcheck">
                                            <img src="includes/images/red_bulb.png"
                                                 alt="green_bulb"/>
                                        </display:column>
                                    </c:when>
                                    <c:when test="${logEntry.type == 'WARNING'}">
                                        <display:column sortable="true" sortProperty="type"
                                            paramId="type" paramProperty="type" title="Flag"
                                            class="tdcheck">
                                            <img src="includes/images/blue_bulb.png"
                                                 alt="blue_bulb"/>
                                        </display:column>
                                    </c:when>
                                    <c:otherwise>
                                        <display:column sortable="true" sortProperty="type"
                                            paramId="type" paramProperty="type" title="Flag"
                                            class="tdcheck">
                                            <img src="includes/images/green_bulb.png"
                                                 alt="red_bulb"/>
                                        </display:column>
                                    </c:otherwise>
                                </c:choose>
                                <display:column sortable="true" title="Message"
                                    sortProperty="message" paramId="message" paramProperty="message"
                                    class="<%= cell_style %>" value="${logEntry.message}">
                                </display:column>
                            </display:table>
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
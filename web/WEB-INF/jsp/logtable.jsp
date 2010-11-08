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
            <display:table name="log_entry_list" id="logEntry" defaultsort="0" requestURI="log.htm"
                cellpadding="0" cellspacing="2" export="false" class="tableborder">
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
                <display:column sortable="false" title="Date" paramId="timeStamp" 
                    paramProperty="timeStamp" class="<%= cell_style %>"
                    value="${fn:substring(logEntry.timeStamp, 0, 10)}">
                </display:column>
                <display:column sortable="false" title="Time" paramId="timeStamp"
                    paramProperty="timeStamp" class="<%= cell_style %>"
                    value="${fn:substring(logEntry.timeStamp, 10, 19)}">
                </display:column>
                <c:choose>
                    <c:when test="${logEntry.type == 'ERROR'}">
                        <display:column sortable="false" paramId="type" paramProperty="type"
                            class="tdcheck">
                            <img src="includes/images/red_bulb.png"
                                 alt="green_bulb"/>
                        </display:column>
                    </c:when>
                    <c:when test="${logEntry.type == 'WARNING'}">
                        <display:column sortable="false" paramId="type" paramProperty="type"
                            class="tdcheck">
                            <img src="includes/images/blue_bulb.png"
                                 alt="blue_bulb"/>
                        </display:column>
                    </c:when>
                    <c:otherwise>
                        <display:column sortable="false" paramId="type" paramProperty="type"
                            class="tdcheck">
                            <img src="includes/images/green_bulb.png"
                                 alt="red_bulb"/>
                        </display:column>
                    </c:otherwise>
                </c:choose>
                <display:column sortable="false" title="Message" paramId="message"
                    paramProperty="message" class="<%= cell_style %>" value="${logEntry.message}">
                </display:column>
            </display:table>
        </div>
    </body>
</html>

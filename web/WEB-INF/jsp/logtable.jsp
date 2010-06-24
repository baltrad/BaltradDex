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
            <display:table name="log_entry_list" id="logEntry" defaultsort="1" requestURI="log.htm"
                cellpadding="0" cellspacing="2" export="false" class="tableborder" sort="page"
                defaultorder="descending">
                <%! String cell_style = ""; %>
                <%! String cell_msg_style = ""; %>
                <c:choose>
                    <c:when test="${logEntry.type == 'INFO'}">
                        <%
                            cell_style = "log-cell-info";
                            cell_msg_style = "log-cell-msg-info";
                        %>
                    </c:when>
                    <c:when test="${logEntry.type == 'WARNING'}">
                        <%
                            cell_style = "log-cell-wrn";
                            cell_msg_style = "log-cell-msg-wrn";
                        %>
                    </c:when>
                    <c:when test="${logEntry.type == 'ERROR'}">
                        <% 
                            cell_style = "log-cell-err";
                            cell_msg_style = "log-cell-msg-err";
                        %>
                    </c:when>
                </c:choose>
                <display:column sortProperty="id" sortable="true"
                    title="ID" class="<%= cell_style %>">
                    <fmt:formatNumber value="${logEntry.id}" pattern="00" />
                </display:column>
                <display:column sortable="true" title="Date" sortProperty="date"
                paramId="date" paramProperty="date" class="<%= cell_style %>"
                    value="${logEntry.date}">
                </display:column>
                <display:column sortable="true" title="Time" sortProperty="time"
                    paramId="time" paramProperty="time" class="<%= cell_style %>"
                    value="${logEntry.time}">
                </display:column>
                <display:column sortable="true" title="Type" sortProperty="type"
                    paramId="type" paramProperty="type" class="<%= cell_style %>"
                    value="${logEntry.type}">
                </display:column>
                <display:column sortable="true" title="Message"
                    sortProperty="message" paramId="message" paramProperty="message"
                    class="<%= cell_msg_style %>" value="${logEntry.message}">
                </display:column>
            </display:table>
        </div>
    </body>
</html>

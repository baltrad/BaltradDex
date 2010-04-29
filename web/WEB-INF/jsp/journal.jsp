<%--
    Document   : System journal page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>System message stack</title>
</head>

<body>
    <div id="container">
        <div id="header"></div>
        <div id="nav">
            <script type="text/javascript" src="includes/navigation.js"></script>
        </div>
        <div class="outer">
            <div class="inner">
                <div class="float-wrap">
                    <div id="main">
                        <h1>System message stack</h1>
                        <br/>
                        <h2>
                            <p>
                            Full system message stack. Click <a href="log.htm">here</a> to go back
                            to the latest messages.
                            </p>
                        </h2>
                        <div id="logtable">
                        <display:table name="full_log_entry_list" id="logEntry" defaultsort="1"
                            requestURI="journal.htm" cellpadding="0" cellspacing="2" export="false"
                            class="tableborder" pagesize="10" sort="list" defaultorder="descending">
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
                    </div>
                    <div id="left">
                        <script type="text/javascript" src="includes/mainmenu.js"></script>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
    </div>
    <div id="footer">
        <script type="text/javascript" src="includes/footer.js"></script>
    </div>
</body>
</html>


















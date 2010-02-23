<%--
    Document   : Log message page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css">
    <title>Baltrad Data Exchange System</title>
</head>

<body>
<div id="content">
    <div id="header">
        <img src="includes/images/baltrad_header.png">
    </div>
    <div id="container1">
        <div id="container2">
            <div id="leftcol">
                <script type="text/javascript" src="includes/mainmenu.js"></script>
            </div>
            <div id="rightcol">
                <div id="table-info">
                    System journal
                </div>
                <div id="table-content">
                    <div id="logtable">
                    <display:table name="full_log_entry_list" id="logEntry" defaultsort="1"
                        requestURI="journal.htm" cellpadding="5" cellspacing="0" export="false"
                        class="tableborder" pagesize="10" sort="list" defaultorder="descending">
                        <display:column sortProperty="id" sortable="true"
                            title="ID" class="tdcenter">
                            <fmt:formatNumber value="${logEntry.id}" pattern="00" />
                        </display:column>
                        <display:column sortable="true" title="Date" sortProperty="date"
                            paramId="date" paramProperty="date" class="tdcenter"
                            value="${logEntry.date}">
                        </display:column>
                        <display:column sortable="true" title="Time" sortProperty="time"
                            paramId="time" paramProperty="time" class="tdcenter"
                            value="${logEntry.time}">
                        </display:column>
                        <display:column sortable="true" title="Type" sortProperty="rank"
                            paramId="rank" paramProperty="rank" class="tdcenter"
                            value="${logEntry.type}">
                        </display:column>
                        <display:column sortable="true" title="Message"
                            sortProperty="text" paramId="text" paramProperty="message"
                            class="tdcenter" value="${logEntry.message}">
                        </display:column>
                    </display:table>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div id="footer">
        <script type="text/javascript" src="includes/footer.js"></script>
    </div>
</div>
</body>
</html>


















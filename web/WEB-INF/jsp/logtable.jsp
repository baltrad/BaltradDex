<%--
    Document   : System welcome page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <display:table name="logentrylist" id="logEntry"
            defaultsort="1" requestURI="log.htm" cellpadding="5"
            cellspacing="0" export="false" class="tableborder">
            <display:column sortProperty="id" sortable="true"
                title="ID" class="tdcenter">
                <fmt:formatNumber value="${logEntry.id}"
                    pattern="00" />
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
                value="${logEntry.rank}">
            </display:column>
            <display:column sortable="true" title="Message"
                sortProperty="text" paramId="text" paramProperty="text"
                class="tdcenter" value="${logEntry.text}">
            </display:column>
        </display:table>
    </body>
</html>

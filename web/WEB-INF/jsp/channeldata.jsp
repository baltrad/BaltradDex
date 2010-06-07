<%--
    Document   : System welcome page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">
     
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%
    String name = request.getParameter( "name" );
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Data from <%= name %></title>
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
                        <h1>Data files from channel <%= name %></h1>
                        <br/>
                        <h2>
                            <p>
                            Click on file name to download data.
                            </p>
                        </h2>
                        <display:table name="channeldata" id="data" defaultsort="1"
                            requestURI="channeldata.htm" cellpadding="0" cellspacing="2"
                            export="false" class="tableborder" pagesize="10" sort="list"
                            defaultorder="descending">
                            <display:column sortProperty="id" sortable="true"
                                title="ID" class="tdcenter">
                                <fmt:formatNumber value="${data.id}" pattern="00" />
                            </display:column>
                            <display:column sortProperty="date" sortable="true"
                                title="Date" class="tdcenter" value="${data.date}">
                            </display:column>
                            <display:column sortProperty="time" sortable="true"
                                title="Time" class="tdcenter" value="${data.time}">
                            </display:column>
                            <display:column sortProperty="path" sortable="true"
                                paramId="path" paramProperty="path" title="File" class="tdcenter"
                                href="download.htm" value="${fn:substring(data.path,
                                            fn:length(data.path) - 44, fn:length(data.path))}">
                            </display:column>
                        </display:table>
                        <div id="table-footer">
                            <a href="channels.htm">&#60&#60 Channel list</a>
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

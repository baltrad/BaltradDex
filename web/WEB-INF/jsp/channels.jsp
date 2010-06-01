<%--
    Document   : Data channels interface
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
    <title>Data channels</title>
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
                        <h1>Available data channels</h1>
                        <br/>
                        <h2>
                            <p>
                            Click on channel name to browse data files.
                            </p>
                        </h2>
                        <display:table name="channels" id="dataChannel" defaultsort="1"
                            requestURI="channels.htm" cellpadding="0" cellspacing="2"
                            export="false" class="tableborder">
                            <display:column sortProperty="id" sortable="true"
                                title="Channel ID" class="tdcenter">
                                <fmt:formatNumber value="${dataChannel.id}" pattern="00" />
                            </display:column>
                            <display:column sortable="true" title="Channel WMO number"
                                sortProperty="wmoNumber" class="tdcenter"
                                value="${dataChannel.wmoNumber}">
                            </display:column>
                            <display:column sortable="true" title="Channel name"
                                href="channeldata.htm" sortProperty="name"
                                paramId="name" paramProperty="name" class="tdcenter"
                                value="${dataChannel.name}">
                            </display:column>
                        </display:table>
                        <div id="table-footer">
                            <a href="welcome.htm">&#60&#60 Home</a>
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



       

<%--
    Document   : Data delivery register interface
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
    <title>Data delivery register</title>
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
                        <h1>Data delivery register</h1>
                        <br/>
                        <h2>
                            <p>
                            Complete data delivery register listing.
                            </p>
                        </h2>
                        <display:table name="register_records" id="registerRecord" defaultsort="1"
                            requestURI="showregister.htm" cellpadding="0" cellspacing="2"
                            export="false" class="tableborder">
                            <display:column sortProperty="dataId" sortable="true"
                                title="Data ID" class="tdcenter">
                                <fmt:formatNumber value="${registerRecord.dataId}" pattern="00" />
                            </display:column>
                            <display:column sortable="true" title="Source data channel"
                                sortProperty="channelName" class="tdcenter"
                                value="${registerRecord.channelName}">
                            </display:column>
                            <display:column sortable="true" title="User ID" sortProperty="userId"
                                class="tdcenter">
                                <fmt:formatNumber value="${registerRecord.userId}" pattern="00" />
                            </display:column>
                            <display:column sortable="true" title="Receiver address"
                                sortProperty="receiverAddress" class="tdcenter"
                                value="${registerRecord.receiverAddress}">
                            </display:column>
                        </display:table>
                        <div id="table-footer">
                            <a href="admin.htm">&#60&#60 System management</a>
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





<%--
    Document   : Edit data channel page
    Created on : May 27, 2010, 11:35:45 AM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Modify data channel</title>
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
                        <h1>Modify data channel</h1>
                        <br/>
                        <h2>
                            <p>
                            Click on data channel ID to modify selected channel.
                            </p>
                        </h2>
                        <display:table name="registered_channels" id="channel" defaultsort="1"
                            requestURI="showchannel.htm" cellpadding="0" cellspacing="2"
                            export="false" class="tableborder" pagesize="10">
                            <display:column sortable="true" title="ID" href="savechannel.htm"
                                sortProperty="id" class="tdcenter" paramProperty="id"
                                paramId="id" value="${channel.id}">
                            </display:column>
                            <display:column sortable="true" title="Channel name" sortProperty="name"
                                class="tdcenter" value="${channel.name}">
                            </display:column>
                            <display:column sortable="true" title="WMO number"
                                sortProperty="wmoNumber" class="tdcenter"
                                value="${channel.wmoNumber}">
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

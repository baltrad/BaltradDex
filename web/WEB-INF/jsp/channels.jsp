<%--
    Document   : System welcome page
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
                    List of available data channels.
                    Click on channel name for data listing.
                </div>
                <div id="table-content">
                    <display:table name="channels" id="dataChannel" defaultsort="1"
                        requestURI="channels.htm" cellpadding="5" cellspacing="0"
                        export="false" class="tableborder">
                        <display:caption class="tablecaption">Data channels</display:caption>
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
                </div>
            </div>
        </div>
    </div>
    <div id="footer">
         <script type="text/javascript" src="includes/footer.js"></script>
    </div>
</div>
</html>



       

<%--
    Document   : Channels listing page
    Created on : May 27, 2010, 12:17:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Remove data channel</title>
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
                        <h1>Remove data channel</h1>
                        <br/>
                        <h2>
                            <p>
                            Select data channel to remove.
                            </p>
                        </h2>
                        <form action="selectchannels.htm">
                            <display:table name="channels" id="channel" defaultsort="1"
                                requestURI="showchannels.htm" cellpadding="0" cellspacing="2"
                                export="false" class="tableborder" pagesize="10">
                                <display:column sortProperty="id" sortable="true"
                                    title="ID" class="tdcenter">
                                    <fmt:formatNumber value="${channel.id}" pattern="00" />
                                </display:column>
                                <display:column sortable="true" title="Channel name"
                                    sortProperty="name" class="tdcenter"
                                    value="${channel.name}">
                                </display:column>
                                <display:column sortable="true" title="WMO number"
                                    sortProperty="wmoNumber" class="tdcenter"
                                    value="${channel.wmoNumber}">
                                </display:column>
                                <display:column sortable="false" title="Select" class="tdcheck">
                                    <input type="checkbox" name="selected_channels"
                                        value="${channel.id}"/>
                                </display:column>
                            </display:table>
                            <div id="table-footer">
                                <input type="submit" value="Submit" name="submitButton"/>
                            </div>
                        </form>
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

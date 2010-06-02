<%--
    Document   : Save data channel page
    Created on : May 26, 2010, 1:44:45 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Manage data channels</title>
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
                        <h1>Manage data channels</h1>
                        <br/>
                        <h2>
                            <p>
                            Create new data channel / manage existing data channel
                            </p>
                        </h2>
                        <form method="post">
                            <table>
                                <caption>Data channel information</caption>
                                <tr class="even">
                                    <td class="left">Channel name</td>
                                    <td class="right">
                                        <form:input path="command.name"/>
                                        <form:errors path="command.name" cssClass="errors"/>
                                    </td>
                                </tr>
                                <tr class="odd">
                                    <td class="left">Channel WMO number</td>
                                    <td class="right">
                                        <form:input path="command.wmoNumber"/>
                                        <form:errors path="command.wmoNumber" cssClass="errors"/>
                                    </td>
                                </tr>
                            </table>
                            <div id="table-footer-rightcol">
                                <input type="submit" value="Submit" name="submit_button"/>
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
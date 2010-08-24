<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW

This file is part of the BaltradDex software.

BaltradDex is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BaltradDex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
----------------------------------------------------------------------------------------------------
Document   : Save data channel page
Created on : Jun 22, 2010, 11:57:02 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Modify local data channel</title>
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
                        <h1>Modify local data channel</h1>
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
                                        <form:input path="command.channelName"/>
                                        <form:errors path="command.channelName" cssClass="errors"/>
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
                        <div id="table-footer-leftcol">
                            <form action="adminControl.htm">
                                <input type="submit" value="Back" name="back_button"/>
                            </form>
                        </div>
                    </div>
                    <div id="left">
                        <%@ include file="/WEB-INF/jsp/mainMenu.jsp"%>
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
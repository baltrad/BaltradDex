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
Document   : System management page
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
    <title>System management</title>
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
                        <h1>System management options</h1>
                        <br/>
                        <h2>
                        </h2>
                        <table>
                            <div id="message-text">
                                <c:if test="${not empty message}">
                                    <c:out value="${message}" />
                                    <c:set var="message" value="" scope="session" />
                                </c:if>
                            </div>
                            <caption>Control features</caption>
                            <tr class="even">
                                <td class="left">Data delivery register</td>
                                <td class="right">
                                    <a href="showregister.htm">Show register</a>
                                    <a href="clearregister.htm">Clear</a>
                                </td>
                            </tr>
                            <tr class="odd">
                                <td class="left">Log messages</td>
                                <td class="right">
                                    <a href="clearmessages.htm">Clear messages</a>
                                </td>
                            </tr>
                            <tr class="even">
                                <td class="left">Users</td>
                                <td class="right">
                                    <a href="editUser.htm">Edit</a>
                                    <a href="saveUser.htm">Add</a>
                                    <a href="showUsers.htm">Remove</a>
                                </td>
                            </tr>
                            <tr class="odd">
                                <td class="left">Local data channels</td>
                                <td class="right">
                                    <a href="editchannel.htm">Edit</a>
                                    <a href="savechannel.htm">Add</a>
                                    <a href="showChannels.htm">Remove</a>
                                </td>
                            </tr>
                            <tr class="even">
                                <td class="left">Subscribed data channels</td>
                                <td class="right">
                                    <a href="selectRemoveSubscriptions.htm">Remove</a>
                                </td>
                            </tr>
                            <tr class="odd">
                                <td class="left">Node connections</td>
                                <td class="right">
                                    <a href="removeNodeConnection.htm">Remove</a>
                                </td>
                            </tr>
                        </table>
                        <div id="table-footer">
                            <form action="welcome.htm">
                                <input type="submit" value="Home" name="home_button"/>
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
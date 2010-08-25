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
Document   : System configuration page
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
                        <div class="tabs">
                            <div class="inactive-ltab">
                                <a href="adminControls.htm">System controls</a>
                            </div>
                            <div class="active-rtab">
                                <a href="adminConfig.htm">Local configuration</a>
                            </div>
                            <div class="config">
                                <div id="message-text">
                                    <c:if test="${not empty message}">
                                        <c:out value="${message}" />
                                        <c:set var="message" value="" scope="session" />
                                    </c:if>
                                </div>
                                <form method="post">
                                <table>
                                    <tr class="even">
                                        <td class="left">Node name</td>
                                        <td class="right">
                                            <form:input path="command.nodeName"/>
                                            <form:errors path="command.nodeName"
                                                         cssClass="errors"/>
                                        </td>
                                    </tr>
                                    <tr class="odd">
                                        <td class="left">Node type</td>
                                        <td class="right">
                                            <spring:bind path="command.nodeType">
                                                <select name='<c:out value=
                                                       "${status.expression}"/>'>
                                                    <c:forEach items="${node_types}"
                                                               var="type">
                                                        <option value='<c:out value=
                                                               "${type}"/>'
                                                            <c:if test="${type ==
                                                                          status.value}">
                                                                SELECTED</c:if>>
                                                                <c:out value="${type}"/>
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </spring:bind>
                                            <form:errors path="command.nodeType"
                                                         cssClass="errors"/>
                                        </td>
                                    </tr>
                                    <tr class="even">
                                        <td class="left">Node address</td>
                                        <td class="right">
                                            <form:input path="command.nodeAddress"/>
                                            <form:errors path="command.nodeAddress"
                                                         cssClass="errors"/>
                                        </td>
                                    </tr>
                                    <tr class="odd">
                                        <td class="left">Organization name</td>
                                        <td class="right">
                                            <form:input path="command.orgName"/>
                                            <form:errors path="command.orgName"
                                                         cssClass="errors"/>
                                        </td>
                                    </tr>
                                    <tr class="even">
                                        <td class="left">Organization address</td>
                                        <td class="right">
                                            <form:input path="command.orgAddress"/>
                                            <form:errors path="command.orgAddress"
                                                         cssClass="errors"/>
                                        </td>
                                    </tr>
                                    <tr class="odd">
                                        <td class="left">Time zone</td>
                                        <td class="right">
                                            <form:input path="command.timeZone"/>
                                            <form:errors path="command.timeZone"
                                                         cssClass="errors"/>
                                        </td>
                                    </tr>
                                    <tr class="even">
                                        <td class="left">Temporary directory</td>
                                        <td class="right">
                                            <form:input path="command.tempDir"/>
                                            <form:errors path="command.tempDir"
                                                         cssClass="errors"/>
                                        </td>
                                    </tr>
                                    <tr class="odd">
                                        <td class="left">Administrator's e-mail</td>
                                        <td class="right">
                                            <form:input path="command.adminEmail"/>
                                            <form:errors path="command.adminEmail"
                                                         cssClass="errors"/>
                                        </td>
                                    </tr>
                                </table>
                                <div id="table-footer-rightcol">
                                    <input type="submit" value="Submit" name="submit_button"/>
                                </div>
                                </form>
                                <div id="table-footer-rightcol">
                                    <form action="welcome.htm">
                                        <input type="submit" value="Home" name="home_button"/>
                                    </form>
                                </div>
                            </div>
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
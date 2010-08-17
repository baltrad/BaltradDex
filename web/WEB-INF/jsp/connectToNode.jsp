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
Document   : Connect to remote node page
Created on : Jun 24, 2010, 2:16:34 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Connect to node</title>
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
                        <h1>Connect to node</h1>
                        <br/>
                        <form method="post">
                            <table>
                                <h2>
                                    <br/>
                                    Select registered node to connect:
                                    <div class="user-input">
                                        <tr>
                                        <td class="left">Select node</td>
                                        <td class="right">
                                            <spring:bind path="command.connectionName">
                                            <select name='<c:out value="${status.expression}"/>'>
                                            <c:forEach items="${connection_list}" var="connection">
                                            <option value='<c:out value="${connection.connectionName}"/>'
                                                <c:if test="${connection.connectionName == status.value}">
                                                    SELECTED</c:if>>
                                                    <c:out value="${connection.connectionName}"/>
                                            </option>
                                            </c:forEach>
                                            </select>
                                            </spring:bind>
                                            <form:errors path="command.connectionName"
                                                         cssClass="errors"/>
                                        </td>
                                        </tr>
                                    </div>
                                </h2>
                            </table>   
                                 <div id="table-footer-rightcol">
                                    <input type="submit" value="Connect" name="submit_button"/>
                                </div>
                        </form>
                                <div class="clear"></div>
                        <form method="post">
                            <table>
                                <h2>
                                    <br/>
                                    Define new connection:
                                    <div class="user-input">
                                        <tr>
                                            <td class="left">Node address</td>
                                            <td class="right">
                                                <form:input path="command.nodeAddress"/>
                                                <form:errors path="command.nodeAddress"
                                                             cssClass="errors"/>
                                            </td>
                                        </tr>
                                        <tr>
                                        <td class="left">User name</td>
                                            <td class="right">
                                                <form:input path="command.userName"/>
                                                <form:errors path="command.userName"
                                                             cssClass="errors"/>
                                            </td>
                                        </tr>
                                        <tr>
                                        <td class="left">Password</td>
                                            <td class="right">
                                                <form:password path="command.password"/>
                                                <form:errors path="command.password"
                                                             cssClass="errors"/>
                                            </td>
                                        </tr>
                                    </div>
                                </h2>
                            </table>
                            <div id="table-footer-rightcol">
                                <input type="submit" value="Connect" name="submit_button"/>
                            </div>
                        </form>
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
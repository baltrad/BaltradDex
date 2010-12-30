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
Created on : Sep 24, 2010, 2:46 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Connect to node</title>
    </head>
    <body>
        <div id="container">
            <div id="header">
                <script type="text/javascript" src="includes/header.js"></script>
            </div>
            <div id="content">
                <div id="left">
                    <%@include file="/WEB-INF/jsp/mainMenu.jsp"%>
                </div>
                <div id="right">
                    <div id="page-title">
                        <div class="left">
                            Connect to remote node
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        Select registered node connection or define new connection in order
                        to access data at the remote node.
                    </div>
                    <div id="table">
                        <div class="header">
                            <div class="title">
                                Select registered node connection
                            </div>
                        </div>
                        <form method="post">
                            <div class="left">
                                <div class="row">
                                    <div class="connectToNode">
                                        Select node connection
                                    </div>
                                </div>
                            </div>
                            <div class="right">
                                <div class="row">
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
                                    <form:errors path="command.connectionName" cssClass="errors"/>
                                </div>
                            </div>
                            <div class="footer">
                                <div class="right">
                                    <button class="rounded" type="submit">
                                        <span>Connect</span>
                                    </button>
                                </div>
                            </div>
                        </form>
                        <div class="header">
                            <div class="title">
                                Define new connection
                            </div>
                        </div>
                        <form method="post">
                            <div class="left">
                                <div class="row">
                                    <div class="connectToNode">
                                        Node Address
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="connectToNode">
                                        User Name
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="connectToNode">
                                        Password
                                    </div>
                                </div>
                            </div>
                            <div class="right">
                                <div class="row">
                                    <div class="row-elem">
                                        http://<form:input path="command.shortAddress"
                                        cssClass="shortAddress"/>:<form:input
                                        path="command.portNumber" cssClass="portNumber"/>
                                    </div>
                                    <form:errors path="command.fullAddress" cssClass="errors"/>
                                </div>
                                <div class="row">
                                    <form:input path="command.userName" cssClass="userName"/>
                                    <form:errors path="command.userName"
                                                 cssClass="errors"/>
                                </div>
                                <div class="row">
                                    <form:password path="command.password" cssClass="passwd"/>
                                    <form:errors path="command.password"
                                                 cssClass="errors"/>
                                </div>
                            </div>
                            <div class="footer">
                                <div class="right">
                                    <button class="rounded" type="submit">
                                        <span>Connect</span>
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <script type="text/javascript" src="includes/footer.js"></script>
        </div>
    </body>
</html>
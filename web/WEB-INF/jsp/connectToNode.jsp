<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW

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
Document   : Page allowing to connect to remote node
Created on : Sep 24, 2010, 2:46 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@page import="eu.baltrad.dex.util.InitAppUtil"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Connect</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/exchangeTab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/exchangeMenu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            <img src="includes/images/icons/connection.png" alt="">
                            Connect to remote node
                        </div>
                        <div class="blttext">
                            Select existing node connection or define new connection in order
                            to access data sources available at the remote node.
                        </div>
                        <div class="table">
                            <div class="connect">
                                <%@include file="/WEB-INF/jsp/formMessages.jsp"%>
                                <div class="bltseparator">Select connection</div>
                                <form method="post">
                                    <div class="rightcol">
                                        <div class="row">
                                            <div class="selectconnection">
                                                <spring:bind path="command.connectionName">
                                                    <select name='<c:out value="${status.expression}"/>'
                                                            title="Select node connection">
                                                        <c:forEach items="${connection_list}" var="connection">
                                                            <option value='<c:out value="${connection.connectionName}"/>'
                                                                <c:if test="${connection.connectionName == status.value}">
                                                                     SELECTED</c:if>>
                                                                <c:out value="${connection.connectionName}"/>
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                </spring:bind>
                                                <div class="hint">
                                                    Node name to connect
                                                </div>
                                            </div>
                                            <form:errors path="command.connectionName" cssClass="error"/>
                                        </div>
                                    </div>
                                    <div class="tablefooter">
                                        <div class="buttons">
                                            <button class="rounded" type="submit">
                                                <span>Connect</span>
                                            </button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                        <div class="table">
                            <div class="connect">
                                <div class="bltseparator">Define new connection</div>
                                <br>
                                <form method="post">
                                    <div class="leftcol">
                                        <div class="row">Node address</div>
                                        <div class="row">User name</div>
                                        <div class="row">Password</div>
                                    </div>
                                    <div class="rightcol">
                                        <div class="row">
                                            <div class="shortaddress">
                                               <form:input path="command.nodeAddress"
                                                   title="Enter node address"/>
                                               <div class="hint">
                                                   Host address, e.g. http://baltrad.eu:8084/BaltradDex/dispatch.htm
                                               </div>
                                            </div>
                                            <form:errors path="command.nodeAddress" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="username">
                                                <form:input path="command.userName"
                                                            title="Enter user name"/>
                                                <div class="hint">
                                                   User name on remote node
                                                </div>
                                            </div>
                                            <form:errors path="command.userName" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="password">
                                                <form:password path="command.password"
                                                               title="Enter password"/>
                                                <div class="hint">
                                                   Password
                                               </div>
                                            </div>
                                            <form:errors path="command.password" cssClass="error"/>
                                        </div>
                                    </div>
                                    <div class="tablefooter">
                                       <div class="buttons">
                                           <button class="rounded" type="submit">
                                               <span>Connect</span>
                                           </button>
                                       </div>
                                   </div>
                               </form>
                           </div>
                       </div>    
                    </div>
                </div>
            </div>
        </div>
        <div id="bltfooter">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>
               

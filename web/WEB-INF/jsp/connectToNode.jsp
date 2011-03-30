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
        <script type="text/javascript" language="javascript" src="includes/tooltip.js"></script>
        <title>Baltrad | Connect to node</title>
        <!-- tooltips -->
        <script type="text/javascript">
            <!--
            var t1 = null;
            var t2 = null;
            var t3 = null;
            var t4 = null;
            var l1 = "Select previously defined node connection";
            var l2 = "Enter base node address and port number, e.g. baltrad.org:8084";
            var l3 = "User name for the remote node";
            var l4 = "User's password for the remote node";
            function initTooltips() {
                t1 = new ToolTip( "connection_tooltip", false );
                t2 = new ToolTip( "address_tooltip", false );
                t3 = new ToolTip( "user_name_tooltip", false );
                t4 = new ToolTip( "password_tooltip", false );
            }
            -->
        </script>
    </head>
    <body onload="initTooltips()">
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
                        Select existing node connection or define new connection in order
                        to access data at the remote node.
                    </div>
                    <div id="node-connect">
                        <div class="connect-header">
                            <div class="title">
                                Select existing connection
                            </div>
                        </div>
                        <form method="post">
                            <div class="row">
                                <div class="left">
                                    Select connection
                                </div>
                                <div class="right">
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
                                    <div class="help-icon" onmouseover="if(t1)t1.Show(event,l1)"
                                        onmouseout="if(t1)t1.Hide(event)">
                                        <img src="includes/images/help-icon.png" alt="help_icon"/>
                                    </div>
                                    <form:errors path="command.connectionName" cssClass="errors"/>
                                </div>
                            </div>
                            <div class="row">
                                <button class="rounded" type="submit">
                                    <span>Connect</span>
                                </button>  
                            </div>
                        </form>
                        <div class="connect-header">
                            <div class="title">
                                Define new connection
                            </div>
                        </div>
                        <form method="post">
                            <div class="row">
                                <div class="left">
                                    Node address
                                </div>
                                <div class="right">
                                    <div class="protocol-prefix">http://</div>
                                    <form:input path="command.shortAddress"
                                        cssClass="node-address"/>
                                    <div class="port-separator">:</div>
                                    <form:input path="command.portNumber" cssClass="port-number"/>
                                    <div class="help-icon" onmouseover="if(t2)t2.Show(event,l2)"
                                        onmouseout="if(t2)t2.Hide(event)">
                                        <img src="includes/images/help-icon.png" alt="help_icon"/>
                                    </div>
                                    <form:errors path="command.fullAddress" cssClass="errors"/>
                                </div>
                            </div>
                            <div class="row">
                                <div class="left">
                                    User name
                                </div>
                                <div class="right">
                                    <form:input path="command.userName" cssClass="user-name"/>
                                    <div class="help-icon" onmouseover="if(t3)t3.Show(event,l3)"
                                        onmouseout="if(t3)t3.Hide(event)">
                                        <img src="includes/images/help-icon.png" alt="help_icon"/>
                                    </div>
                                    <form:errors path="command.userName" cssClass="errors"/>
                                </div>
                            </div>
                            <div class="row">
                                <div class="left">
                                    Password
                                </div>
                                <div class="right">
                                    <form:password path="command.password" cssClass="user-passwd"/>
                                    <div class="help-icon" onmouseover="if(t4)t4.Show(event,l4)"
                                        onmouseout="if(t4)t4.Hide(event)">
                                        <img src="includes/images/help-icon.png" alt="help_icon"/>
                                    </div>
                                    <form:errors path="command.password" cssClass="errors"/>
                                </div>
                            </div>
                            <div class="row">
                                <button class="rounded" type="submit">
                                    <span>Connect</span>
                                </button>
                            </div>
                            <div class="footer"></div>
                        </form>
                    </div>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
        <div id="connection_tooltip" class="tooltip" style="width:180px; height:40px;"></div>
        <div id="address_tooltip" class="tooltip" style="width:180px; height:52px;"></div>
        <div id="user_name_tooltip" class="tooltip" style="width:180px; height:40px;"></div>
        <div id="password_tooltip" class="tooltip" style="width:180px; height:40px;"></div>
    </body>
</html>
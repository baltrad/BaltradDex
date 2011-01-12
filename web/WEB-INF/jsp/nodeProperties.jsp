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
Document   : Local node properties settings
Created on : Oct 1, 2010, 14:06 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Node properties</title>
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
                            Node properties
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        Local node properties. Click Submit to apply modified settings.
                    </div>
                    <div id="table">
                        <div class="props">
                            <form method="post">
                                <div class="left">
                                    <div class="row">Node name</div>
                                    <div class="row">Node type</div>
                                    <div class="row">Node address</div>
                                    <div class="row">Organization</div>
                                    <div class="row">Address</div>
                                    <div class="row">Local time zone</div>
                                    <div class="row">Temporary directory</div>
                                    <div class="row">Node administrator's email</div>
                                </div>
                                <div class="right">
                                    <div class="row">
                                        <form:input path="command.nodeName"/>
                                        <form:errors path="command.nodeName"
                                                     cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <spring:bind path="command.nodeType">
                                            <select name='<c:out value="${status.expression}"/>'>
                                                <c:forEach items="${node_types}" var="type">
                                                    <option value='<c:out value="${type}"/>'
                                                        <c:if test="${type == status.value}">
                                                                SELECTED</c:if>>
                                                            <c:out value="${type}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </spring:bind>
                                        <form:errors path="command.nodeType"
                                                     cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <div class="row-elem">
                                            http://<form:input path="command.shortAddress"
                                            cssClass="shortAddress"/>:<form:input
                                            path="command.portNumber" cssClass="portNumber"/>
                                        </div>
                                        <form:errors path="command.fullAddress" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.orgName"/>
                                        <form:errors path="command.orgName"
                                                     cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.orgAddress"/>
                                        <form:errors path="command.orgAddress"
                                                     cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <spring:bind path="command.timeZone">
                                            <select name='<c:out value=
                                                   "${status.expression}"/>'>
                                                <c:forEach items="${time_zones}" var="zone">
                                                    <option value='<c:out value="${zone}"/>'
                                                        <c:if test="${zone == status.value}">
                                                                SELECTED</c:if>>
                                                            <c:out value="${zone}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </spring:bind>
                                        <form:errors path="command.timeZone"
                                                     cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.tempDir"/>
                                        <form:errors path="command.tempDir"
                                                     cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.adminEmail"/>
                                        <form:errors path="command.adminEmail"
                                                     cssClass="errors"/>
                                    </div>
                                </div>
                                <div id="text-box">
                                    <c:choose>
                                        <c:when test="${not empty ok_message}">
                                            <div class="message">
                                                <div class="icon">
                                                    <img src="includes/images/icons/circle-check.png"
                                                         alt="remove_ok"/>
                                                </div>
                                                <div class="text">
                                                    <c:out value="${ok_message}"/>
                                                    <c:set var="ok_message" value="" scope="session"/>
                                                </div>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <c:if test="${not empty error_message}">
                                                <div class="message">
                                                    <div class="icon">
                                                        <img src="includes/images/icons/circle-delete.png"
                                                             alt="remove_error"/>
                                                    </div>
                                                    <div class="text">
                                                        <c:out value="${error_message}"/>
                                                        <c:set var="error_message" value="" scope="session"/>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="footer">
                                    <div class="right">
                                        <button class="rounded" type="submit">
                                            <span>Submit</span>
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
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

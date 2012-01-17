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
Document   : Local node settings page
Created on : Jun 6, 2011, 10:02 AM
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
        <title>BALTRAD | Node settings</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/settingsTab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/settingsMenu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            <img src="includes/images/icons/settings.png" alt="">
                            Local node settings
                        </div>
                        <div class="blttext">
                            Local node configuration. Use the button below to save modified
                            settings.
                        </div>
                        <div class="table">
                            <div class="props">
                                <form method="post">
                                    <%@include file="/WEB-INF/jsp/formMessages.jsp"%>
                                    <div class="leftcol">
                                        <div class="row">Node name</div>
                                        <div class="row">Node type</div>
                                        <div class="row">Node address</div>
                                        <div class="row">Organization</div>
                                        <div class="row">Address</div>
                                        <div class="row">Local time zone</div>
                                        <div class="row">Work directory</div>
                                        <div class="row">Administrator's email</div>
                                    </div>
                                    <div class="rightcol">
                                        <div class="row">
                                            <div class="nodename">
                                                <form:input path="command.nodeName" 
                                                    title="Enter node name"/>
                                                <div class="hint">
                                                    Unique node identifier
                                                </div>
                                            </div>
                                            <form:errors path="command.nodeName" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="nodetype">
                                                <spring:bind path="command.nodeType">
                                                    <select name='<c:out value="${status.expression}"/>'
                                                            title="Select node type">
                                                        <c:forEach items="${node_types}" var="type">
                                                            <option value='<c:out value="${type}"/>'
                                                                <c:if test="${type == status.value}">
                                                                        SELECTED</c:if>>
                                                                    <c:out value="${type}"/>
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                </spring:bind>
                                                <div class="hint">
                                                    Primary or backup node
                                                </div>
                                            </div>
                                            <form:errors path="command.nodeType" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="fulladdress">
                                                <form:input path="command.nodeAddress"
                                                    title="Enter fully qualified node address"/>
                                                <div class="hint">
                                                    Node address, e.g. http://baltrad.eu:8084/BaltradDex/dispatch.htm
                                                </div>
                                            </div>
                                            <form:errors path="command.nodeAddress" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="orgname">
                                                <form:input path="command.organization"
                                                    title="Enter organization name"/>
                                                <div class="hint">
                                                    Name of organization hosting Baltrad node
                                                </div>
                                            </div>
                                            <form:errors path="command.organization" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="orgaddress">
                                                <form:input path="command.address"
                                                    title="Enter organization's address"/>
                                                <div class="hint">
                                                    Organization's address
                                                </div>
                                            </div>
                                            <form:errors path="command.address" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="timezone">
                                                <spring:bind path="command.timeZone">
                                                    <select name='<c:out value=
                                                        "${status.expression}"/>'
                                                        title="Select time zone">
                                                        <c:forEach items="${time_zones}" var="zone">
                                                            <option value='<c:out value="${zone}"/>'
                                                                <c:if test="${zone == status.value}">
                                                                        SELECTED</c:if>>
                                                                    <c:out value="${zone}"/>
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                </spring:bind>
                                                <div class="hint">
                                                    UTC time zone 
                                                </div>
                                            </div>
                                            <form:errors path="command.timeZone" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="workdir">
                                                <form:input path="command.workDir"
                                                    title="Enter work directory path"/>
                                                <div class="hint">
                                                    Storage for temporary files & images 
                                                </div>
                                            </div>
                                            <form:errors path="command.workDir" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="adminmail">
                                                <form:input path="command.email"
                                                    title="Enter administrator's e-mail"/>
                                                <div class="hint">
                                                    Node administrator's e-mail
                                                </div>
                                            </div>
                                            <form:errors path="command.email" cssClass="error"/>
                                        </div>
                                    </div>
                                    <div class="tablefooter">
                                        <div class="buttons">
                                            <button class="rounded" type="submit">
                                                <span>Save</span>
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
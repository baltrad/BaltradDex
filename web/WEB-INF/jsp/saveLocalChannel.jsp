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
Document   : Save local radar station
Created on : Oct 5, 2010, 11:49 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Save radar</title>
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
                            Save local radar station
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        Save new radar station or modify existing station.
                    </div>
                    <div id="table">
                        <div class="props">
                            <form method="post">
                                <div class="left">
                                    <div class="row">Radar station name</div>
                                    <div class="row">WMO number</div>
                                    <c:if test="${command.channelName != null}">
                                        <div class="row">Users allowed</div>
                                    </c:if>
                                </div>
                                <div class="right">
                                    <div class="row">
                                        <form:input path="command.channelName"/>
                                        <form:errors path="command.channelName" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.wmoNumber"/>
                                        <form:errors path="command.wmoNumber" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <c:if test="${command.channelName != null}">
                                            <select>
                                                <c:forEach items="${users}" var="user">
                                                    <option>
                                                        <c:out value="${user.name}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                            <button class="rounded" type="button"
                                                onclick="window.location='setPermission.htm'">
                                                <span>Set</span>
                                            </button>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="footer">
                                    <div class="right">
                                        <button class="rounded" type="button"
                                            onclick="history.go(-1);">
                                            <span>Back</span>
                                        </button>
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
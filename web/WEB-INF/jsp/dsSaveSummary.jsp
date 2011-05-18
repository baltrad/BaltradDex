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
Document   : Save data source summary page
Created on : Apr 27, 2011, 10:16 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="java.util.List" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Configure data source</title>
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
                            Configure data source
                        </div>
                        <div class="right">
                        </div>
                    </div>
                        <div id="text-box">
                            Step 4. Data source configuration summary
                        </div>
                        <div id="table">
                            <form method="post" action="dsSave.htm">
                                <div id="dsConfig">
                                    <div class="row">
                                        <div class="left">
                                            Data source name
                                        </div>
                                        <div class="right">
                                            <c:out value="${dsName}"></c:out>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="left">
                                            Description
                                        </div>
                                        <div class="right">
                                            <c:out value="${dsDescription}"></c:out>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="left">
                                            Radar station
                                        </div>
                                        <div class="right">
                                            <c:forEach items="${selectedRadars}" var="radar">
                                                <div class="selectedItem">
                                                    <c:out value="${radar.channelName}"></c:out>
                                                    &nbsp;
                                                    WMO number: <c:out value="${radar.wmoNumber}"></c:out>
                                                    &nbsp;
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="left">
                                            Users allowed
                                        </div>
                                        <div class="right">
                                            <c:forEach items="${selectedUsers}" var="user">
                                                <div class="selectedItem">
                                                    Name:&nbsp;<c:out value="${user.name}"></c:out>&nbsp;
                                                    Role: <c:out value="${user.roleName}"></c:out>
                                                    &nbsp;Organization:
                                                    <c:out value="${user.factory}"></c:out>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                    <div class="footer">
                                        <div class="right">
                                            <button class="rounded" type="submit" name="backButton">
                                                <span>Back</span>
                                            </button>
                                            <button class="rounded" type="submit" name="nextButton">
                                                <span>Save</span>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>
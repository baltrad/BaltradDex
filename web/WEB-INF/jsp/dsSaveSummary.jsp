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

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Configure data source</title>
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
                            Configure data source <div class="stepno">Step 5</div>
                        </div>
                        <div class="blttext">
                            Configuration summary
                            <div class="hint">
                                Data source will be saved with the following configuration.
                            </div>
                        </div>
                        <div class="table">
                            <div class="dssave">
                                <form method="post" action="dsSave.htm">
                                    <div class="row">
                                        <div class="leftcol">Data source name</div>
                                        <div class="rightcol">
                                            <c:out value="${dsName}"></c:out>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="leftcol">Description</div>
                                        <div class="rightcol">
                                            <c:out value="${dsDescription}"></c:out>
                                        </div>
                                    </div>
                                    <c:if test="${not empty selectedRadars}">
                                        <div class="row">
                                            <div class="leftcol">Radar stations</div>
                                            <div class="rightcol">
                                                <c:forEach items="${selectedRadars}" var="radar">
                                                    <div class="dsparam">
                                                        <c:out value="${radar.channelName}"></c:out>
                                                        &nbsp;
                                                        WMO number: <c:out value="${radar.wmoNumber}"></c:out>
                                                        &nbsp;
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </c:if>
                                    <c:if test="${not empty selectedFileObjects}">
                                        <div class="row">
                                            <div class="leftcol">File objects</div>
                                            <div class="rightcol">
                                                <c:forEach items="${selectedFileObjects}" var="fobject">
                                                    <div class="dsparam">
                                                        <c:out value="${fobject.fileObject}"></c:out>
                                                        &nbsp;
                                                        <c:out value="${fobject.description}"></c:out>
                                                        &nbsp;
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </c:if>
                                    <c:if test="${not empty selectedUsers}">
                                        <div class="row">
                                            <div class="leftcol">Users allowed</div>
                                            <div class="rightcol">
                                                <c:forEach items="${selectedUsers}" var="user">
                                                    <div class="dsparam">
                                                        Name:&nbsp;<c:out value="${user.name}"></c:out>&nbsp;
                                                        Role: <c:out value="${user.roleName}"></c:out>
                                                        &nbsp;Organization:
                                                        <c:out value="${user.factory}"></c:out>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </c:if>
                                    <div class="tablefooter">
                                        <div class="buttons">
                                           <button class="rounded" type="submit" name="backButton">
                                                <span>Back</span>
                                            </button>
                                            <button class="rounded" type="submit" name="nextButton">
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
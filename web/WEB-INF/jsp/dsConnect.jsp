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
Document   : Displays list of remote data sources
Created on : Sep 29, 2010, 12:00 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Connect" activeTab="exchange">
    <jsp:body>
        <div class="left">
            <t:menu_exchange/>
        </div>
        <div class="right">
            <div class="blttitle">
                <c:choose>
                    <c:when test="${not empty remoteNodeName}">
                        <img src="includes/images/icons/connection.png" alt="">
                        Connected to <c:out value="${remoteNodeName}"/>
                    </c:when>
                    <c:otherwise>
                        <img src="includes/images/icons/failure.png" alt="">
                        Connection failure
                    </c:otherwise>
                </c:choose>
            </div>
            <c:choose>
                <c:when test="${not empty errorMsg}">
                    <div class="blttext">
                        <c:out value="${errorMsg}"/>
                    </div>
                    <div class="table">
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                        onclick="window.location.href='connectToNode.htm'">
                                    <span>OK</span>
                                </button>
                            </div>
                        </div>
                    </div>    
                </c:when>
                <c:otherwise>
                    <div class="blttext">
                        Data sources available at <c:out value="${remoteNodeName}"/>. 
                        Subscribe a desired data source by selecting a corresponding check box.
                    </div>
                    <div class="table">
                        <div class="dsconnect">
                            <form action="dsToConnect.htm" method="post">
                                <div class="tableheader">
                                    <div id="cell" class="count">&nbsp;</div>
                                    <div id="cell" class="name">
                                        Name
                                    </div>
                                    <div id="cell" class="description">
                                        Description
                                    </div>
                                    <div id="cell" class="check">
                                        Select
                                    </div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach items="${remoteDataSources}" var="dataSource">
                                    <div class="entry">
                                        <div id="cell" class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
                                        </div>
                                        <div id="cell" class="name">
                                            <c:out value="${dataSource.name}"/>
                                        </div>
                                        <div id="cell" class="description">
                                            <c:out value="${dataSource.description}"/>
                                        </div>
                                        <div id="cell" class="check">
                                            <input type="checkbox" name="selectedDataSources"
                                                value="${dataSource.name}"/>
                                        </div>
                                    </div>
                                </c:forEach>
                                <div class="tablefooter">
                                    <div class="buttons">
                                        <button class="rounded" type="button"
                                                onclick="window.location.href='connectToNode.htm'">
                                            <span>Back</span>
                                        </button>
                                        <button class="rounded" type="submit">
                                            <span>OK</span>
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>      
    </jsp:body>
</t:page_tabbed>

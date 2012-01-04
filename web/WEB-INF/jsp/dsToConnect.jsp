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
Document   : Selected remote data sources
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
                <img src="includes/images/icons/connection.png" alt="">
                Connected to <c:out value="${remoteNodeName}"/>
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
                        Remote data sources selected for subscription.
                        Click OK to confirm your selection.
                    </div>
                    <div class="table">
                        <div class="dsconnect">
                            <form action="dsSubscribed.htm">
                                <div class="tableheader">
                                    <div id="cell" class="count">&nbsp;</div>
                                    <div id="cell" class="name">
                                        Name
                                    </div>
                                    <div id="cell" class="description">
                                        Description
                                    </div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach items="${selectedDataSources}" var="dataSource">
                                    <div class="entry">
                                        <div id="cell" class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
                                        </div>
                                        <div id="cell" class="name">
                                            <a href="dsFiles.htm?dsName=${dataSource.name}">
                                                <c:out value="${dataSource.name}"/>
                                            </a>
                                        </div>
                                        <div id="cell" class="description">
                                            <c:out value="${dataSource.description}"/>
                                        </div>
                                    </div>
                                </c:forEach>
                                <div class="tablefooter">
                                    <div class="buttons">
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

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
Document   : Displays available data sources allowing to select data source for removal
Created on : Apr 4, 2011, 14:03 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@page import="java.util.List"%>
<%
    List dataSources = ( List )request.getAttribute( "dataSources" );
    if( dataSources == null || dataSources.size() <= 0 ) {
        request.getSession().setAttribute( "dsStatus", 0 );
    } else {
        request.getSession().setAttribute( "dsStatus", 1 );
    }
%>

<t:page_tabbed pageTitle="Remove data source" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Remove data source
            </div>
            <c:choose>
                <c:when test="${dsStatus == 1}">
                    <div class="blttext">
                        The following data sources will be removed from the system.
                    </div>
                    <div class="table">
                        <div class="dsremove">
                            <form method="post" action="remove_datasource_status.htm">
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
                                <c:forEach items="${dataSources}" var="dataSource">
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
                                        <div id="cell" class="hidden">
                                            <input type="checkbox" name="selectedSources"
                                                    value="${dataSource.id}" checked>
                                        </div>
                                    </div>
                                </c:forEach>
                                <div class="tablefooter">
                                    <div class="buttons">
                                        <button class="rounded" type="button"
                                            onclick="window.location.href='remove_datasource.htm'">
                                            <span>Back</span>
                                        </button>
                                        <button class="rounded" type="submit"
                                                name="submitButton">
                                            <span>OK</span>
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="blttext">
                        No data sources have been found.
                        Use configure data source functionality in order to define
                        new data sources.
                    </div>
                    <div class="table">
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                    onclick="window.location.href='home.htm'">
                                    <span>OK</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </jsp:body>    
</t:page_tabbed>

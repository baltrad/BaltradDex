<%------------------------------------------------------------------------------
Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW

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
--------------------------------------------------------------------------------
Document   : Displays available data sources
Created on : Apr 4, 2011, 14:03 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Data sources" activeTab="home">
  <jsp:body>
    <div class="left">
        <t:menu_home/>
    </div>
    <div class="right">
        <div class="blttitle">
            Data sources
        </div>
        <c:choose>
            <c:when test="${not empty data_sources}">
                <div class="blttext">
                    List of available data sources. Click on data source name
                    in order to view dataset available for this source.
                </div>
                <div class="table">
                    <div class="dsshow">
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
                        <c:forEach items="${data_sources}" var="dataSource">
                            <div class="entry">
                                <div id="cell" class="count">
                                    <c:out value="${count}"/>
                                    <c:set var="count" value="${count + 1}"/>
                                </div>
                                <div id="cell" class="name">
                                    <a href="datasource_files.htm?ds_name=${dataSource.name}">
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
                                <button class="rounded" type="button"
                                    onclick="window.location.href='home.htm'">
                                    <span>OK</span>
                                </button>
                            </div>
                        </div>
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
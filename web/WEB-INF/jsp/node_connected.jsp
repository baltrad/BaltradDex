<%------------------------------------------------------------------------------
Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW

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
Document   : Show peer data sources
Created on : May 29, 2013, 3:10 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Connect">
    <jsp:body>
        <div class="node-connected">
            <div class="table">
                <c:choose>
                    <c:when test="${not empty data_sources}">
                        <div class="header">
                            <div class="row">Connected to ${peer_name}</div>
                        </div>
                        <div class="header-text">
                            Data sources available at ${peer_name}.
                            Click checkbox next to selected data source 
                            and <i>OK</i> to subscribe.
                        </div>
                        <form action="node_datasources.htm" method="POST">
                            <div class="body">
                                <div class="header-row">
                                    <div class="count">&nbsp;</div>
                                    <div class="ds_name">Data source name</div>
                                    <div class="ds_description">Description</div>
                                    <div class="select">Select</div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach items="${data_sources}" var="ds">
                                    <div class="row">
                                        <div class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
                                        </div>
                                        <div class="ds_name">
                                            <c:out value="${ds.name}"/>
                                        </div>
                                        <div class="ds_description">
                                            <c:out value="${ds.description}"/>
                                        </div>
                                        <div class="select">
                                            <input type="checkbox" 
                                                   name="selected_data_sources"
                                                   value="${ds.name}"/>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                            <div class="table-footer">
                                <div class="buttons">
                                    <div class="button-wrap">
                                        <input class="button" type="button" 
                                               value="Back"
                                               onclick="window.location.href='node_connect.htm'"/>
                                    </div>
                                    <div class="button-wrap">
                                        <input class="button" type="submit" 
                                               value="OK"/>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <div class="header">
                            <div class="row">Connected to ${peer_name}</div>
                        </div>
                        <div class="header-text">
                            <img src="includes/images/circle-check.png" 
                                 alt="OK"/>
                            <div class="msg">
                                Successfully connected to ${peer_name}, but 
                                no data sources are available.</br> 
                                Ask peer node's administrator to make data 
                                sources available for subscription.
                            </div>
                        </div>
                        <div class="table-footer">
                            <div class="buttons">
                                <div class="button-wrap">
                                    <input class="button" type="button" 
                                           value="OK"
                                           onclick="window.location.href='node_connect.htm'"/>
                                </div>
                            </div>
                        </div>    
                    </c:otherwise>
                </c:choose>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>

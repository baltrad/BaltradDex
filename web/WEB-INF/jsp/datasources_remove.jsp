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
Document   : Select data sources to be removed
Created on : May 22, 2013, 1:24 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Remove data sources">
    <jsp:body>
        <div class="datasources">
            <div class="table">
                <div class="header">
                    <div class="row">
                        Remove data sources
                    </div>
                </div>
                <c:choose>
                    <c:when test="${not empty data_sources}">
                        <div class="header-text">
                            Click check box to select data source to be 
                            removed. Next click <i>OK</i> to confirm selection. 
                        </div>
                        <form action="datasources_remove_selected.htm">
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
                                            <a href="datasources_save.htm?ds_id=${ds.id}">
                                                <c:out value="${ds.name}"/>
                                            </a>
                                        </div>
                                        <div class="ds_description">
                                            <c:out value="${ds.description}"/>
                                        </div>
                                        <div class="select">
                                            <input type="checkbox" 
                                                   name="selected_data_sources"
                                                   value="${ds.id}"/>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                            <div class="table-footer">
                                <div class="buttons">
                                    <div class="button-wrap">
                                        <input class="button" type="submit" 
                                               value="OK"/>
                                    </div>
                                </div>
                            </div>  
                        </form>
                    </c:when>
                    <c:otherwise>
                        <div class="header-text">
                            No data sources found.
                        </div>
                        <div class="table-footer">
                            <div class="buttons">
                                <div class="button-wrap">
                                    <input class="button" type="button" 
                                           value="Home"
                                           onclick="window.location.href='status.htm'"/>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>                
            </div>
        </div>    
    </jsp:body>
</t:generic_page>

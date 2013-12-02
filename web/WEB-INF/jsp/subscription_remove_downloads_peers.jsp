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
Document   : Peer nodes with subscribed data sources
Created on : May 31, 2013, 13:11 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Subscriptions">
    <jsp:body>
        <div class="subscription">
            <div class="table">
                <div class="header">
                    <div class="row">Remove downloads</div>
                </div>
                <c:choose>
                    <c:when test="${not empty downloads_peers}">
                        <div class="header-text">
                            Click peer node's name in order to access list of 
                            subscribed data sources.
                        </div>
                        <div class="body">
                            <div class="header-row">
                                <div class="count">&nbsp;</div>
                                <div class="name">Node name</div>
                                <div class="address">Address</div>
                            </div>
                            <c:set var="count" scope="page" value="1"/>
                            <c:forEach items="${downloads_peers}" var="node">
                                <div class="row">
                                    <div class="count">
                                        <c:out value="${count}"/>
                                        <c:set var="count" value="${count + 1}"/>
                                    </div>
                                    <div class="name">
                                        <a href="subscription_remove_downloads.htm?peer_name=${node.name}" 
                                           title="Click to access subscribed data sources">
                                            <c:out value="${node.name}" />
                                        </a>
                                    </div>
                                    <div class="address">
                                        <c:out value="${node.nodeAddress}"/>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="header-text">
                            No active downloads found.
                        </div>
                    </c:otherwise>
                </c:choose>
                <div class="table-footer">
                    <div class="buttons">
                        <div class="button-wrap">
                            <input class="button" type="button" 
                                   value="Home"
                                   onclick="window.location.href='status.htm'"/>
                        </div>
                    </div>
                </div>                    
            </div>
        </div>
    </jsp:body>
</t:generic_page>

           
                   
  
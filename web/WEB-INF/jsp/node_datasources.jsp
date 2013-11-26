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
Document   : Peer data sources selected for subscription
Created on : May 31, 2013, 12:10 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Connect">
    <jsp:body>
        <div class="node-connected">
            <div class="table">
                <div class="header">
                    <div class="row">Connected to ${peer_name}</div>
                </div>
                <div class="header-text">
                    Data sources selected for subscription. 
                    Click <i>OK</i> to subscribe.
                </div>
                <form action="subscription_start_status.htm?peer_name=${peer_name}"
                      method="POST">
                    <div class="body">
                        <div class="header-row">
                            <div class="count">&nbsp;</div>
                            <div class="ds_name">Data source name</div>
                            <div class="ds_description">Description</div>
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
                            </div>
                        </c:forEach>
                    </div>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="button" 
                                       value="Back"
                                       onclick="history.back()"/>
                            </div>
                            <div class="button-wrap">
                                <input class="button" type="submit" 
                                       value="OK"/>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </jsp:body>
</t:generic_page>

                
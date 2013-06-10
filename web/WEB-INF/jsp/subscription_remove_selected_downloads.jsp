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
Document   : Remove selected subscriptions
Created on : June 3, 2013, 10:28 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Remove subscription">
    <jsp:body>
        <div class="subscription-remove">
            <div class="table">
                <div class="header">
                    <div class="row">Remove downloads</div>
                </div>
                <div class="header-text">
                    <img src="includes/images/circle-alert.png" 
                         alt="OK"/>
                    <div class="msg">
                        Selected subscriptions will be removed.
                    </div>
                </div>
                <form action="subscription_remove_downloads_status.htm" 
                      method="POST">
                    <div class="body">
                        <div class="header-row">
                            <div class="count">&nbsp;</div>
                            <div class="node">Node name</div>
                            <div class="ds_name">Data source name</div>
                            <div class="started">Started on</div>
                            <div class="status">Status</div>
                        </div>
                        <c:set var="count" scope="page" value="1"/>
                        <c:forEach items="${selected_downloads}" var="sub">
                            <div class="row">
                                <div class="count">
                                    <c:out value="${count}"/>
                                    <c:set var="count" value="${count + 1}"/>
                                </div>
                                <div class="node">
                                    <c:out value="${sub.user}"/>
                                </div>
                                <div class="ds_name">
                                    <c:out value="${sub.dataSource}"/>
                                </div>
                                <div class="started">
                                    <fmt:formatDate value="${sub.date}" 
                                                    pattern="yyyy/dd/MM HH:mm:ss"/>
                                </div>
                                <div class="status">
                                    <c:choose>
                                        <c:when test="${sub.active == true}">
                                            <img src="includes/images/log-info.png"
                                                    alt="Active" title="Active subscription"/>
                                        </c:when>
                                        <c:otherwise>
                                            <img src="includes/images/stop.png"
                                                    alt="Stopped" title="Stopped subscription"/>
                                        </c:otherwise>
                                    </c:choose>
                                </div>        
                            </div>
                        </c:forEach>
                    </div>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="button" 
                                       value="Back"
                                       onclick="window.location.href='subscription_remove_downloads.htm'"/>
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

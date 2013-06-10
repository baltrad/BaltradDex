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
Document   : List of subscribed data sources
Created on : May 31, 2013, 13:51 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Subscriptions">
    <jsp:body>
        <div class="subscription">
            <div class="table">
                <div class="header">
                    <div class="row">Subscriptions</div>
                </div>
                 <c:choose>
                     <c:when test="${not empty subscription_by_peer}">
                        <div class="header-text">
                            Data sources subscribed at ${peer_name}.
                            Click checkbox next to selected data source in order
                            to start or cancel subscription. Click <i>OK</i>
                            to confirm.
                        </div>
                        <form action="subscription_selected.htm?peer_name=${peer_name}" 
                              method="POST">
                            <t:message_box msgHeader="Subscription status not changed."
                               msgBody="${subscription_status_unchanged}"/>
                            <div class="body">
                                <div class="header-row">
                                    <div class="count">&nbsp;</div>
                                    <div class="ds_name">Data source name</div>
                                    <div class="started">Started on</div>
                                    <div class="status">Status</div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach items="${subscription_by_peer}" var="sub">
                                    <div class="row">
                                        <div class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
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
                                                    <input type="checkbox" 
                                                           name="selected_subscription_ids"
                                                           value="${sub.id}" 
                                                           checked/>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="checkbox" 
                                                           name="selected_subscription_ids"
                                                           value="${sub.id}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="hidden">        
                                            <input type="checkbox"
                                                   name="current_subscription_ids"
                                                   value="${sub.id}" checked/>
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
                             Failed to fetch list of subscribed data sources. 
                             Try again or report this problem to administrator.
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

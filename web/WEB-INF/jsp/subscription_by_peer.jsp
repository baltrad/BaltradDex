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
Document   : Subscription management page
Created on : Sep 30, 2010, 16:34 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Subscriptions" activeTab="exchange">
    <jsp:body>
        <div class="left">
            <t:menu_exchange/>
        </div>
        <div class="right">
            <div class="blttitle">
                Data sources subscribed on <c:out value="${peer_name}"/> 
            </div>
            <c:choose>
                <c:when test="${not empty subscription_by_peer}">
                    <div class="blttext"> 
                        Click on a check box to start or cancel subscription of a 
                        selected data source.
                    </div>
                    <div class="table">
                        <div class="subscriptions">
                            <form action="selected_subscription.htm?peer_name=${peer_name}" 
                                  method="post">
                                <div class="tableheader">
                                    <div id="cell" class="count">&nbsp;</div>
                                    <div id="cell" class="name">
                                        Data source
                                    </div>
                                    <div id="cell" class="timestamp">
                                        Started at
                                    </div>
                                    <div id="cell" class="active">
                                        Status
                                    </div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach items="${subscription_by_peer}" var="sub">
                                    <div class="entry">
                                        <div id="cell" class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
                                        </div>
                                        <div id="cell" class="name">
                                            <c:out value="${sub.dataSourceName}"/>
                                        </div>
                                        <div id="cell" class="timestamp">
                                            <fmt:formatDate value="${sub.timeStamp}" 
                                                            pattern="yyyy/dd/MM HH:mm:ss"/>
                                        </div>
                                        <div id="cell" class="active">
                                            <c:choose>
                                                <c:when test="${sub.active == true}">
                                                    <input type="checkbox" 
                                                           name="selected_data_sources"
                                                           value="${sub.dataSourceName}" 
                                                           checked/>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="checkbox" 
                                                           name="selected_data_sources"
                                                           value="${sub.dataSourceName}"/>
                                                </c:otherwise>
                                            </c:choose>
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
                </c:when>
                <c:otherwise>
                    <div class="blttext">
                        Failed to fetch list of subscribed data sources. 
                        Try again or report this problem to administrator.
                    </div>
                    <div class="table">
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                    onclick="window.location.href='exchange.htm'">
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
  
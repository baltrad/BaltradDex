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
                Subscribed peer nodes
            </div>
            <c:choose>
                <c:when test="${not empty subscribed_peers}">
                    <div class="blttext">
                        Click on peer node's name to access list of subscribed
                        data sources.
                    </div>
                    <div class="table">
                        <div class="subscriptions">
                            <div class="tableheader">
                                <div id="cell" class="count">&nbsp;</div>
                                <div id="cell" class="name">
                                    Peer name
                                </div>
                                <div id="cell" class="timestamp">
                                    Node address
                                </div>
                            </div>
                            <c:set var="count" scope="page" value="1"/>
                            <c:forEach items="${subscribed_peers}" var="sub">
                                <div class="entry">
                                    <div id="cell" class="count">
                                        <c:out value="${count}"/>
                                        <c:set var="count" value="${count + 1}"/>
                                    </div>
                                    <div id="cell" class="operator">
                                        <a href="subscription_by_peer.htm?peer_name=${sub.operatorName}" 
                                            title="Click to access subscribed data sources">
                                            <c:out value="${sub.operatorName}" />
                                        </a>
                                    </div>
                                    <div id="cell" class="nodeaddress">
                                        <c:out value="${sub.nodeAddress}"/>
                                    </div>
                                </div>
                            </c:forEach>
                            <div class="tablefooter">
                                <div class="buttons">
                                    <button class="rounded" type="button"
                                            onclick="window.location.href='exchange.htm'">
                                        <span>Back</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="blttext">
                        No subscribed peers found. Use node connection 
                        functionality to connect to peer nodes and subscribe 
                        data sources.
                    </div>
                    <div class="table">
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                    onclick="window.location.href='connect_to_node.htm'">
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
  
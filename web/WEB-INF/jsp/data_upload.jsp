<%------------------------------------------------------------------------------
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
--------------------------------------------------------------------------------
Document   : Data upload status page
Created on : Apr 1, 2011, 12:13 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Data upload" activeTab="home">
  <jsp:body>
    <div class="left">
        <t:menu_home/>
    </div>
    <div class="right">
        <div class="blttitle">
            <img src="includes/images/icons/upload.png" alt="">
            Data upload status
        </div>
        <div class="blttext">
            Data uploaded to subscribers.
        </div>
        <c:choose>
            <c:when test="${not empty users}">
                <div class="blttext">
                    Click on user name to view detailed information
                    on subscriber.
                </div>
                <c:forEach var="user" items="${users}">
                <div class="expandable">
                    <div class="save">
                        <div class="item">
                            <a href="javascript:void(0)" class="dsphead"
                            onclick="dsp(this)">
                                <span class="dspchar">
                                    <img src="includes/images/icons/expand.png"
                                        alt="+" title="Show">
                                </span>
                                <div class="user">
                                    <c:out value="${user.name}"/>
                                </div>
                            </a>
                        </div>
                        <div class="dspcont">
                            <div class="statustable">
                                <c:choose>
                                    <c:when test="${not empty subscriptions}">
                                        <div class="header">
                                            <div id="cell" class="station">
                                                Data source
                                            </div>
                                            <div id="cell" class="timestamp">
                                                Started at
                                            </div>
                                        </div>
                                        <c:forEach var="sub" items="${subscriptions}">
                                            <c:if test="${sub.user == user.name}">    
                                                <div class="entry">
                                                    <div id="cell" class="station">
                                                        <c:out value="${sub.dataSource}"/>
                                                    </div>
                                                    <div id="cell" class="timestamp">
                                                        <fmt:formatDate value="${sub.date}" 
                                                                        pattern="yyyy/dd/MM HH:mm:ss"/>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                    </c:when>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="blttext">
                    Your local data sources are currently not subscribed
                    by peers.
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    </div>
  </jsp:body>
</t:page_tabbed>

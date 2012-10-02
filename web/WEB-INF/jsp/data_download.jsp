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
Document   : Data download status page
Created on : Apr 1, 2011, 12:23 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Data download" activeTab="home">
  <jsp:body>
    <div class="left">
        <t:menu_home/>
    </div>
    <div class="right">              
        <div class="blttitle">
            <img src="includes/images/icons/download.png" alt="">
            Data download status
        </div>
        <div class="blttext">
            Data incoming from subscribed data sources.
        </div>
        <c:choose>
            <c:when test="${not empty operators}">
                <div class="blttext">
                    Click on node name to view detailed information
                    on subscribed data sources.
                </div>
                <c:forEach var="operator" items="${operators}">
                <c:set var="op" scope="page" value="${operator}"></c:set>
                <div class="expandable">
                    <div class="save">
                        <div class="item">
                            <a href="javascript:void(0)" class="dsphead"
                            onclick="dsp(this)">
                                <span class="dspchar">
                                    <img src="includes/images/icons/expand.png"
                                            alt="+" title="Show">
                                </span>
                                <div class="operator">
                                    <c:out value="${operator}"/>
                                </div>
                            </a>
                        </div>
                        <div class="dspcont">
                            <div class="statustable">
                            <c:choose>
                                <c:when test="${not empty local}">
                                    <div class="header">
                                        <div id="cell" class="station">
                                            Data source
                                        </div>
                                        <div id="cell" class="timestamp">
                                            Started at
                                        </div>
                                        <div id="cell" class="active">
                                            Status
                                        </div>
                                    </div>
                                    <c:forEach var="sub" items="${local}">
                                        <c:if test="${sub.operatorName == op}">
                                            <div class="entry">
                                                <div id="cell" class="station">
                                                    <c:out value="${sub.dataSourceName}"/>
                                                </div>
                                                <div id="cell" class="timestamp">
                                                    <fmt:formatDate value="${sub.timeStamp}" 
                                                                    pattern="yyyy/dd/MM HH:mm:ss"/>
                                                </div>
                                                <div id="cell" class="active">
                                                <c:choose>
                                                    <c:when test="${sub.active == true}">
                                                        <img src="includes/images/icons/download.png"
                                                            alt="active"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="includes/images/icons/stop.png"
                                                            alt="stopped"/>
                                                    </c:otherwise>
                                                </c:choose>
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
                    No subscribed data sources found.
                </div>
            </c:otherwise>
        </c:choose>        
    </div>
  </jsp:body>
</t:page_tabbed>

<%-- 
        <!--meta name="save" content="history" /-->
        <noscript>
            <style type="text/css"><!--.dspcont{display:block;}--></style>
        </noscript>
--%>
                   
        
   

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
Document   : Log table page
Created on : Jun 22, 2013, 13:39:02 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<c:choose>
    <c:when test="${not empty messages}">
        <div class="header-row">
            <div class="date">Date</div>
            <div class="time">Time</div>
            <div class="flag">&nbsp;</div>
            <div class="message">Message</div>
        </div>
        <c:forEach var="msg" items="${messages}">
            <c:choose>
                <c:when test="${msg.level == 'ERROR'}">
                    <div class="row" id="error">
                        <div class="date">
                            <fmt:formatDate value="${msg.date}" 
                                            pattern="yyyy/dd/MM"/>
                        </div>
                        <div class="time">
                            <fmt:formatDate value="${msg.date}" 
                                            pattern="HH:mm:ss"/>  
                        </div>
                        <div class="flag">
                            <img src="includes/images/log-error.png" 
                                 alt="error"/>
                        </div>    
                        <div class="message">
                            <c:out value="${msg.message}"/>
                        </div>
                    </div>
                </c:when>
                <c:when test="${msg.level == 'WARN'}">
                    <div class="row" id="warning">
                        <div class="date">
                            <fmt:formatDate value="${msg.date}" 
                                            pattern="yyyy/dd/MM"/>
                        </div>
                        <div class="time">
                            <fmt:formatDate value="${msg.date}" 
                                            pattern="HH:mm:ss"/>   
                        </div>
                        <div class="flag">
                            <img src="includes/images/log-alert.png" 
                                 alt="error"/>
                        </div>                    
                        <div class="message">
                            <c:out value="${msg.message}"/>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row" id="info">
                        <div class="date">
                            <fmt:formatDate value="${msg.date}" 
                                            pattern="yyyy/dd/MM"/>
                        </div>
                        <div class="time">
                            <fmt:formatDate value="${msg.date}" 
                                            pattern="HH:mm:ss"/>   
                        </div>
                        <div class="flag">
                            <img src="includes/images/log-info.png" 
                                 alt="error"/>
                        </div>                    
                        <div class="message">
                            <c:out value="${msg.message}"/>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </c:when>
    <c:otherwise>
        No messages found in system log.
    </c:otherwise>
</c:choose>

    
    
                   








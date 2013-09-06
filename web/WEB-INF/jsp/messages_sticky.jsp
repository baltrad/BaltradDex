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
Document   : Sticky messages
Created on : Sep 1, 2013, 9:57 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Sticky messages">
    <jsp:body>
         <div class="sticky-messages">
            <div class="table">
                <div class="header">
                    <div class="row">
                        Sticky messages
                    </div>
                </div>
                <c:choose>
                    <c:when test="${not empty messages}">
                         <div class="header-text">
                            Use <i>Delete</i> next to message in order to remove 
                            old messages.
                        </div>
                        <c:forEach var="msg" items="${messages}">
                            <form method="POST">
                                <input type="checkbox" name="message_id"
                                       class="hidden" value="${msg.id}" 
                                       checked/>
                                <div class="message-header">
                                    Received:
                                    <fmt:formatDate value="${msg.date}" 
                                                    pattern="yyyy/MM/dd"/>
                                    <fmt:formatDate value="${msg.date}" 
                                                    pattern="HH:mm:ss"/> 
                                </div>
                                <div class="message-content">
                                    <c:out value="${msg.message}"/>
                                </div>
                                <div class="message-footer">
                                    <div class="buttons">
                                        <div class="button-wrap">
                                            <input class="button" type="submit" 
                                                   value="Delete"></input>
                                        </div>
                                    </div>
                                </div>
                            </form>    
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="header-text">
                            No sticky messages found in system log.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
         </div>
    </jsp:body>
</t:generic_page>
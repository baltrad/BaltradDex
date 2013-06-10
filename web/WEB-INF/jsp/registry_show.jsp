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
Document   : Data delivery registry
Created on : May 27, 2013, 9:12 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Delivery registry">
    <jsp:body>
        <div class="registry">
            <div class="table">
                <div class="header">
                    <div class="row">
                        Delivery registry
                    </div>
                </div>
                <c:choose>
                    <c:when test="${not empty entries}">
                        <div class="header-text">
                            Data delivery registry entries.
                        </div>
                        <form action="registry_show.htm" method="POST">
                            <div id="scroll">
                                
                                <%--
                                
                                <form action="registry.htm" method="post">
                                    <input type="submit" name="pagenum" value="<<"
                                            title="First page">
                                    <span></span>
                                    <input type="submit" name="pagenum" value="<"
                                            title="Previous page">
                                    <span></span>
                                    <c:forEach var="i" begin="${first_page}" end="${last_page}"
                                                step="1" varStatus ="status">
                                            <c:choose>
                                                <c:when test="${current_page == i}">
                                                    <input style="background:#FFFFFF" type="submit"
                                                            name="pagenum" value="${i}">
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="submit" name="pagenum" value="${i}">
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    <span></span>
                                    <input type="submit" name="pagenum" value=">"
                                            title="Next page">
                                    <span></span>
                                    <input type="submit" name="pagenum" value=">>"
                                            title="Last page">
                                </form>
                                --%>
                                
                                
                                
                                
                                <div class="leftcol">
                                    <input type="submit" name="selected_page" value="<<"
                                           title="First page">
                                    <input type="submit" name="selected_page" value="<"
                                           title="Previous page">
                                </div>
                                <div class="midcol">
                                    <c:forEach var="i" begin="${first_page}" 
                                               end="${last_page}" step="1" 
                                               varStatus ="status">
                                        <c:choose>
                                            <c:when test="${current_page == i}">
                                                <input style="font-weight: bold;
                                                              font-size: 14px;" 
                                                       type="submit" 
                                                       name="selected_page" 
                                                       value="${i}">
                                            </c:when>
                                            <c:otherwise>
                                                <input type="submit" 
                                                       name="selected_page" 
                                                       value="${i}">
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </div>
                                <div class="rightcol">
                                    <input type="submit" name="selected_page" value=">"
                                           title="Next page">
                                    <input type="submit" name="selected_page" value=">>"
                                           title="Last page">
                                </div>
                            </div>   
                        </form> 
                        <div class="body">
                             <div class="header-row">
                                <div class="date">Date</div>
                                <div class="time">Time</div>
                                <div class="recipient">Recipient</div>
                                <div class="uuid">File signature (UUID)</div>
                                <div class="status">Status</div>
                            </div>
                            <c:forEach var="entry" items="${entries}">
                                <div class="row">
                                    <div class="date">
                                        <fmt:formatDate value="${entry.date}" 
                                                        pattern="yyyy/MM/dd"/>
                                    </div>
                                    <div class="time">
                                        <fmt:formatDate value="${entry.date}" 
                                                        pattern="HH:mm:ss"/>  
                                    </div>
                                    <div class="recipient">
                                        <c:out value="${entry.userName}"/>
                                    </div>
                                    <div class="uuid">
                                        <c:out value="${entry.uuid}"/>
                                    </div>
                                    <div class="status">
                                        <c:choose>
                                            <c:when test="${entry.status == true}">
                                                <img src="includes/images/log-info.png"
                                                        alt="Success" title="Delivery success">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="includes/images/log-error.png"
                                                        alt="Failure" title="Delivery failure">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>            
                    </c:when>
                    <c:otherwise>
                        <div class="header-text">
                            No entries found in delivery registry.
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

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
Document   : Data source files
Created on : May 10, 2013, 11:53 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Data source files">
    <jsp:body>
        <div class="datasource-files">
            <div class="table">
                <div class="header">
                    <div class="row">
                        Data source files
                    </div>
                </div>
                <c:choose>
                    <c:when test="${not empty fc_error}">
                        <div id="message-box">
                            <t:message_box errorHeader="Problems encountered"
                                           errorBody="${fc_error}"/>
                        </div>
                        <div class="table-footer">
                            <div class="buttons">
                                <div class="button-wrap">
                                    <input class="button" type="button"
                                           value="Back"
                                           onclick="window.location.href='datasources_show.htm'">
                                </div>
                            </div>
                        </div> 
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${not empty files}">
                                <div class="header-text">
                                    Data files from ${data_source_name}.
                                </div>
                                <form action="datasource_files.htm" method="POST">
                                    <div id="scroll">
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
                                    <div class="body">
                                        <div class="header-row">
                                            <div class="count">&nbsp;</div>
                                            <div class="date">
                                                <input name="sortByDate" type="submit" 
                                                       value="Date" 
                                                       title="Sort result set by date">                                        
                                            </div>
                                            <div class="time">
                                                <input name="sortByTime" type="submit" 
                                                       value="Time"
                                                       title="Sort result set by time">
                                            </div>
                                            <div class="source">Source</div>
                                            <div class="type">
                                                <input name="sortByObject" type="submit" 
                                                       value="Type"
                                                       title="Sort result set by file object type">
                                            </div>
                                            <div class="details">&nbsp;</div>
                                            <div class="download">&nbsp;</div>
                                        </div>
                                        <c:set var="count" scope="page" value="1"/>
                                        <c:forEach var="file" items="${files}">
                                            <div class="row">
                                                <div class="count">
                                                    <c:out value="${count}"/>
                                                    <c:set var="count" value="${count + 1}"/>
                                                </div>
                                                <div class="date">
                                                    <fmt:formatDate pattern="yyyy-MM-dd"
                                                        value="${file.timeStamp}"/>
                                                </div>
                                                <div class="time">
                                                    <fmt:formatDate pattern="HH:mm:ss"
                                                        value="${file.timeStamp}"/>
                                                </div>
                                                <div class="source">
                                                    <c:out value="${file.source}"></c:out>
                                                </div>
                                                <div class="type">
                                                    <c:out value="${file.type}"></c:out>
                                                </div>
                                                <div class="details">
                                                    <a href="file_details.htm?uuid=${file.uuid}">
                                                        <c:out value="Details"/>
                                                    </a>
                                                </div>
                                                <div class="download">
                                                    <a href="file_download.htm?uuid=${file.uuid}">
                                                        <c:out value="Download"/>
                                                    </a>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <div class="table-footer">
                                        <div class="buttons">
                                            <div class="button-wrap">
                                                <input class="button" type="button"
                                                       value="Back"
                                                       onclick="window.location.href='datasources_show.htm'">
                                            </div>
                                        </div>
                                    </div>                              
                                </form>    
                            </c:when>
                            <c:otherwise>
                                <div class="header-text">
                                    No matching data files found.
                                </div>
                                <div class="table-footer">
                                    <div class="buttons">
                                        <div class="button-wrap">
                                            <input class="button" type="button"
                                                   value="Back"
                                                   onclick="window.location.href='datasources_show.htm'"/>
                                        </div>
                                        <div class="button-wrap">
                                            <input class="button" type="button" 
                                                   value="Home"
                                                   onclick="window.location.href='status.htm'"/>
                                        </div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>  
    </jsp:body>
</t:generic_page>

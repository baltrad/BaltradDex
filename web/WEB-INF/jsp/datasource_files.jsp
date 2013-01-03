<%------------------------------------------------------------------------------
Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW

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
Document   : Displays data from selected data source
Created on : Sep 24, 2010, 13:51 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Browse data" activeTab="home">
  <jsp:body>
    <div class="left">
        <t:menu_home/>
    </div>
    <div class="right">
        <div class="blttitle">
            Data from ${datasource_name}
        </div>
        <c:choose>
            <c:when test="${not empty file_entries}">
                <div class="blttext">
                    Data files available for data source ${datasource_name}.
                </div>
                <div class="table">
                    <div class="dsfiles">
                        <div id="tablecontrol">
                            <form action="datasource_files.htm" method="post">
                                <input type="submit" name="selected_page" value="<<"
                                        title="First page">
                                <span></span>
                                <input type="submit" name="selected_page" value="<"
                                        title="Previous page">
                                <span></span>
                                <c:forEach var="i" begin="${first_page}" end="${last_page}"
                                            step="1" varStatus ="status">
                                        <c:choose>
                                            <c:when test="${current_page == i}">
                                                <input style="background:#FFFFFF" type="submit"
                                                        name="selected_page" value="${i}">
                                            </c:when>
                                            <c:otherwise>
                                                <input type="submit" 
                                                       name="selected_page" 
                                                       value="${i}">
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                <span></span>
                                <input type="submit" name="selected_page" value=">"
                                        title="Next page">
                                <span></span>
                                <input type="submit" name="selected_page" value=">>"
                                        title="Last page">
                            </form>
                        </div>
                        <div class="tableheader">
                            <div id="cell" class="date">
                                Date
                            </div>
                            <div id="cell" class="time">
                                Time
                            </div>
                            <div id="cell" class="source">
                                Source
                            </div>
                            <div id="cell" class="type">
                                Type
                            </div>
                            <div id="cell" class="details">&nbsp;</div>
                            <div id="cell" class="download">&nbsp;</div>
                        </div>
                        <c:forEach var="entry" items="${file_entries}">
                            <div class="entry">
                                <div id="cell" class="date">
                                    <fmt:formatDate pattern="yyyy-MM-dd"
                                        value="${entry.timeStamp}"/>
                                </div>
                                <div id="cell" class="time">
                                    <fmt:formatDate pattern="HH:mm:ss"
                                        value="${entry.timeStamp}"/>
                                </div>
                                <div id="cell" class="source">
                                    <c:out value="${entry.source}"></c:out>
                                </div>
                                <div id="cell" class="type">
                                    <c:out value="${entry.type}"></c:out>
                                </div>
                                <div id="cell" class="details">
                                    <a href="file_details.htm?uuid=${entry.uuid}">
                                        <c:out value="Details"/>
                                    </a>
                                </div>
                                <div id="cell" class="download">
                                    <a href="download.htm?uuid=${entry.uuid}">
                                        <c:out value="Download"/>
                                    </a>
                                </div>
                            </div>
                        </c:forEach>
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                    onclick="window.location.href='show_datasources.htm'">
                                    <span>Back</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="blttext">
                    No files found for the selected data source.
                </div>
                <div class="table">
                    <div class="tablefooter">
                        <div class="buttons">
                            <button class="rounded" type="button"
                                onclick="window.location.href='show_datasources.htm'">
                                <span>OK</span>
                            </button>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    </div>
  </jsp:body>
</t:page_tabbed>

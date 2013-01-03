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
Document   : Data delivery register
Created on : Oct 6, 2010, 10:49 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Data delivery registry" activeTab="exchange">
    <jsp:body>
        <div class="left">
            <t:menu_exchange/>
        </div>
        <div class="right">
            <div class="blttitle">
                Data delivery registry
            </div>
            <c:choose>
                <c:when test="${not empty entries}">
                    <div class="blttext">
                        All data delivery registry entries.
                    </div>
                    <div class="table">
                        <div class="register">
                            <div id="tablecontrol">
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
                            </div>
                            <div class="tableheader">
                                <div id="cell" class="date">
                                    Date
                                </div>
                                <div id="cell" class="time">
                                    Time
                                </div>
                                <div id="cell" class="recipient">
                                    Recipient
                                </div>
                                <div id="cell" class="signature">
                                    File signature
                                </div>
                                <div id="cell" class="status">
                                    Status
                                </div>
                            </div>
                            <c:forEach var="entry" items="${entries}">
                                <div class="entry">
                                    <div id="cell" class="date">
                                        <c:out value="${fn:substring(entry.dateTime, 0, 10)}"/>
                                    </div>
                                    <div id="cell" class="time">
                                        <c:out value="${fn:substring(entry.dateTime, 10, 19)}"/>
                                    </div>
                                    <div id="cell" class="recipient">
                                        <c:out value="${entry.user}"/>
                                    </div>
                                    <div id="cell" class="signature">
                                        <c:out value="${entry.uuid}"/>
                                    </div>
                                    <div id="cell" class="status">
                                        <c:choose>
                                            <c:when test="${entry.status == 'SUCCESS'}">
                                                <img src="includes/images/icons/success.png"
                                                        alt="Success" title="Delivery success">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="includes/images/icons/failure.png"
                                                        alt="Failure" title="Delivery failure">
                                            </c:otherwise>
                                        </c:choose>
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
                        Data delivery registry is empty.
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

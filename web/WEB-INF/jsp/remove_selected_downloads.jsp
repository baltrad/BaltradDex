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
Document   : Subscription management page. 
Created on : Oct 5, 2010, 3:06 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Subscription management" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Subscription management - data download
            </div>
            <div class="blttext">
                <div class ="alert">
                    <div class="icon">
                        <img src="includes/images/icons/circle-alert.png" 
                             alt="">
                    </div>
                    <div class="text">
                        <br/>
                        The following subscriptions will be permanently removed
                        from the system.
                    </div>
                </div>
            </div> 
            <div class="table">
                <div class="subscriptions">
                    <form action="remove_downloads_status.htm" method="post">
                        <div class="tableheader">
                            <div id="cell" class="count">&nbsp;</div>
                            <div id="cell" class="name">
                                Data source
                            </div>
                            <div id="cell" class="operator">
                                Operator
                            </div>
                        </div>
                        <c:set var="count" scope="page" value="1"/>
                        <c:forEach items="${selected_downloads}" var="sub">
                            <div class="entry">
                                <div id="cell" class="count">
                                    <c:out value="${count}"/>
                                    <c:set var="count" value="${count + 1}"/>
                                </div>
                                <div id="cell" class="name">
                                    <c:out value="${sub.dataSource}"/>
                                </div>
                                <div id="cell" class="operator">
                                    <c:out value="${sub.user}"/>
                                </div>
                            </div>
                        </c:forEach>
                        <div class="tablefooter">
                            <button class="rounded" type="button"
                                onclick="window.location='remove_downloads.htm'">
                                <span>Back</span>
                            </button>
                            <button class="rounded" type="submit">
                                <span>OK</span>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </jsp:body>
</t:page_tabbed>

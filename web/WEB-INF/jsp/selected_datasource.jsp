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
Document   : Shows peer data sources selected for subscription
Created on : May 11, 2012, 11:54 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Connect" activeTab="exchange">
    <jsp:body>
        <div class="left">
            <t:menu_exchange/>
        </div>
        <div class="right"> 
            <div class="blttitle">
                <img src="includes/images/icons/connection.png" alt="">
                Connected to <c:out value="${peer_name}"/>
            </div>
            <div class="blttext">
                Data sources selected for subscription. Click <i>Submit</i>
                to send subscription request to the peer node.
            </div>
            <div class="table">
                <div class="dsconnect">
                    <form action="subscribe.htm?peer_name=${peer_name}" 
                          method="post">
                        <div class="tableheader">
                            <div id="cell" class="count">&nbsp;</div>
                            <div id="cell" class="name">
                                Name
                            </div>
                            <div id="cell" class="description">
                                Description
                            </div>
                        </div>
                        <c:set var="count" scope="page" value="1"/>
                        <c:forEach items="${data_sources}" var="ds">
                            <div class="entry">
                                <div id="cell" class="count">
                                    <c:out value="${count}"/>
                                    <c:set var="count" value="${count + 1}"/>
                                </div>
                                <div id="cell" class="name">
                                    <c:out value="${ds.name}"/>
                                </div>
                                <div id="cell" class="description">
                                    <c:out value="${ds.description}"/>
                                </div>
                                <div id="cell" class="check">
                                    <input type="checkbox" class="hidden" 
                                           name="selected_data_sources"
                                           value="${ds.id}_${ds.name}_${ds.description}"
                                           checked/>
                                </div>
                            </div>
                        </c:forEach>
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                        onclick="history.back()">
                                    <span>Back</span>
                                </button>
                                <button class="rounded" type="submit">
                                    <span>Submit</span>
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>      
    </jsp:body>
</t:page_tabbed>
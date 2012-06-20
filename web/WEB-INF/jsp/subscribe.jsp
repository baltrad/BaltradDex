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
Document   : Shows subscription status
Created on : May 23, 2012, 12:07 AM
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
                Data source subscription status.
            </div>
            <div class="table">
                <%@include file="/WEB-INF/jsp/messages.jsp"%>
                <div class="dsconnect">
                    <div class="tablefooter">
                       <div class="buttons">
                           <button class="rounded" type="button"
                                onclick="window.location.href='subscribed_peers.htm'">
                                <span>OK</span>
                           </button>
                       </div>
                   </div>
                </div>
            </div>
        </div>      
    </jsp:body>
</t:page_tabbed>

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
Document   : Manage keystore page
Created on : Mar 4, 2013, 12:31 PM
Author     : szewczenko
------------------------------------------------------------------------------%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Manage keystore" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Manage keystore
            </div>
            <div class="blttext">
                Use functionality below to manage keystore
            </div>
            
            
            
            
            <div class="table">
                <div class="tableheader">
                    <div id="cell">
                        Key name
                    </div>
                    <div id="cell">
                        Checksum
                    </div>
                    <div id="cell">
                        Status
                    </div>
                    <div id="cell">
                        Modify
                    </div>
                </div>
                <c:forEach var="key" items="${keys}">
                    
                    <div id="cell">
                        <c:out value="${key.name}"/>
                    </div>
                    <div id="cell">
                        <c:out value="${key.checksum}"/>
                    </div>
                    <div id="cell">
                        <c:out value="${key.authorized}"/>
                    </div>
                    
                    
                </c:forEach>
            </div>
           
            
            
            
        </div>
    </jsp:body>
</t:page_tabbed>

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
Document   : Displays diagnostic messages 
Created on : May 18, 2012, 8:58:02 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<c:if test="${not empty error_message}">
    <div class="systemerror">
        <div class="header">
            Problems encountered.
        </div>
        <div class="message">
            <c:out value="${error_message}"/>
            <c:set var="error_message" value="" scope="session" />
        </div>        
        <c:if test="${not empty error_details}">
            <div class="msg_head" id="msg_node">
                Show details
            </div>
            <div class="msg_body">
                <c:out value="${error_details}"/>
                <c:set var="error_details" value="" scope="session" />
            </div>
        </c:if>        
    </div>
</c:if> 
<c:if test="${not empty success_message}">
    <div class="systemmessage">
        <div class="header">
            Success.
        </div>
        <div class="message">
            <c:out value="${success_message}"/>
            <c:set var="success_message" value="" scope="session" />
        </div>        
        <c:if test="${not empty success_details}">
            <div class="msg_head" id="msg_node">
                Show details
            </div>
            <div class="msg_body">
                <c:out value="${success_details}"/>
                <c:set var="success_details" value="" scope="session" />
            </div>
        </c:if>        
    </div>
</c:if>                

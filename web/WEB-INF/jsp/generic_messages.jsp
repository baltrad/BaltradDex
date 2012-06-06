<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW

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
----------------------------------------------------------------------------------------------------
Document   : Display messages page
Created on : Jun 22, 2010, 11:57:02 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!-- Error message -->
<c:if test="${not empty error}">
    <div class="systemerror">
        <div class="header">
            Problems encountered.
        </div>
        <div class="message">
            <c:out value="${error}"/>
            <c:set var="error" value="" scope="session" />
        </div>
    </div>
</c:if>
<!-- Status message -->
<c:if test="${not empty message}">
    <div class="systemmessage">
        <div class="header">
            Success.
        </div>
        <div class="message">
            <c:out value="${message}"/>
            <c:set var="message" value="" scope="session" />
        </div>
    </div>
</c:if>


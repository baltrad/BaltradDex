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
Document   : Sticky message counter
Created on : Sep 4, 2013, 11:07:59 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Sticky message counter</title>
    </head>
    <body>
        <c:if test="${message_count > 0}">
            <div id="sticky-message">
                <c:choose>
                    <c:when test="${message_count == 1}">
                        You have <c:out value="${message_count}"/> sticky message. 
                    </c:when>
                    <c:otherwise>
                        You have <c:out value="${message_count}"/> sticky 
                        messages. 
                    </c:otherwise>
                </c:choose>
                Click <a href="messages_sticky.htm">here</a> to read.
            </div>
        </c:if>
    </body>
</html>

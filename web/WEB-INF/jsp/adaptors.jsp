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
List of adaptors
@date 2010-03-23
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@ page import="java.util.List" %>

<%
    // Check if there are adaptors available to display
    List adaptors = ( List )request.getAttribute( "adaptors" );
    if( adaptors == null || adaptors.size() <= 0 ) {
        request.getSession().setAttribute( "adaptors_status", 0 );
    } else {
        request.getSession().setAttribute( "adaptors_status", 1 );
    }
%>

<t:generic_page pageTitle="Adaptors">
    <jsp:body>
        <div class="adaptors">
            <div class="table">
                <div class="header">
                    <div class="row">Adaptors</div>
                </div>
                <div class="header-text">
                    Click on adaptor name to modify or delete or click
                    <i>Create</i> to create a new adaptor. 
                </div>
                <form name="createAdaptorForm" action="adaptor_create.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>
                    <c:choose>
                        <c:when test="${adaptors_status == 1}">
                            <div class="body">
                                <div class="header-row">
                                    <div class="count">&nbsp;</div>
                                    <div class="name">Name</div>
                                    <div class="type">Type</div>
                                </div>
                                <c:set var="count" scope="page" value="1"/>
                                <c:forEach var="adaptor" items="${adaptors}">
                                    <div class="row">
                                        <div class="count">
                                            <c:out value="${count}"/>
                                            <c:set var="count" value="${count + 1}"/>
                                        </div>
                                        <div class="name">
                                            <a href="adaptor_show.htm?name=${adaptor.name}">
                                                <c:out value="${adaptor.name}"/>
                                            </a>
                                        </div>
                                        <div class="type">
                                            <c:out value="${adaptor.type}"/>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>  
                        </c:when>
                    </c:choose>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="submit" 
                                       value="Create"/>
                            </div>
                        </div>
                    </div>                      
                </form>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>

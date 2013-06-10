<%------------------------------------------------------------------------------
Copyright (C) 2009-2013 Swedish Meteorological and Hydrological Institute, SMHI

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
Supervisor settings
@date 2013-01-18
@author Anders Henja
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%@ page import="java.util.List" %>
<%
    // Check if there are adaptors available to display
    List filters = ( List )request.getAttribute( "filters" );
    if( filters == null || filters.size() <= 0 ) {
        request.getSession().setAttribute( "filters_status", 0 );
    } else {
        request.getSession().setAttribute( "filters_status", 1 );
    }
%>

<t:generic_page pageTitle="Supervisor settings">
    <jsp:body>
        <div class="supervisor">
            <div class="table">
                <div class="header">
                    <div class="row">Supervisor settings</div>
                </div>
                <div class="header-text">
                    Configure allowed supervisor IP-addresses. 
                </div>
                <form name="addSupervisorSettingForm" 
                      action="supervisor_add_setting.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>        
                    <div class="section">
                        Add IP address filter
                    </div>
                    <div class="section-text">
                        The format of the filter should <i>always</i> 
                        be N.N.N.N where N either can be * or a value 
                        between 0-255.
                        The supervisor will always allow connection attempts 
                        from localhost (127.0.0.1).<br/></br>
                        Your current IP address is ${currentip}.
                    </div>
                    <div class="row" id="filter-add">
                        <input type="text" name="filter" value="${filter}"
                               title="Enter filter address"/>
                    </div>    
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="submit" 
                                       value="Add"/>
                            </div>
                        </div>
                    </div>  
                </form>  
                <c:choose>
                    <c:when test="${filters_status == 1}">
                        <div class="body">
                            <div class ="header-row">
                                <div class="count">&nbsp;</div>
                                <div class="filter">Filter</div>
                                <div class="delete">&nbsp;</div>
                            </div>
                            <c:set var="count" scope="page" value="1"/>
                            <c:forEach var="filter" items="${filters}">
                                <div class="row">
                                    <div class="count">
                                        <c:out value="${count}"/>
                                        <c:set var="count" value="${count + 1}"/>
                                    </div>
                                    <div class="filter">
                                        <c:out value="${filter}"/>
                                    </div>
                                    <div class="delete">
                                        <form name="removeSupervisorSettingForm" 
                                              action="supervisor_remove_setting.htm">
                                            <input type="hidden" 
                                                   value="${filter}" 
                                                   name="filter"/>
                                            <button type="submit" 
                                                    class="delete-filter"
                                                    title="Delete filter">
                                                Delete
                                            </button>
                                        </form>
                                    </div> 
                                </div>
                            </c:forEach>
                        </div>                
                    </c:when>
                </c:choose>  
            </div>
        </div>
    </jsp:body>
</t:generic_page>
    
      
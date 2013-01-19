<%--------------------------------------------------------------------------------------------------
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
----------------------------------------------------------------------------------------------------
List of adaptors
@date 2013-01-18
@author Anders Henja
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
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

<t:page_tabbed pageTitle="Supervisor settings" activeTab="processing">
    <jsp:body>
        <div class="left">
            <t:menu_processing/>
        </div>
        <div class="right">
            <div class="blttitle">
                Supervisor settings
            </div>
            <div class="blttext">
                Here you can configure allowed supervisor ip-addresses. The supervisor will always
                allow connection atempts from localhost (127.0.0.1) so there is no reason to add that
                filter. The format of the filter should <b>always</b> be N.N.N.N where N either can be *
                or a value between 0-255.
                <br/>
            </div>
            <div class="table">
                <t:error_message message="${emessage}"/>
                <div class="supervisor">
                  <div class="tableheader">
                    <div id="cell" class="count">&nbsp;</div>
                    <div id="cell" class="filter">Filter</div>
                    <div id="cell" class="filter_space">&nbsp;</div>
                    <div id="cell" class="supervisor_button">&nbsp;</div>
                  </div>
                  <c:choose>
                    <c:when test="${filters_status == 1}">
                      <c:forEach var="filter" items="${filters}">
                        <div class="entry">
                          <div id="cell" class="count">
                            &nbsp;
                          </div>
                          <div id="cell" class="supervisorfilter">
                            <c:out value="${filter}"/>
                          </div>
                          <div id="cell" class="supervisorfilter_space">
                            &nbsp;
                          </div>
                          <div id="cell" class="supervisor_button">
                            <form name="removeSupervisorSettingForm" action="removesupervisorsetting.htm">
                              <input type="hidden" value="${filter}" name="filter"/>
                              <button class="rounded" type="submit">
                                <span>Delete</span>
                              </button>
                            </form>
                          </div>
                        </div>
                      </c:forEach>
                    </c:when>
                  </c:choose>
                  <form name="addSupervisorSettingForm" action="addsupervisorsetting.htm">
                    <br/>
                    <br/>
                    <div class="entry">
                      <div id="cell" class="count">
                        &nbsp;
                      </div>
                      <div id="cell" class="supervisorfilter">
                        <input type="text" name="filter" value="${filter}"/>
                      </div>
                      <div id="cell" class="supervisorfilter_space">
                        &nbsp;
                      </div>
                      <div id="cell" class="supervisor_button">
                        <button class="rounded" type="submit">
                          <span>Add</span>
                        </button>
                      </div>
                    </div>
                    <br/>
                    <div class="tablefooter">
                      <div class="buttons">
                          <button class="rounded" type="button"
                                  onclick="window.location.href='processing.htm'">
                              <span>Back</span>
                          </button>
                      </div>
                    </div>
                  </form>
                </div>
            </div>      
        </div>
    </jsp:body>
</t:page_tabbed>
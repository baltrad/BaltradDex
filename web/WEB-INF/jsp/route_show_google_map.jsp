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
Modifies a google map route
@date 2012-03-29
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:generic_page pageTitle="Edit route">
    <jsp:body>
        <div class="routes">
            <div class="table">
                <div class="header">
                    <div class="row">Edit route</div>
                </div>
                <div class="header-text">
                     Modify Google Map routing rule. 
                </div>
                <form name="showRouteForm" action="route_show_google_map.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>
                    <div class="body">
                        <div class="row2">
                            <div class="leftcol">Name:</div>
                            <div class="rightcol">
                                <c:out value="${name}"/>
                                <input type="hidden" name="name" value="${name}"/>
                            </div>        
                        </div>
                        <div class="row2">
                            <div class="leftcol">Author:</div>
                            <div class="rightcol">
                                <input type="text" name="author" 
                                       value="${author}" 
                                       title="Route author's name"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Active:</div>
                            <div class="rightcol">
                                <input type="checkbox" name="active" 
                                       title="Check to activate route"
                                       <c:if test="${active == true}">checked</c:if> />
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Description:</div>
                            <div class="rightcol">
                                <input type="text" name="description" 
                                       value="${description}"
                                       title="Route's description"/>
                            </div>
                        </div>
                        <div class="row2">
                            <datalist id="arealist">
                              <c:forEach var="areaid" items="${arealist}">
                                <option value="${areaid}"><c:out value="${areaid}"/></option>
                              </c:forEach>
                            </datalist>                          
                            <div class="leftcol">Area:</div>
                            <div class="rightcol">
                                <input type="text" name="area" value="${area}"
                                       title="An area defining the region for which this route should be triggered" list="arealist"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Path:</div>
                            <div class="rightcol">
                                 <input type="text" name="path" value="${path}"
                                        title="The base path (e.g. /var/www/html/data) where the generated PNG image should be placed."/>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Recipients:</div>
                            <div class="rightcol">
                                <select id="recipients" multiple size="4" 
                                        name="recipients" 
                                        title="Select target adaptors">
                                    <c:forEach var="adaptor" items="${adaptors}">
                                        <option value="${adaptor}" <c:if test="${ fn:contains(recipients, adaptor) }">selected</c:if> >${adaptor}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="submit"
                                       name="submitButton" value="Save"/>
                            </div>
                            <div class="button-wrap">
                                <input class="button" type="submit"
                                       name="submitButton" value="Delete"/>
                            </div>
                        </div>
                    </div>                      
                </form>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>

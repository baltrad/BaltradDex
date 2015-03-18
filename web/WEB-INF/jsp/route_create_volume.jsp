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
Creates a volume route
@date 2011-01-06
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:generic_page pageTitle="Create route">
    <jsp:body>
        <div class="routes">
            <div class="table">
                <div class="header">
                    <div class="row">Create route</div>
                </div>
                <div class="header-text">
                     Create volume routing rule. 
                </div>
                <form name="createRouteForm" action="route_create_volume.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>
                    <div class="body">
                        <div class="row2">
                            <div class="leftcol">Name:</div>
                            <div class="rightcol">
                                 <input type="text" name="name" 
                                        value="${name}" title="Route name"/>
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
                            <div class="leftcol">Ascending:</div>
                            <div class="rightcol">
                                <input type="checkbox" name="ascending"
                                       title="Select ascending order"
                                       <c:if test="${ascending == true}">checked</c:if> />
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Minimum elevation:</div>
                            <div class="rightcol">
                                <input type="text" name="mine" value="${mine}"
                                       title="Specify minimum elevation angle"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Maximum elevation:</div>
                            <div class="rightcol">
                                <input type="text" name="maxe" value="${maxe}"
                                       title="Specify maximum elevation angle"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Elevation angles:</div>
                            <div class="rightcol">
                                <input type="text" name="elangles" 
                                       value="${elangles}"
                                       title="Comma separated list of elevation angles in format x.y"/>
                            </div>
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
                        <div class="row2">
                            <div class="leftcol">Interval:</div>
                            <div class="rightcol">
                                <select name="interval" title="Select interval">
                                    <c:forEach var="iv" items="${intervals}">
                                        <option value="${iv}" <c:if test="${interval == iv}">selected</c:if> >${iv}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Timeout:</div>
                            <div class="rightcol">
                                <input type="text" name="timeout" 
                                       value="${timeout}"
                                       title="Timeout in seconds"/>
                            </div>        
                        </div>
                        <div class="row2">
                            <div class="leftcol">Sources:</div>
                            <div class="rightcol">
                                <select id="sources" multiple size="6" 
                                        name="sources"
                                        title="Select source radars">
                                    <c:forEach var="id" items="${sourceids}">
                                        <option value="${id}" <c:if test="${ fn:contains(sources, id) }">selected</c:if> >${id}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Quality controls:</div>
                            <div class="rightcol">
                                <select id="quality-controls" multiple size="6" 
                                        name="detectors"
                                        title="Select quality controls to be used">
                                    <c:forEach var="detector" items="${anomaly_detectors}">
                                        <option value="${detector.name}" <c:if test="${ fn:contains(detectors, detector.name) }">selected</c:if> >${detector.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
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
            </div>
        </div>
    </jsp:body>
</t:generic_page>

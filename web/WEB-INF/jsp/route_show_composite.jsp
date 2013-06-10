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
Modify a composite route
@date 2010-05-13
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
                     Modify composite routing rule. 
                </div>
                <form name="createRouteForm" action="route_show_composite.htm">
                    <t:message_box errorHeader="Problems encountered."
                                   errorBody="${emessage}"/>
                    <div class="body">
                        <div class="row2">
                            <div class="leftcol">Name:</div>
                            <div class="rightcol">
                                 <c:out value="${name}"/>
                                 <input type="hidden" name="name" 
                                        value="${name}"/>    
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
                            <div class="leftcol">Scan-based:</div>
                            <div class="rightcol">
                                <input type="checkbox" name="byscan" 
                                       title="Check to select scan-based route"
                                       <c:if test="${byscan == true}">checked</c:if> />
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Method:</div>
                            <div class="rightcol">
                                <select name="method" 
                                        title="Choose a method to use for generating the composite.">
                                    <option value="pcappi" <c:if test="${method == 'pcappi'}">selected</c:if> >PCAPPI</option>
                                    <option value="ppi" <c:if test="${method == 'ppi'}">selected</c:if> >PPI</option>
                                    <option value="cappi" <c:if test="${method == 'cappi'}">selected</c:if> >CAPPI</option>
                                    <option value="max" <c:if test="${method == 'max'}">selected</c:if> >MAX</option>
                                    <option value="pmax" <c:if test="${method == 'pmax'}">selected</c:if> >PMAX</option>
                                 </select>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Product parameter:</div>
                            <div class="rightcol">
                                <input type="text" name="prodpar" value="${prodpar}"
                                       title="Product parameter associated with the method, e.g. elevation angle for PPI"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Selection method:</div>
                            <div class="rightcol">
                                <select name="selection_method" 
                                        title="Choose a selection method">
                                    <option value="0" <c:if test="${selection_method == 0}">selected</c:if> >Nearest radar</option>
                                    <option value="1" <c:if test="${selection_method == 1}">selected</c:if> >Nearest sea level</option>
                                </select>
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
                            <div class="leftcol">Area ID:</div>
                            <div class="rightcol">
                                <input type="text" name="areaid" 
                                       value="${areaid}" title="Select area ID"/>
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
                                <input class="button" name="submitButton"
                                       type="submit" value="Save"/>
                            </div>
                            <div class="button-wrap">
                                <input class="button" name="submitButton"
                                       type="submit" value="Delete"/>
                            </div>
                        </div>
                    </div>                      
                </form>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>

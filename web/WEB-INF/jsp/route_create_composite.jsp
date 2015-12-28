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
Creates a composite route
@date 2010-05-13
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:generic_page pageTitle="Create route">
    <jsp:attribute name="extraHeader">
        <script type="text/javascript"
                src="//ajax.microsoft.com/ajax/jquery.templates/beta1/jquery.tmpl.min.js">
        </script>

        <script type="text/javascript">
            $(document).ready(function() {
                $('#btn-up').bind('click', function() {
                    $('#quality-controls option:selected').each( function() {
                        var newPos = $('#quality-controls option').index(this) - 1;
                        if (newPos > -1) {
                            $('#quality-controls option').eq(newPos).before("<option value='"+$(this).val()+"' selected='selected'>"+$(this).text()+"</option>");
                            $(this).remove();
                        }
                    });
                });
                $('#btn-down').bind('click', function() {
                    var countOptions = $('#quality-controls option').size();
                    $('#quality-controls option:selected').each( function() {
                        var newPos = $('#quality-controls option').index(this) + 1;
                        if (newPos < countOptions) {
                            $('#quality-controls option').eq(newPos).after("<option value='"+$(this).val()+"' selected='selected'>"+$(this).text()+"</option>");
                            $(this).remove();
                        }
                    });
                });
            });
        </script>
    </jsp:attribute>   
    <jsp:body>
        <div class="routes">
            <div class="table">
                <div class="header">
                    <div class="row">Create route</div>
                </div>
                <div class="header-text">
                     Create composite routing rule. 
                </div>
                <form name="createRouteForm" action="route_create_composite.htm">
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
                            <datalist id="arealist">
                              <c:forEach var="areaid" items="${arealist}">
                                <option value="${areaid}"><c:out value="${areaid}"/></option>
                              </c:forEach>
                            </datalist>                        
                            <div class="leftcol">Area ID:</div>
                            <div class="rightcol">
                                <input type="text" name="areaid" 
                                       value="${areaid}" title="Select area ID" list="arealist"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Quantity:</div>
                            <div class="rightcol">
                                <input type="text" name="quantity" 
                                       value="${quantity}" title="The quantity that should be be used for compositing."/>
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
                            <div class="leftcol">Apply GRA:</div>
                            <div class="rightcol">
                                <input type="checkbox" name="applygra" 
                                       title="Check to select tha GRA correction should be applied"
                                       <c:if test="${applygra == true}">checked</c:if> />
                            </div>        
                        </div> 
                        <div class="row2">
                            <div class="leftcol">ZR A:</div>
                            <div class="rightcol">
                                <input type="text" name="ZR_A" 
                                       value="${ZR_A}"
                                       title="The ZR_A coefficient when converting from reflectivity to MM/H"/>
                            </div>        
                        </div>                                               
                        <div class="row2">
                            <div class="leftcol">ZR b:</div>
                            <div class="rightcol">
                                <input type="text" name="ZR_b" 
                                       value="${ZR_b}"
                                       title="The ZR_b coefficient when converting from reflectivity to MM/H"/>
                            </div>        
                        </div>          
                        <div class="row2">
                            <div class="leftcol">Ignore malfunc:</div>
                            <div class="rightcol">
                                <input type="checkbox" name="ignore_malfunc" 
                                       title="Check to select that scans/volumes with how/malfunc=True should be ignored"
                                       <c:if test="${ignore_malfunc == true}">checked</c:if> />
                            </div>        
                        </div> 
                        <div class="row2">
                            <div class="leftcol">CT-filter:</div>
                            <div class="rightcol">
                                <input type="checkbox" name="ctfilter" 
                                       title="Check to select that ct-filtering should be performed"
                                       <c:if test="${ctfilter == true}">checked</c:if> />
                            </div>        
                        </div>
                        <div class="row2">
                            <div class="leftcol">QI-total Field:</div>
                            <div class="rightcol">
                                <input type="text" name="qitotal_field" 
                                       value="${qitotal_field}" title="QI total field. If not empty, then compositing will be based on quality field"/>
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
                                <a href="JavaScript:void(0);" id="btn-up"><img src="includes/images/up.png" alt="Up"/></a>
                                <a href="JavaScript:void(0);" id="btn-down"><img src="includes/images/down.png" alt="Down"/></a>
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

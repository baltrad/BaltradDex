<%------------------------------------------------------------------------------
Copyright (C) 2009-2014 Swedish Meteorological and Hydrological Institute, SMHI

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
Modify a site2d route
@date 2014-08-26
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:generic_page pageTitle="Edit route">
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
        <script type="text/javascript"
                src="includes/js/jquery.serializeJSON.js">
        </script>
        <script type="text/javascript"
                src="includes/js/json2.js">
        </script>
        <script type="text/javascript"
                src="includes/js/jquery.postJSON.js">
        </script>
        <script type="text/javascript"
                src="includes/js/filter.js">
        </script>
        <script type="text/javascript">
            // prevent executing the function twice
            var ready;
            $(document).ready(function() {
                if (!ready) {
                    var filter = null;
                    <c:choose>
                      <c:when test="${!empty filterJson}">
                        filter = createBdbFilter(${filterJson});
                      </c:when>
                      <c:otherwise>
                        filter = createBdbFilter({
                          type: "combined",
                          matchType: "ALL",
                          childFilters: [{
                            type: "always"
                          }]
                        });
                      </c:otherwise>
                    </c:choose>
                    $("#filter").append(filter.dom);
                    var submit = $("[name='submitButton']");
                    submit.click(function(evt) {
                      filter.updateDataFromDom();
                      if (!isValidBdbFilter(filter.data)) {
                        evt.preventDefault();
                        alert("invalid filter");
                      } else {
                        $("#filterJson").val(JSON.stringify(filter.data));
                        $("#filter").empty();
                      }
                    });
                    ready = true;
                }
            });
        </script>
    </jsp:attribute>    
    <jsp:body>
        <div class="routes">
            <div class="table">
                <div class="header">
                    <div class="row">Edit route</div>
                </div>
                <div class="header-text">
                     Modify site2d routing rule. 
                </div>
                <form name="createRouteForm" action="route_show_site2d.htm">
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
                            <div class="leftcol">Recipients:</div>
                            <div class="rightcol">
                                <select id="recipients" multiple size="4" 
                                        name="recipients" 
                                        title="Select target adaptors">
                                    <c:forEach var="adaptor" items="${adaptors}">
                                        <c:set var="adaptor_selected" value="false" />
                                        <c:forEach var="recipient" items="${recipients}">
                                            <c:if test="${recipient eq adaptor}">
                                                <c:set var="adaptor_selected" value="true" />
                                            </c:if>
                                        </c:forEach>
                                        <option value="${adaptor}" <c:if test="${adaptor_selected}">selected</c:if> >${adaptor}</option>
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
                            <datalist id="pcslist">
                              <c:forEach var="pid" items="${pcslist}">
                                <option value="${pid}"><c:out value="${pid}"/></option>
                              </c:forEach>
                            </datalist>                          
                            <div class="leftcol">Pcs ID:</div>
                            <div class="rightcol">
                                <input type="text" name="pcsid" 
                                       value="${pcsid}" title="Pcs ID to use for best-fit" list="pcslist"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">X-scale:</div>
                            <div class="rightcol">
                                <input type="text" name="xscale" 
                                       value="${xscale}" title="X-scale to use for best-fit"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Y-scale:</div>
                            <div class="rightcol">
                                <input type="text" name="yscale" 
                                       value="${yscale}" title="Y-scale to use for best-fit"/>
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
                            <div class="leftcol">Options:</div>
                            <div class="rightcol">
                                <input type="text" name="options" 
                                       value="${options}"
                                       title="Extra options to the Site2D plugin"/>
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
                                            <c:set var="contains" value="false" />
                                            <c:forEach var="dstr" items="${detectors}">
                                                <c:if test="${ dstr eq detector.name }">
                                                    <c:set var="contains" value="true" />
                                                </c:if>
                                            </c:forEach>
                                            <option value="${detector.name}" <c:if test="${ contains == true}">selected</c:if> >${detector.name}</option>
                                        </c:forEach>
                                </select>
                                <a href="JavaScript:void(0);" id="btn-up"><img src="includes/images/up.png" alt="Up"/></a>
                                <a href="JavaScript:void(0);" id="btn-down"><img src="includes/images/down.png" alt="Down"/></a>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Quality controls mode</div>
                            <div class="rightcol">
                                <select name="quality_control_mode" 
                                        title="Choose a quality control mode">
                                    <option value="0" <c:if test="${quality_control_mode == 0}">selected</c:if> >Analyze &amp; Apply</option>
                                    <option value="1" <c:if test="${quality_control_mode == 1}">selected</c:if> >Analyze only</option>
                                </select>
                            </div>
                        </div>                        
                        <div class="row2">
                            <div class="bdb-filter-text">
                                Select filter parameters
                            </div> 
                            <div class="bdb-filter">
                                <div id="filter"></div>
                                <input type="hidden" name="filterJson" 
                                       id="filterJson" />
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
                            <div class="button-wrap">
                                <input class="button" name="submitButton"
                                       type="submit" value="Duplicate"/>
                            </div>
                        </div>
                    </div>                      
                </form>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>

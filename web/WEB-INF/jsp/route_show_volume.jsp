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
Modifie or delete a volume route
@date 2011-01-05
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
                    <div class="row">Create route</div>
                </div>
                <div class="header-text">
                     Modify or delete a volume routing rule. 
                </div>
                <form name="createRouteForm" action="route_show_volume.htm">
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
                            <div class="leftcol">Adaptive elevation handling:</div>
                            <div class="rightcol">
                                <input type="checkbox" name=adaptive_elangles 
                                       title="Check to enable adaptive elevation handling"
                                       <c:if test="${adaptive_elangles == true}">checked</c:if> />
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
                            <div class="leftcol">Nominal timeout:</div>
                            <div class="rightcol">
                                <input type="checkbox" name="nominal_timeout" 
                                       title="If nominal time should be used as base for timeouts"
                                       <c:if test="${nominal_timeout == true}">checked</c:if> />
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

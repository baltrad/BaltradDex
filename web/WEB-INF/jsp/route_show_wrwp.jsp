<%------------------------------------------------------------------------------
Copyright (C) 2009-2013 Swedish Hydrological and Meteorological Institute, SMHI

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
Modify or delete a wrwp route
@date 2013-09-24
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:generic_page pageTitle="Create route">
    <jsp:attribute name="extraHeader">
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
                     Modify or delete WRWP routing rule. <br/>
                </div>
                <form name="showRouteForm" 
                      action="route_show_wrwp.htm">
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
                            <div class="leftcol">Interval:</div>
                            <div class="rightcol">
                                <input type="text" name="interval" value="${interval}"
                                       title="Specify height interval"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Maximum height:</div>
                            <div class="rightcol">
                                <input type="text" name="maxheight" value="${maxheight}"
                                       title="Specify maximum profile height"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Minimum distance:</div>
                            <div class="rightcol">
                                <input type="text" name="mindistance" value="${mindistance}"
                                       title="Minimum distance for deriving a profile"/>
                            </div>
                        </div>         
                        <div class="row2">
                            <div class="leftcol">Maximum distance:</div>
                            <div class="rightcol">
                                <input type="text" name="maxdistance" value="${maxdistance}"
                                       title="Maximum distance for deriving a profile"/>
                            </div>
                        </div>         
                        <div class="row2">
                            <div class="leftcol">Min elevation angle:</div>
                            <div class="rightcol">
                                <input type="text" name="minelangle" value="${minelangle}"
                                       title="Minimum elevation angle [deg]"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Max elevation angle:</div>
                            <div class="rightcol">
                                <input type="text" name="maxelangle" value="${maxelangle}"
                                       title="Maximum elevation angle [deg]"/>
                            </div>
                        </div>      
                        <div class="row2">
                            <div class="leftcol">Radial velocity threshold:</div>
                            <div class="rightcol">
                                <input type="text" name="minvelocitythreshold" value="${minvelocitythreshold}"
                                       title="Radial velocity threshold [m/s]"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Upper threshold for calculated velocity:</div>
                            <div class="rightcol">
                                <input type="text" name="maxvelocitythreshold" value="${maxvelocitythreshold}"
                                       title="Upper threshold for calculated velocity [m/s]"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Min sample size for reflectivity:</div>
                            <div class="rightcol">
                                <input type="text" name="minsamplesizereflectivity" value="${minsamplesizereflectivity}"
                                       title="Minimum sample size for reflectivity"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Min sample size for wind:</div>
                            <div class="rightcol">
                                <input type="text" name="minsamplesizewind" value="${minsamplesizewind}"
                                       title="Minimum sample size for wind"/>
                            </div>
                        </div>    
                        <div class="row2">
                            <div class="leftcol">Fields:</div>
                            <div class="rightcol">
                                <select id="fields" multiple size="4" 
                                        name="fields" 
                                        title="Select fields">
                                    <c:forEach var="available_field" items="${available_fields}">
                                        <c:set var="field_selected" value="false" />
                                        <c:forEach var="field" items="${fields}">
                                            <c:if test="${field eq available_field}">
                                                <c:set var="field_selected" value="true" />
                                            </c:if>
                                        </c:forEach>
                                        <option value="${available_field}" <c:if test="${field_selected}">selected</c:if> >${available_field}</option>
                                    </c:forEach>
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

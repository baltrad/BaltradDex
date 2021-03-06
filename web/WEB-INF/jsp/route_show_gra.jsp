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
Creates a gra route
@date 2014-01-15
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:generic_page pageTitle="Create route">
    <jsp:attribute name="extraHeader">
        <script type="text/javascript"
                src="//ajax.microsoft.com/ajax/jquery.templates/beta1/jquery.tmpl.min.js">
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
                    <div class="row">Modify or delete route</div>
                </div>
                <div class="header-text">
                     Modify or delete GRA routing rule. <br/>
                     <b>This rule is triggered by the scheduler</b> and will determine the files to include in the accumulation as follows: <br/>
                     files per hour gives the interval, e.g. files per hour = 4, gives a 15 minute interval (00,15,30,45).<br/>
                     First term UTC is the offset in hours to the first observation term. This is the time when the term ends. Interval defines
                     how many hours there are in each term. For example, if first term utc = 6 and interval = 12, then the day is divided into two periods,
                     one between 0600 to 1759 and the other between 1800 and 0559.
                     When this rule is triggered by the scheduler, the nominal time will be the closest interval time in the present.<br/> 
                     The accumulation will either be performed on a COMP or IMAGE product and a quantity, most likely DBZH or TH even
                     if you can specify any quantity.<br/>
                     You might have to specify a distance field depending on how the GRA algorithm has been implemented by the PGF.                     
                     <br/>
                </div>
                <form name="showRouteForm" 
                      action="route_show_gra.htm">
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
                            <div class="leftcol">Object type:</div>
                            <div class="rightcol">
                                <select name="object_type" title="Select type">
                                    <c:forEach var="iv" items="${object_types}">
                                        <option value="${iv}" <c:if test="${object_type == iv}">selected</c:if> >${iv}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Quantity:</div>
                            <div class="rightcol">
                                <input type="text" name="quantity" value="${quantity}"
                                       title="The quantity that should be be accumulated."/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Files per hour:</div>
                            <div class="rightcol">
                                <input type="text" name="filesPerHour" value="${filesPerHour}"
                                       title="The number of files per hour to be used. E.g if 4, then files at 00,15,30 and 45 will be used."/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Acceptable loss:</div>
                            <div class="rightcol">
                                <input type="text" name="acceptableLoss" value="${acceptableLoss}"
                                       title="The acceptable loss in percent (0-100)."/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Distance field:</div>
                            <div class="rightcol">
                                <input type="text" name="distanceField" value="${distanceField}"
                                       title="The quality field for distance. If not specified, eu.baltrad.composite.quality.distance.radar will be used."/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Zr A:</div>
                            <div class="rightcol">
                                <input type="text" name="zrA" value="${zrA}"
                                       title="The zr-A constant."/>
                            </div>
                        </div>                        
                        <div class="row2">
                            <div class="leftcol">Zr B:</div>
                            <div class="rightcol">
                                <input type="text" name="zrB" value="${zrB}"
                                       title="The zr-B constant."/>
                            </div>
                        </div>                
                        <div class="row2">
                            <div class="leftcol">First Term UTC:</div>
                            <div class="rightcol">
                                <input type="text" name="firstTermUTC" value="${firstTermUTC}"
                                       title="The offset in hours to the first observation term. This is the time when the term ends."/>
                            </div>
                        </div>      
                        <div class="row2">
                            <div class="leftcol">Interval:</div>
                            <div class="rightcol">
                                <input type="text" name="interval" value="${interval}"
                                       title="The number of hours of each term."/>
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

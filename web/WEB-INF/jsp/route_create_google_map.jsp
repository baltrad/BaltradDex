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
Creates a google map route
@date 2012-03-23
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
                    <div class="row">Create route</div>
                </div>
                <div class="header-text">
                     Create Google Map routing rule. 
                </div>
                <form name="createRouteForm" 
                      action="route_create_google_map.htm">
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
                            <datalist id="arealist">
                              <c:forEach var="areaid" items="${arealist}">
                                <option value="${areaid}"><c:out value="${areaid}"/></option>
                              </c:forEach>
                            </datalist>                          
                            <div class="leftcol">Area:</div>
                            <div class="rightcol">
                                <input type="text" name="area" value="${area}"
                                       title="An area defining the region for which this route should be triggered" list="arealist" />
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Use area in path:</div>
                            <div class="rightcol">
                                <input type="checkbox" name="areapath" 
                                       title="Check to use the area name in the path"
                                       <c:if test="${areapath == true}">checked</c:if> />
                            </div>
                        </div>                        
                        <div class="row2">
                            <div class="leftcol">Path:</div>
                            <div class="rightcol">
                                 <input type="text" name="path" value="${path}"
                                        title="The base path (e.g. /var/www/html/data) where the generated PNG image should be placed."/>
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
                                <input name="submitButton" class="button" type="submit" 
                                       value="Add"/>
                            </div>
                        </div>
                    </div>                      
                </form>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>

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
Creates a distribution route
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:generic_page pageTitle="${create ? 'Create' : 'Edit'} route">
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
                    <div class="row">${create ? 'Create' : 'Edit'} route</div>
                </div>
                <div class="header-text">
                     ${create ? 'Create' : 'Edit'} distribution routing rule.
                </div>
                <t:message_box errorHeader="Problems encountered."
                               errorBody="${emessage}"/>
                <t:form_route_common route="${route}" create="${create}"
                                     formAction="route_create_distribution.htm">
                    <jsp:attribute name="extraBottom">
                        <div class="row2">
                            <div class="leftcol">Destination:</div>
                            <div class="rightcol">
                                <input type="text" name="destination" 
                                       value="${destination}"
                                       title="URL of the destination to send files to"/>
                            </div>
                        </div>
                        <div class="row2">
                            <div class="leftcol">Name template:</div>
                            <div class="rightcol">
                                <input type="text" name="namingTemplate" 
                                       value="${namingTemplate}"
                                       title="Template to use when naming files (if empty, use UUID)"/>
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
                    </jsp:attribute>
                </t:form_route_common>
            </div>
        </div>
    </jsp:body>
</t:generic_page>

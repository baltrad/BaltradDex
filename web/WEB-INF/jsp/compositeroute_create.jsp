<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW

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
----------------------------------------------------------------------------------------------------
Creates a composite route
@date 2010-05-13
@author Anders Henja
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Create route" activeTab="processing">
    <jsp:body>
        <div class="left">
            <t:menu_processing/>
        </div>
        <div class="right">
            <div class="blttitle">
                Create route
            </div>
            <div class="blttext">
                Create a Composite routing rule.
            </div>
            <div class="table">
              <t:error_message message="${emessage}"/>
              <div class="modifyroute">
                <form name="createRouteForm" action="compositeroute_create.htm">
                  <div class="leftcol">
                    <div class="row">Name</div>
                    <div class="row">Author</div>
                    <div class="row">Active</div>
                    <div class="row">Description</div>
                    <div class="row">Scan based</div>
                    <div class="row">Method</div>
                    <div class="row">Product parameter</div>
                    <div class="row">Selection method</div>
                    <div class="row4">Recipients</div>
                    <div class="row">Areaid</div>
                    <div class="row">Interval</div>
                    <div class="row">Timeout</div>
                    <div class="row6">Sources</div>
                    <div class="row6">Quality controls</div>
                  </div>
                  <div class="rightcol">
                    <div class="row">
                      <input type="text" name="name" value="${name}"/>
                      <div class="hint">
                        Route name
                      </div>
                    </div>
                    <div class="row">
                      <input type="text" name="author" value="${author}"/>
                      <div class="hint">
                        Route author's name
                      </div>
                    </div>
                    <div class="row">
                      <input type="checkbox" name="active" <c:if test="${active == true}">checked</c:if> />
                      <div class="hint">
                        Check to activate route
                      </div>
                    </div>
                   <div class="row">
                     <input type="text" name="description" value="${description}"/>
                     <div class="hint">
                       Verbose description
                     </div>
                   </div>
                   <div class="row">
                     <input type="checkbox" name="byscan" <c:if test="${byscan == true}">checked</c:if> />
                     <div class="hint">
                       Check to select scan-based route
                     </div>
                   </div>
                   <div class="row">
                     <select name="method">
                       <option value="pcappi" <c:if test="${method == 'pcappi'}">selected</c:if> >PCAPPI</option>
                       <option value="ppi" <c:if test="${method == 'ppi'}">selected</c:if> >PPI</option>
                       <option value="cappi" <c:if test="${method == 'cappi'}">selected</c:if> >CAPPI</option>
                     </select>
                     <div class="hint">
                       Choose a method to use for generating the composite.
                     </div>
                   </div>                                        
                   <div class="row">
                     <input type="text" name="prodpar" value="${prodpar}"/>
                     <div class="hint">
                       Product parameter associated with the method. E.g. for PPI, specify elevation angle.
                     </div>
                   </div>
                   <div class="row">
                     <select name="selection_method">
                       <option value="0" <c:if test="${selection_method == 0}">selected</c:if> >Nearest radar</option>
                       <option value="1" <c:if test="${selection_method == 1}">selected</c:if> >Nearest sea level</option>
                     </select>
                     <div class="hint">
                       Choose a selection method
                     </div>
                   </div>
                   <div class="row4">
                     <select multiple size="4" name="recipients">
                       <c:forEach var="adaptor" items="${adaptors}">
                         <option value="${adaptor}" <c:if test="${ fn:contains(recipients, adaptor) }">selected</c:if> >${adaptor}</option>
                       </c:forEach>
                     </select>
                     <div class="hint">
                       Select target adaptors
                     </div>
                   </div>
                   <div class="row">
                     <input type="text" name="areaid" value="${areaid}"/>
                     <div class="hint">
                       Select area ID
                     </div>
                   </div>
                   <div class="row">
                     <select name="interval">
                       <c:forEach var="iv" items="${intervals}">
                         <option value="${iv}" <c:if test="${interval == iv}">selected</c:if> >${iv}</option>
                       </c:forEach>
                     </select>
                     <div class="hint">
                       Define interval
                     </div>
                   </div>
                   <div class="row">
                     <input type="text" name="timeout" value="${timeout}"/>
                     <div class="hint">
                       Timeout in seconds
                     </div>
                   </div>
                   <div class="row6">
                     <select multiple size="6" name="sources">
                       <c:forEach var="id" items="${sourceids}">
                         <option value="${id}" <c:if test="${ fn:contains(sources, id) }">selected</c:if> >${id}</option>
                       </c:forEach>
                     </select>
                     <div class="hint">
                       Select source radars
                     </div>
                   </div>
                   <div class="row6">
                     <select multiple size="6" name="detectors">
                       <c:forEach var="detector" items="${anomaly_detectors}">
                         <option value="${detector.name}" <c:if test="${ fn:contains(detectors, detector.name) }">selected</c:if> >${detector.name}</option>
                       </c:forEach>
                     </select>
                     <div class="hint">
                       Select quality controls to be used
                     </div>
                   </div>                                        
                 </div>
                 <div class="tablefooter">
                   <div class="buttons">
                     <button class="rounded" type="submit">
                       <span>Add</span>
                     </button>
                   </div>
                 </div>
                </form>
              </div>
            </div>      
        </div>
    </jsp:body>
</t:page_tabbed>

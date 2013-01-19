<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW

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
Creates a volume route
@date 2011-01-06
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
                Modify or delete a volume routing rule.
            </div>
            <div class="table">
              <t:error_message message="${emessage}"/>
              <div class="modifyroute">
                <form name="createRouteForm" action="volumeroute_create.htm">
                  <div class="leftcol">
                    <div class="row">Name</div>
                    <div class="row">Author</div>
                    <div class="row">Active</div>
                    <div class="row">Description</div>
                    <div class="row">Ascending</div>
                    <div class="row">Min elevation</div>
                    <div class="row">Max elevation</div>
                    <div class="row4">Recipients</div>
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
                     <input type="checkbox" name="ascending" <c:if test="${ascending == true}">checked</c:if> />
                     <div class="hint">
                       Select ascending order
                     </div>
                   </div>
                   <div class="row">
                     <input type="text" name="mine" value="${mine}"/>
                     <div class="hint">
                       Specify minimum elevation angle
                     </div>
                   </div>
                   <div class="row">
                     <input type="text" name="maxe" value="${maxe}"/>
                     <div class="hint">
                       Specify maximum elevation angle
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

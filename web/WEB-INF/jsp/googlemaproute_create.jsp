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
Creates a google map route
@date 2012-03-23
@author Anders Henja
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Create google map route" activeTab="processing">
    <jsp:body>
        <div class="left">
            <t:menu_processing/>
        </div>
        <div class="right">
            <div class="blttitle">
                Create google map route
            </div>
            <div class="blttext">
                Create a google map routing rule.
            </div>
            <div class="table">
              <t:error_message message="${emessage}"/>
              <div class="modifyroute">
                <form name="createRouteForm" action="googlemaproute_create.htm">
                  <div class="leftcol">
                    <div class="row">Name</div>
                    <div class="row">Author</div>
                    <div class="row">Active</div>
                    <div class="row">Description</div>
                    <div class="row">Area</div>
                    <div class="row">Path</div>
                    <div class="row4">Recipients</div>                    
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
                     <input type="text" name="area" value="${area}"/>
                     <div class="hint">
                       An area defining the region for which this route should be triggered.
                     </div>
                   </div>  
                   <div class="row">
                     <input type="text" name="path" value="${path}"/>
                     <div class="hint">
                       The base path (e.g. /var/www/html/data) where the generated png should be placed.
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

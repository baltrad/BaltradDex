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
Creates a distribution route
--------------------------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="${create ? 'Create' : 'Modify'} distribution route" activeTab="processing">
  <jsp:attribute name="extraHeader">
        <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/themes/base/jquery-ui.css"
              rel="stylesheet"
              type="text/css" />
        <script type="text/javascript"
                src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/jquery-ui.min.js">
        </script>
        <script type="text/javascript"
                src="http://ajax.microsoft.com/ajax/jquery.templates/beta1/jquery.tmpl.min.js">
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
                src="includes/js/jquery.populate.js">
        </script>
        <script type="text/javascript"
                src="includes/js/filter.js">
        </script>
 </jsp:attribute>
 <jsp:attribute name="extraBottom">
        <script type="text/javascript">
          $(document).ready(function() {
          <c:choose>
            <c:when test="${!empty filterJson}">
              createTopLevelFilter($("#filter1"), ${filterJson}, $("#filterJson"));
            </c:when>
            <c:otherwise>
              createTopLevelFilter($("#filter1"), null, $("#filterJson"));
            </c:otherwise>
          </c:choose>
          });
        </script>
  </jsp:attribute>
  <jsp:body>
     <div class="left">
        <t:menu_processing/>
     </div>
     <div class="right">
         <div class="blttitle">
             ${create ? 'Create' : 'Modify'} Distribution route
         </div>
         <div class="blttext">
             Rule description here.
         </div>
         <div class="table">
             <t:error_message message="${emessage}"/>
             <div class="modifyroute">
                 <t:form_route_common route="${route}"
                                      create="${create}"
                                      formAction="distributionroute.htm">
                     <jsp:attribute name="extraLeft">
                         <div class="row">Destination</div>
                         <div class="row">Filter</div>
                     </jsp:attribute>
                     <jsp:attribute name="extraRight">
                         <div class="row">
                             <input type="text" name="destination" value="${destination}"/>
                             <div class="hint">
                                 url of the destination to send files to
                             </div>
                         </div>
                         <div class="row">
                             <div id="filter1"></div>
                             <input type="hidden" name="filterJson" id="filterJson" />
                             <div class="hint">
                               filter to match incoming files against ${!empty filterJson}
                             </div>
                         </div>
                     </jsp:attribute>
                 </t:form_route_common>
             </div>
         </div>
     </div>
  </jsp:body>
</t:page_tabbed>

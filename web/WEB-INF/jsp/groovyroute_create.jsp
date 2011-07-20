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
Creates a groovy route
@date 2010-03-25
@author Anders Henja
--------------------------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Create Groovy route" activeTab="processing">
    <div class="left">
        <t:menu_processing/>
    </div>
    <div class="right">
        <div class="blttitle">
            Create Groovy route
        </div>
        <div class="blttext">
            Create a Groovy scripted routing rule.
        </div>
        <div class="table">
            <t:error_message message="${emessage}"/>
            <div class="modifyroute">
                <t:form_route_common adaptors="${adaptors}"
                                     route="${route}"
                                     create="true"
                                     formAction="groovyroute_create.htm">
                    <jsp:attribute name="extraLeft">
                        <div class="row">Script</div>
                    </jsp:attribute>
                    <jsp:attribute name="extraRight">
                        <div class="textrow">
                            <textarea class="routedefinition" name="typdef">${typdef}</textarea>
                            <div class="hint">
                               Groovy route definition
                            </div>
                        </div>
                    </jsp:attribute>
                </t:form_route_common>
            </div>
        </div>
    </div>
</t:page_tabbed>

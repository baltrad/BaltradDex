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
Creates a groovy route
@date 2010-03-25
@author Anders Henja
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:generic_page pageTitle="Create adaptor">
    <jsp:body>
        <div class="routes">
            <div class="table">
                <div class="header">
                    <div class="row">Create route</div>
                </div>
                <div class="header-text">
                     Create a Groovy scripted routing rule.
                </div>
                <t:message_box errorHeader="Problems encountered."
                               errorBody="${emessage}"/>
                <t:form_route_common adaptors="${adaptors}"
                                 route="${route}"
                                 create="true"
                                 formAction="route_create_groovy.htm">
                    <jsp:attribute name="extraBottom">
                        <div class="row2">
                            <div class="leftcol">Script:</div>
                            <div class="rightcol">
                                <textarea class="routedefinition" name="typdef"
                                          title="Groovy route definition">${typdef}
                                </textarea>
                            </div>    
                        </div>
                    </jsp:attribute>
                </t:form_route_common>
            </div>
        </div>
    </jsp:body>
</t:generic_page>


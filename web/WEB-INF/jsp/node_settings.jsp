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
Document   : Node settings
Created on : May 20, 2013, 3:18 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Node settings">
    <jsp:body>
        <div class="node-settings">
            <div class="table">
                <div class="header">
                    <div class="row">Local node settings</div>
                </div>
                <div class="header-text">
                    Edit node settings and click <i>Save</i> button in order 
                    to save changes. 
                </div>
                <form:form method="POST" commandName="config">
                    <t:message_box msgHeader="Success."
                                   msgBody="${message}"
                                   errorHeader="Problems encountered."
                                   errorBody="${error}"/>
                    <div class="body">
                        <div class="row">
                            <div class="leftcol">
                                Node name:
                            </div>
                            <div class="rightcol">
                                <form:input path="nodeName" 
                                            title="Unique node name"/>
                                <form:errors path="nodeName" cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Node address:
                            </div>
                            <div class="rightcol">
                                <form:input path="nodeAddress"
                                    title="Node address, e.g. http://baltrad.eu:8084" />
                                <form:errors path="nodeAddress"
                                    cssClass="error" />
                                                                
                                <span class="error"><c:out value="${address_warning}" /></span>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Node type:
                            </div>
                            <div class="rightcol">
                                <form:select path="nodeType" 
                                             title="Select node type">
                                    <form:options items="${node_types}"/>
                                </form:select>
                                <form:errors path="nodeType" cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Organization name:
                            </div>
                            <div class="rightcol">
                                <form:input path="orgName"
                                            title="Name of organization"/>
                                <form:errors path="orgName" cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Unit name:
                            </div>
                            <div class="rightcol">
                                <form:input path="orgUnit"
                                            title="Unit name, e.g. Forecast Department"/>
                                <form:errors path="orgUnit" cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Address:
                            </div>
                            <div class="rightcol">
                                <form:input path="locality" title="Address"/>
                                <form:errors path="locality" cssClass="error"/>
                            </div>
                        </div>        
                        <div class="row">
                            <div class="leftcol">
                                Country:
                            </div>
                            <div class="rightcol">
                                <form:input path="state" title="Country name"/>
                                <form:errors path="state" cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Country code:
                            </div>
                            <div class="rightcol">
                                <form:input path="countryCode" 
                                            title="2-letter country code"/>
                                <form:errors path="countryCode" cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Working directory:
                            </div>
                            <div class="rightcol">
                                <form:input path="workDir" 
                                            title="Working directory path, 
                                            storage for temporary files and images"/>
                                <form:errors path="workDir" cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Administrator's e-mail:
                            </div>
                            <div class="rightcol">
                                <form:input path="adminEmail" 
                                            title="Node administrator's email
                                            address"/>
                                <form:errors path="adminEmail" cssClass="error"/>
                            </div>
                        </div>       
                    </div>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="submit" 
                                       value="Save"/>
                            </div>
                        </div>
                    </div>             
                </form:form>
            </div>
        </div>
    </jsp:body>
</t:generic_page>


<%------------------------------------------------------------------------------
Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW

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
Document   : Local node settings page
Created on : Jun 6, 2011, 10:02 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Node settings" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                <img src="includes/images/icons/settings.png" alt="">
                Local node settings
            </div>
            <div class="blttext">
                Local node configuration. Click save button to store modified
                settings.
            </div>
            <div class="table">
                <div class="props">
                    <form:form method="POST" commandName="config">
                        <%@include file="/WEB-INF/jsp/form_messages.jsp" %>
                        <div class="leftcol">
                            <div class="row">Node name</div>
                            <div class="row">Node type</div>
                            <div class="row">Node address</div>
                            <div class="row">Organization name</div>
                            <div class="row">Unit name</div>
                            <div class="row">Locality name (City)</div>
                            <div class="row">State name (Country)</div>
                            <div class="row">Country code</div>
                            <div class="row">Local time zone</div>
                            <div class="row">Work directory</div>
                            <div class="row">Administrator's email</div>
                        </div>
                        <div class="rightcol">
                            <div class="row">
                                <div class="nodename">
                                    <form:input path="nodeName" 
                                        title="Enter node name"/>
                                    <div class="hint">
                                        Unique node identifier
                                    </div>
                                </div>
                                <form:errors path="nodeName" cssClass="error"/>
                            </div>
                            <div class="row">
                                <div class="nodetype">
                                    <form:select path="nodeType" 
                                                 title="Select node type">
                                        <form:options items="${node_types}"/>
                                    </form:select>    
                                    <div class="hint">
                                        Primary or backup node
                                    </div>
                                </div>
                                <form:errors path="nodeType" cssClass="error"/>
                            </div>
                            <div class="row">
                                <div class="fulladdress">
                                    <form:input path="nodeAddress"
                                        title="Enter fully qualified node address"/>
                                    <div class="hint">
                                        Node address, e.g. http://baltrad.eu:8084
                                    </div>
                                </div>
                                <form:errors path="nodeAddress" cssClass="error"/>
                            </div>        
                            <div class="row">
                                <div class="orgname">
                                    <form:input path="orgName"
                                                title="Enter organization name"/>
                                    <div class="hint">
                                        Name of organization
                                    </div>
                                </div>
                                <form:errors path="orgName" cssClass="error"/>
                            </div>
                            <div class="row">
                                <div class="orgname">
                                    <form:input path="orgUnit"
                                                title="Enter unit name"/>
                                    <div class="hint">
                                        Unit name, e.g. Forecast Department
                                    </div>
                                </div>
                                <form:errors path="orgUnit" cssClass="error"/>
                            </div>
                            <div class="row">
                                <div class="city">
                                    <form:input path="locality"
                                                title="Enter address"/>
                                    <div class="hint">
                                        Address
                                    </div>
                                </div>
                                <form:errors path="locality" cssClass="error"/>
                            </div>
                            <div class="row">
                                <div class="country">
                                    <form:input path="state"
                                                title="State name"/>
                                    <div class="hint">
                                        State name (Country)
                                    </div>
                                </div>
                                <form:errors path="state" cssClass="error"/>
                            </div>
                            <div class="row">
                                <div class="zipcode">
                                    <form:input path="countryCode"
                                                title="Enter two-letter country code"/>
                                    <div class="hint">
                                        Two-letter country code
                                    </div>
                                </div>
                                <form:errors path="countryCode" cssClass="error"/>
                            </div>                 
                            <div class="row">
                                <div class="timezone">
                                    <form:select path="timeZone" 
                                                 title="Select time zone">
                                        <form:options items="${time_zones}"/>
                                    </form:select> 
                                    <div class="hint">
                                        UTC time zone 
                                    </div>
                                </div>
                                <form:errors path="timeZone" cssClass="error"/>
                            </div>
                            <div class="row">
                                <div class="workdir">
                                    <form:input path="workDir"
                                        title="Enter work directory path"/>
                                    <div class="hint">
                                        Storage for temporary files & images 
                                    </div>
                                </div>
                                <form:errors path="workDir" cssClass="error"/>
                            </div>
                            <div class="row">
                                <div class="adminmail">
                                    <form:input path="email"
                                        title="Enter administrator's e-mail"/>
                                    <div class="hint">
                                        Node administrator's e-mail
                                    </div>
                                </div>
                                <form:errors path="email" cssClass="error"/>
                            </div>
                        </div>
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="submit">
                                    <span>Save</span>
                                </button>
                            </div>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </jsp:body>
</t:page_tabbed>

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
Document   : Delivery registry settings
Created on : May 27, 2013, 8:57 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Registry settings">
    <jsp:body>
        <div class="registry-settings">
            <div class="table">
                <div class="header">
                    <div class="row">Registry settings</div>
                </div>
                <div class="header-text">
                    Delivery registry settings allow to keep number of entries
                    under control. Click <i>Save</i> to store settings. 
                </div>
                <form:form method="POST" commandName="config">
                    <t:message_box msgHeader="Success."
                               msgBody="${registry_conf_success}"
                               errorHeader="Problems encountered."
                               errorBody="${registry_conf_error}"/> 
                    <div class="body">
                        <div class="section">Maximum number of entries</div>
                        <div class="row">
                            <div class="leftcol">Max number:</div>
                            <div class="rightcol">
                                <form:input path="regRecordLimit"
                                            title="Maximum number of registry entries"/>    
                                <form:errors path="regRecordLimit" 
                                             cssClass="error"/>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="leftcol">Set active:</div>
                            <div class="rightcol">
                                <form:checkbox path="regTrimByNumber" 
                                               title="Activate / turn off" 
                                               value="true"/>
                            </div>
                        </div>
                        <div class="section" id="maxage">
                            Maximum age of entries
                        </div>
                        <div class="row">
                            <div class="leftcol">Days:</div>
                            <div class="rightcol">
                                <form:input path="regMaxAgeDays" 
                                            title="Set number of days"/>
                                <form:errors path="regMaxAgeDays" 
                                             cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">Hours:</div>
                            <div class="rightcol">
                                <form:input path="regMaxAgeHours"
                                            title="Set number of hours"/>
                                <form:errors path="regMaxAgeHours" 
                                             cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">Minutes:</div>
                            <div class="rightcol">
                                <form:input path="regMaxAgeMinutes"
                                            title="Set number of minutes"/>    
                                <form:errors path="regMaxAgeMinutes" 
                                             cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">Set active:</div>
                            <div class="rightcol">
                                <form:checkbox path="regTrimByAge"
                                               title="Activate / turn off"
                                               value="true"/>
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

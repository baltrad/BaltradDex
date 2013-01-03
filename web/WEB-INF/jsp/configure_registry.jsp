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
Document   : System messages settings page
Created on : Aug 25, 2011, 12:12 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Configure registry" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Configure registry
            </div>
            <div class="blttext">
                Use the following options to control number of entries in data delivery registry.
            </div>
            <div class="logconf">
                <div class="table">
                    <form:form method="POST" commandName="config">
                        <%@include file="/WEB-INF/jsp/form_messages.jsp"%>
                        <div class="blttext">
                            Set maximum number of registry entries
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                <div class="inner-leftcol">
                                    Limit
                                </div>
                                <div class="inner-rightcol">
                                    <form:input path="recordLimit"
                                                title="Maximum number of registry entries"/>
                                    <div class="hint">
                                        Max number of registry entries
                                    </div>
                                    <form:errors path="recordLimit" 
                                                 cssClass="error"/>
                                </div>
                            </div>
                            <div class="rightcol">
                                <div class="col">
                                    Set active
                                </div>
                                <div class="col">
                                    <form:checkbox path="trimByNumber"
                                                   title="Set active/inactive"/>
                                </div>
                            </div>
                        </div>
                        <div class="bltseparator"></div>
                        <div class="blttext">
                            Set maximum age of registry entries
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                <div class="inner-leftcol">
                                    Days
                                </div>
                                <div class="inner-rightcol">
                                    <form:input path="maxAgeDays" 
                                                title="Set number of days"/>
                                    <div class="hint">
                                        Number of days
                                    </div>
                                    <form:errors path="maxAgeDays" 
                                                 cssClass="error"/>
                                </div>
                            </div>
                            <div class="rightcol">
                                <div class="col">
                                    Set active
                                </div>
                                <div class="col">
                                    <form:checkbox path="trimByAge"
                                                   title="Set active/inactive"/>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                <div class="inner-leftcol">
                                    Hours
                                </div>
                                <div class="inner-rightcol">
                                    <form:input path="maxAgeHours"
                                                title="Set number of hours"/>
                                    <div class="hint">
                                        Number of hours
                                    </div>
                                    <form:errors path="maxAgeHours" 
                                                 cssClass="error"/>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                <div class="inner-leftcol">
                                    Minutes
                                </div>
                                <div class="inner-rightcol">
                                    <form:input path="maxAgeMinutes"
                                                title="Set number of minutes"/>
                                    <div class="hint">
                                        Number of minutes
                                    </div>
                                    <form:errors path="maxAgeMinutes" 
                                                 cssClass="error"/>
                                </div>
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
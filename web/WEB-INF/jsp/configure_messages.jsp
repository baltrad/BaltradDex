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
Document   : System messages settings page
Created on : Aug 22, 2011, 3:09 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Configure messages" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Configure messages
            </div>
            <div class="blttext">
                Use the following options to control number of entries in the system log.
            </div>
            <div class="logconf">
                <div class="table">
                    <form method="post">
                        <%@include file="/WEB-INF/jsp/form_messages.jsp"%>
                        <div class="blttext">
                            Set maximum number of log entries
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                <div class="inner-leftcol">
                                    Limit
                                </div>
                                <div class="inner-rightcol">
                                    <form:input path="command.recordLimit"
                                                title="Maximum number of log entries"/>
                                    <div class="hint">
                                        Max number of log entries
                                    </div>
                                    <form:errors path="command.recordLimit" cssClass="error"/>
                                </div>
                            </div>
                            <div class="rightcol">
                                <div class="col">
                                    Set active
                                </div>
                                <div class="col">
                                    <form:checkbox path="command.trimByNumber"
                                                   title="Set active/inactive"/>
                                </div>
                            </div>
                        </div>
                        <div class="bltseparator"></div>
                        <div class="blttext">
                            Set maximum age of log entries
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                <div class="inner-leftcol">
                                    Days
                                </div>
                                <div class="inner-rightcol">
                                    <form:input path="command.maxAgeDays" title="Set number of days"/>
                                    <div class="hint">
                                        Number of days
                                    </div>
                                    <form:errors path="command.maxAgeDays" cssClass="error"/>
                                </div>
                            </div>
                            <div class="rightcol">
                                <div class="col">
                                    Set active
                                </div>
                                <div class="col">
                                    <form:checkbox path="command.trimByAge"
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
                                    <form:input path="command.maxAgeHours"
                                                title="Set number of hours"/>
                                    <div class="hint">
                                        Number of hours
                                    </div>
                                    <form:errors path="command.maxAgeHours" cssClass="error"/>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                <div class="inner-leftcol">
                                    Minutes
                                </div>
                                <div class="inner-rightcol">
                                    <form:input path="command.maxAgeMinutes"
                                                title="Set number of minutes"/>
                                    <div class="hint">
                                        Number of minutes
                                    </div>
                                    <form:errors path="command.maxAgeMinutes" cssClass="error"/>
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
                    </form>
                </div>
            </div>
        </div>
    </jsp:body>
</t:page_tabbed>
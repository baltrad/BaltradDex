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
Document   : System log settings
Created on : May 24, 2013, 11:07 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Messages settings">
    <jsp:body>
        <div class="messages-settings">
            <div class="table">
                <div class="header">
                    <div class="row">Messages settings</div>
                </div>
                <div class="header-text">
                    Messages settings allow to keep the size of the system log
                    under control. Click <i>Save</i> to store settings. 
                </div>
                <form:form method="POST" commandName="config">
                    <t:message_box msgHeader="Success."
                               msgBody="${messages_conf_success}"
                               errorHeader="Problems encountered."
                               errorBody="${messages_conf_error}"/> 
                    <div class="body">
                        <div class="section">Maximum number of messages</div>
                        <div class="row">
                            <div class="leftcol">Max number:</div>
                            <div class="rightcol">
                                <form:input path="msgRecordLimit"
                                            title="Maximum number of log entries"/>    
                                <form:errors path="msgRecordLimit" 
                                             cssClass="error"/>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="leftcol">Set active:</div>
                            <div class="rightcol">
                                <form:checkbox path="msgTrimByNumber" 
                                               title="Activate / turn off" 
                                               value="true"/>
                            </div>
                        </div>
                        <div class="section" id="maxage">Maximum age of messages</div>
                        <div class="row">
                            <div class="leftcol">Days:</div>
                            <div class="rightcol">
                                <form:input path="msgMaxAgeDays" 
                                            title="Set number of days"/>
                                <form:errors path="msgMaxAgeDays" 
                                             cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">Hours:</div>
                            <div class="rightcol">
                                <form:input path="msgMaxAgeHours"
                                            title="Set number of hours"/>
                                <form:errors path="msgMaxAgeHours" 
                                             cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">Minutes:</div>
                            <div class="rightcol">
                                <form:input path="msgMaxAgeMinutes"
                                            title="Set number of minutes"/>    
                                <form:errors path="msgMaxAgeMinutes" 
                                             cssClass="error"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">Set active:</div>
                            <div class="rightcol">
                                <form:checkbox path="msgTrimByAge"
                                               title="Activate / turn off"
                                               value="true"/>
                            </div>
                        </div>
                        <div class="section" id="messagebrowsersettings">Browse message settings</div>
                        <div class="row">
                            <div class="leftcol">Scroll range:</div>
                            <div class="rightcol">
<!--
                                <form:input path="messageBrowserScrollRange" 
                                            title="The scroll range, must be an odd number &ge; 3"/>
-->
                                <form:select path="messageBrowserScrollRange" 
                                             title="Select browser scroll range">
                                    <form:options items="${scroll_ranges}"/>
                                </form:select>

                                <form:errors path="messageBrowserScrollRange" 
                                             cssClass="error"/>
                                             
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

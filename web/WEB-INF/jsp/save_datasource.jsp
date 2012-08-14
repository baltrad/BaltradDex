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
Document   : Save data source name and description page
Created on : Apr 22, 2011, 9:04 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Configure data source" activeTab="settings">
    <jsp:body>
        <body onload="selectAll(['all_radars_hid', 'selected_radars_hid',
              'all_file_objects_hid', 'selected_file_objects_hid',
              'all_users_hid','selected_users_hid'])"> 
            <div class="left">
                <t:menu_settings/>
            </div>
            <div class="right">
                <div class="blttitle">
                    Save data source
                </div>
                <div class="blttext">
                    Configure and save data source. Data source provides access to 
                    radar data files based on selected parameters.   
                </div>
                <div class="table">
                    <form:form method="POST" commandName="data_source">
                        <div class="dssave">
                            <div class="dsparam">
                                <div class="row">
                                    <div class="dsid">
                                        Data source name
                                    </div>
                                    <div class="input">
                                        <form:input path="name"  
                                                    title="Enter data source name"/>                     
                                        <div class="hint">
                                            Unique and meaningful name.
                                        </div>
                                    </div>
                                    <form:errors path="name" cssClass="error"/>    
                                </div>
                                <div class="row">
                                    <div class="dsid">
                                        Description
                                    </div>
                                    <div class="input">
                                        <div class="dsdescription">
                                            <form:textarea path="description" 
                                                           title="Enter detailed description"/>
                                            <div class="hint">
                                                Detailed description
                                            </div>
                                        </div>
                                    </div>
                                    <form:errors path="description" cssClass="error"/>        
                                </div>
                            </div>
                            <div class="separator"></div>
                            <div class="dsparam">
                                <div class="row">
                                    <div class="hdr">
                                        Select radar station
                                        <div class="hint">
                                            At least one radar station is required
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Available radar stations
                                        <select name="all_radars" size="6" 
                                                multiple>
                                            <c:forEach items="${all_radars}" 
                                                       var="radar">
                                                <option value="${radar.radarName}">    
                                                    <c:out value="${radar.wmoNumber}:
                                                        ${radar.radarName}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <div class="hidden">            
                                            <select name="all_radars_hid" size="6" 
                                                    multiple>
                                                <c:forEach items="${all_radars}" 
                                                           var="radar">
                                                    <option value="${radar.radarName}">    
                                                        <c:out value="${radar.wmoNumber}:
                                                            ${radar.radarName}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>        
                                    </div>
                                    <div class="midcol">
                                        <div class="select">
                                            <input type="button" value=">" 
                                                title="Add radar station" 
                                                onclick="copyOption(
                                                    'all_radars',
                                                    'selected_radars', 
                                                    'all_radars_hid',
                                                    'selected_radars_hid')"/>
                                            <input type="button" value="<" 
                                                title="Remove radar station"
                                                onclick="copyOption(
                                                    'selected_radars', 
                                                    'all_radars', 
                                                    'selected_radars_hid',
                                                    'all_radars_hid')"/>
                                        </div>
                                    </div>
                                    <div class="rightcol">
                                        Selected radar stations
                                        <select name="selected_radars" size="6" 
                                                multiple>
                                            <c:if test="${not empty missing_radar_error}">
                                                <option class="error" id="error" >
                                                    <c:out value="${missing_radar_error}"/>
                                                </option>
                                                <c:set var="missing_radar_error" 
                                                       value="" 
                                                       scope="session"/>
                                            </c:if> 
                                            <c:forEach items="${selected_radars}" var="radar">
                                                <option value="${radar.radarName}">    
                                                    <c:out value="${radar.wmoNumber}:
                                                        ${radar.radarName}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                                
                                        <div class="hidden">            
                                            <select name="selected_radars_hid" size="6" 
                                                    multiple>
                                                <c:forEach items="${selected_radars}" var="radar">
                                                    <option value="${radar.radarName}">    
                                                        <c:out value="${radar.wmoNumber}:
                                                            ${radar.radarName}"/>
                                                    </option>
                                                </c:forEach>    
                                            </select>                  
                                        </div>            
                                    </div>
                                </div>                                                    
                                <div class="row">
                                    <div class="hdr">
                                        Select file object
                                        <div class="hint">
                                            File object type according to ODIM_H5 specification
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Available file objects
                                        <select name="all_file_objects" size="6" 
                                                multiple>
                                            <c:forEach items="${all_file_objects}" var="fobject">
                                                <option value="${fobject.fileObject}" 
                                                        title="${fobject.description}">
                                                    <c:out value="${fobject.fileObject}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <div class="hidden">           
                                            <select name="all_file_objects_hid" 
                                                    size="6" multiple>
                                                <c:forEach items="${all_file_objects}" var="fobject">
                                                    <option value="${fobject.fileObject}" 
                                                            title="${fobject.description}">
                                                        <c:out value="${fobject.fileObject}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>        
                                        </div>
                                    </div>
                                    <div class="midcol">
                                        <div class="select">
                                            <input type="button" value=">" 
                                                title="Add file object"
                                                onclick="copyOption(
                                                    'all_file_objects', 
                                                    'selected_file_objects',
                                                    'all_file_objects_hid',
                                                    'selected_file_objects_hid')"/>
                                            <input type="button" value="<" 
                                                title="Remove file object"
                                                onclick="copyOption(
                                                    'selected_file_objects', 
                                                    'all_file_objects',
                                                    'selected_file_objects_hid',
                                                    'all_file_objects_hid')"/>
                                        </div>
                                    </div>
                                    <div class="rightcol">
                                        Selected file objects
                                        <select name="selected_file_objects" 
                                                size="6" multiple>
                                            <c:forEach items="${selected_file_objects}" 
                                                       var="fobject">
                                                <option value="${fobject.fileObject}" 
                                                        title="${fobject.description}">
                                                    <c:out value="${fobject.fileObject}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <div class="hidden">           
                                            <select name="selected_file_objects_hid" 
                                                    size="6" multiple>
                                                <c:forEach items="${selected_file_objects}" 
                                                        var="fobject">
                                                    <option value="${fobject.fileObject}" 
                                                            title="${fobject.description}">
                                                        <c:out value="${fobject.fileObject}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>        
                                        </div>        
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="hdr">
                                        Select users
                                        <div class="hint">
                                            User allowed to subscribe this data source
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Available users
                                        <select name="all_users" size="6" multiple>
                                            <c:forEach items="${all_users}" var="user">
                                                <option value="${user.name}">    
                                                    <c:out value="${user.name}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <div class="hidden">           
                                            <select name="all_users_hid" 
                                                    size="6" multiple>
                                                <c:forEach items="${all_users}" 
                                                           var="user">
                                                    <option value="${user.name}">    
                                                        <c:out value="${user.name}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>        
                                        </div>        
                                    </div>
                                    <div class="midcol">
                                        <div class="select">
                                            <input type="button" value=">" 
                                                title="Add user"
                                                onclick="copyOption(
                                                    'all_users',
                                                    'selected_users',
                                                    'all_users_hid',
                                                    'selected_users_hid')"/>
                                            <input type="button" value="<" 
                                                title="Remove user"
                                                onclick="copyOption(
                                                    'selected_users',
                                                    'all_users',
                                                    'selected_users_hid', 
                                                    'all_users_hid')"/>
                                        </div>
                                    </div>
                                    <div class="rightcol">
                                        Selected users
                                        <select name="selected_users" size="6" 
                                                multiple>
                                            <c:forEach items="${selected_users}" 
                                                       var="user">
                                                <option value="${user.name}">    
                                                    <c:out value="${user.name}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <div class="hidden">           
                                            <select name="selected_users_hid" 
                                                    size="6" multiple>
                                                <c:forEach items="${selected_users}" 
                                                           var="user">
                                                    <option value="${user.name}">    
                                                        <c:out value="${user.name}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>        
                                        </div>           
                                    </div>
                                </div>
                            </div>  
                        </div>
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                    onclick="window.location.href='settings.htm'">
                                    <span>Cancel</span>
                                </button>
                                <button class="rounded" type="submit">
                                    <span>Save</span>
                                </button>
                            </div>
                        </div>
                    </form:form>                    
                </div>                  
            </div>
        </body>    
    </jsp:body>    
</t:page_tabbed>

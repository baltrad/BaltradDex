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
Document   : Save data source page
Created on : May 22, 2013, 10:06 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp"%>

<t:generic_page pageTitle="Save data source">
    <jsp:body>
        <body onload="selectAll(['all_radars_hid', 'selected_radars_hid',
              'all_file_objects_hid', 'selected_file_objects_hid',
              'all_users_hid','selected_users_hid'])">
            <div class="datasources-save">
                <div class="table">
                    <div class="header">
                        <div class="row">
                            Save data source
                        </div>
                    </div>
                    <div class="header-text">
                        Configure data source parameters. Click <i>Save</i>
                        to store data source.
                    </div>
                    <form:form method="POST" commandName="data_source">
                        <div class="body">
                            <div class="row2">
                                <div class="datasource-basic">
                                    <div class="leftcol">
                                        Data source name:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="name"  
                                                    title="Unique and meaningful data source name"/>                     
                                        <form:errors path="name" cssClass="error"/>   
                                    </div>
                                </div>    
                            </div>
                            <div class="row2">
                                <div class="datasource-basic">
                                    <div class="leftcol">
                                        Description:
                                    </div>
                                    <div class="rightcol">
                                        <form:textarea path="description" 
                                                       title="Brief data source description"/>
                                        <form:errors path="description" 
                                                     cssClass="error"/>  
                                    </div>
                                </div>    
                            </div>                                
                            <div class="row2">
                                <div class="datasource-param">
                                    <div class="section" 
                                         title="At least one radar is required">
                                        Select radar station
                                    </div>
                                    <div class="leftcol">
                                        <div class="label">
                                            Available radar stations
                                        </div>
                                        <select name="all_radars" size="6" 
                                                multiple>
                                            <c:forEach items="${all_radars}" 
                                                       var="radar">
                                                <option value="${radar.radarPlace}">    
                                                    <c:out value="${radar.radarPlace} 
                                                               / ${radar.radarCode}
                                                               / ${radar.radarWmo}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <div class="hidden">            
                                            <select name="all_radars_hid" size="6" 
                                                    multiple>
                                                <c:forEach items="${all_radars}" 
                                                           var="radar">
                                                    <option value="${radar.radarPlace}">    
                                                        <c:out value="${radar.radarPlace} 
                                                               / ${radar.radarCode}
                                                               / ${radar.radarWmo}" />
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div> 
                                    </div>
                                    <div class="midcol">
                                        <div class="select">
                                            <div class="button-wrap">
                                                <input class="button" 
                                                       type="button" 
                                                       value=">"
                                                       title="Add radar station"
                                                       onclick="copyOption(
                                                            'all_radars',
                                                            'selected_radars', 
                                                            'all_radars_hid',
                                                            'selected_radars_hid')"/>
                                            </div>
                                            <div class="button-wrap">
                                                <input class="button" 
                                                       type="button" 
                                                       value="<" 
                                                       title="Remove radar station"
                                                       onclick="copyOption(
                                                            'selected_radars', 
                                                            'all_radars', 
                                                            'selected_radars_hid',
                                                            'all_radars_hid')"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="rightcol">
                                        <div class="label">
                                            Selected radar stations
                                        </div>
                                        <select name="selected_radars" size="6" 
                                                multiple>
                                            <c:if test="${not empty missing_radar_error}">
                                                <option class="error" id="error" >
                                                    <c:out value="${missing_radar_error}"/>
                                                </option>
                                                <c:set var="missing_radar_error" 
                                                       value="" scope="session"/>
                                            </c:if> 
                                            <c:forEach items="${selected_radars}" var="radar">
                                                <option value="${radar.radarPlace}">    
                                                    <c:out value="${radar.radarPlace} 
                                                            / ${radar.radarCode}
                                                            / ${radar.radarWmo}" />
                                                </option>
                                            </c:forEach>
                                        </select>  
                                        <div class="hidden">            
                                            <select name="selected_radars_hid" size="6" 
                                                    multiple>
                                                <c:forEach items="${selected_radars}" var="radar">
                                                    <option value="${radar.radarPlace}">    
                                                        <c:out value="${radar.radarPlace} 
                                                               / ${radar.radarCode}
                                                               / ${radar.radarWmo}" />
                                                    </option>
                                                </c:forEach>    
                                            </select>                  
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row2">
                                <div class="datasource-param">
                                    <div class="section"
                                         title="File object type according to ODIM_H5 information model">
                                        Select file objects
                                    </div>
                                    <div class="leftcol">
                                        <div class="label">
                                            Available file objects
                                        </div>
                                        <select name="all_file_objects" size="6" 
                                                multiple>
                                            <c:forEach items="${all_file_objects}" var="fobject">
                                                <option value="${fobject.name}" 
                                                        title="${fobject.description}">
                                                    <c:out value="${fobject.name}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <div class="hidden">           
                                            <select name="all_file_objects_hid" 
                                                    size="6" multiple>
                                                <c:forEach items="${all_file_objects}" var="fobject">
                                                    <option value="${fobject.name}" 
                                                            title="${fobject.description}">
                                                        <c:out value="${fobject.name}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>        
                                        </div>

                                    </div>
                                    <div class="midcol">
                                        <div class="select">
                                            <div class="button-wrap">
                                                <input class="button" 
                                                       type="button" 
                                                       value=">"
                                                       title="Add file object"
                                                       onclick="copyOption(
                                                            'all_file_objects', 
                                                            'selected_file_objects',
                                                            'all_file_objects_hid',
                                                            'selected_file_objects_hid')"/>
                                            </div>
                                            <div class="button-wrap">
                                                <input class="button" 
                                                       type="button" 
                                                       value="<" 
                                                       title="Remove file object"
                                                       onclick="copyOption(
                                                            'selected_file_objects', 
                                                            'all_file_objects',
                                                            'selected_file_objects_hid',
                                                            'all_file_objects_hid')"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="rightcol">
                                        <div class="label">
                                            Selected file objects
                                        </div>
                                        <select name="selected_file_objects" 
                                                size="6" multiple>
                                            <c:forEach items="${selected_file_objects}" 
                                                       var="fobject">
                                                <option value="${fobject.name}" 
                                                        title="${fobject.description}">
                                                    <c:out value="${fobject.name}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <div class="hidden">           
                                            <select name="selected_file_objects_hid" 
                                                    size="6" multiple>
                                                <c:forEach items="${selected_file_objects}" 
                                                        var="fobject">
                                                    <option value="${fobject.name}" 
                                                            title="${fobject.description}">
                                                        <c:out value="${fobject.name}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>        
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row2">
                                <div class="datasource-param">
                                    <div class="section"
                                         title="Users allowed to subscribe this data source">
                                        Select users
                                    </div>
                                    <div class="leftcol">
                                        <div class="label">
                                            Available users
                                        </div>
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
                                            <div class="button-wrap">
                                                <input class="button" 
                                                       type="button" 
                                                       value=">"
                                                       title="Add user"
                                                       onclick="copyOption(
                                                            'all_users',
                                                            'selected_users',
                                                            'all_users_hid',
                                                            'selected_users_hid')"/>
                                            </div>
                                            <div class="button-wrap">
                                                <input class="button" 
                                                       type="button" 
                                                       value="<" 
                                                       title="Remove user"
                                                       onclick="copyOption(
                                                            'selected_users',
                                                            'all_users',
                                                            'selected_users_hid', 
                                                            'all_users_hid')"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="rightcol">
                                        <div class="label">
                                            Selected users
                                        </div>
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
        </body>    
    </jsp:body>
</t:generic_page>


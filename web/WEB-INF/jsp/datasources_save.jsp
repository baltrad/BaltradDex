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
                                    <select name="radars_available" size="6" 
                                            multiple>
                                        <c:forEach items="${radars_available}" 
                                                   var="radar">
                                            <option value="${radar.value.id}">    
                                                <c:out value="${radar.value.radarPlace} 
                                                           / ${radar.value.radarCode}
                                                           / ${radar.value.radarWmo}"/>
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="midcol">
                                    <div class="select">
                                        <div class="button-wrap">
                                            <input class="button"
                                                   name="add_radar"
                                                   type="submit" 
                                                   value=">"
                                                   title="Add radar station"/>
                                        </div>
                                        <div class="button-wrap">
                                            <input class="button"
                                                   name="remove_radar"
                                                   type="submit" 
                                                   value="<" 
                                                   title="Remove radar station"/>
                                        </div>
                                    </div>
                                </div>
                                <div class="rightcol">
                                    <div class="label">
                                        Selected radar stations
                                    </div>
                                    <select name="radars_selected" size="6" 
                                            multiple>
                                        <c:if test="${not empty missing_radar_error}">
                                            <option class="error" id="error" >
                                                <c:out value="${missing_radar_error}"/>
                                            </option>
                                            <c:set var="missing_radar_error" 
                                                   value="" scope="session"/>
                                        </c:if> 
                                        <c:forEach items="${radars_selected}" var="radar">
                                            <option value="${radar.value.id}">    
                                                <c:out value="${radar.value.radarPlace} 
                                                        / ${radar.value.radarCode}
                                                        / ${radar.value.radarWmo}" />
                                            </option>
                                        </c:forEach>
                                    </select>  
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
                                    <select name="file_objects_available" size="6" 
                                            multiple>
                                        <c:forEach items="${file_objects_available}" var="fobject">
                                            <option value="${fobject.value.id}" 
                                                    title="${fobject.value.description}">
                                                <c:out value="${fobject.value.name}"/>
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="midcol">
                                    <div class="select">
                                        <div class="button-wrap">
                                            <input class="button" 
                                                   name="add_file_object"
                                                   type="submit" 
                                                   value=">"
                                                   title="Add file object"/>
                                        </div>
                                        <div class="button-wrap">
                                            <input class="button"
                                                   name="remove_file_object"
                                                   type="submit" 
                                                   value="<" 
                                                   title="Remove file object"
                                                   />
                                        </div>
                                    </div>
                                </div>
                                <div class="rightcol">
                                    <div class="label">
                                        Selected file objects
                                    </div>
                                    <select name="file_objects_selected" 
                                            size="6" multiple>
                                        <c:forEach items="${file_objects_selected}" 
                                                   var="fobject">
                                            <option value="${fobject.value.id}" 
                                                    title="${fobject.value.description}">
                                                <c:out value="${fobject.value.name}"/>
                                            </option>
                                        </c:forEach>
                                    </select>
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
                                    <select name="users_available" size="6" multiple>
                                        <c:forEach items="${users_available}" var="user">
                                            <option value="${user.value.id}">    
                                                <c:out value="${user.value.name}"/>
                                            </option>
                                        </c:forEach>
                                    </select>

                                </div>
                                <div class="midcol">
                                    <div class="select">
                                        <div class="button-wrap">
                                            <input class="button"
                                                   name="add_user"
                                                   type="submit" 
                                                   value=">"
                                                   title="Add user"/>
                                        </div>
                                        <div class="button-wrap">
                                            <input class="button"
                                                   name="remove_user"
                                                   type="submit" 
                                                   value="<" 
                                                   title="Remove user"/>
                                        </div>
                                    </div>
                                </div>
                                <div class="rightcol">
                                    <div class="label">
                                        Selected users
                                    </div>
                                    <select name="users_selected" size="6" 
                                            multiple>
                                        <c:forEach items="${users_selected}" 
                                                   var="user">
                                            <option value="${user.value.id}">    
                                                <c:out value="${user.value.name}"/>
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="table-footer">
                        <div class="buttons">
                            <div class="button-wrap">
                                <input class="button" type="submit" 
                                       name="save_data_source" 
                                       value="Save"/>
                            </div>
                        </div>
                    </div>                              
                </form:form>
            </div>
        </div>
    </jsp:body>
</t:generic_page>


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
Document   : Save local radar station
Created on : Oct 5, 2010, 11:49 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Add radar station" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
         <div class="right">
            <div class="blttitle">
                Save radar station
            </div>
            <div class="blttext">
                Select center ID and radar station from the list. 
                Click save button to store settings.  
            </div>
            <div class="table">
                <form:form method="POST" commandName="radar">
                    <div class="saveradar">
                        <div class="row">
                            <div class="leftcol">
                                Center ID
                                <select name="center_id" size="6" multiple 
                                        onchange="submitCenterId(this.form)">
                                    <c:forEach items="${centers}" 
                                               var="center">
                                        <c:choose>
                                            <c:when test="${center == center_selected}">
                                                <option value="${center}" selected 
                                                        title="Country - GTS code - Center number">    
                                                    <c:out value="${center}"/>
                                                </option>    
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${center}"
                                                        title="Country - GTS code - Center number">    
                                                    <c:out value="${center}"/>
                                                </option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                <select>
                            </div>
                            <div class="rightcol">
                                Radar station
                                <select name="radar_id" size="6" multiple>
                                    <c:forEach items="${radars}" 
                                               var="radar">
                                        <option value="${radar}"
                                                title="Place - Code - WMO number">    
                                            <c:out value="${radar}"/>
                                        </option>
                                    </c:forEach>
                                <select>
                            </div>
                        </div>
                    </div>    
                    <div class="tablefooter">
                        <div class="buttons">
                            <button class="rounded" type="button"
                                onclick="window.location.href='settings.htm'">
                                <span>Back</span>
                            </button>
                            <button class="rounded" type="submit">
                                <span>Save</span>
                            </button>
                        </div>
                    </div>
                </form:form>    
            </div>
        </div>
    </jsp:body>
</t:page_tabbed>


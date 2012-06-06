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
Document   : Save data source name and description page
Created on : Apr 22, 2011, 9:04 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Configure data source" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <div class="blttitle">
                Configure data source <div class="stepno">Step 1</div>
            </div>
            <div class="blttext">
                Enter data source name and description
                <div class="hint">
                    Parameters should identify data source characteristics.
                </div>
            </div>
            <div class="table">
                <div class="dssave">
                    <form method="post" action="save_datasource_radar.htm">
                        <div class="row">
                            <div class="leftcol">
                                Data source name
                            </div>
                            <div class="rightcol">
                                <div class="dsname">
                                    <input type="text" name="dsName"
                                        value="${dsName}" title="Enter data source name">
                                    <div class="hint">
                                        Unique name of the data source
                                    </div>
                                </div>
                                <c:if test="${not empty dsNameError}">
                                    <div class="error">
                                        <c:out value="${dsNameError}"/>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                        <div class="row">
                            <div class="leftcol">
                                Description
                            </div>
                            <div class="rightcol">
                                <div class="dsdescription">
                                    <textarea name="dsDescription">${dsDescription}</textarea>
                                    <div class="hint">
                                        Data source description
                                    </div>
                                </div>
                                <c:if test="${not empty dsDescriptionError}">
                                    <div class="error">
                                        <c:out value="${dsDescriptionError}"/>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                        <div class="tablefooter">
                            <div class="buttons">
                                <button class="rounded" type="button"
                                    onclick="window.location.href='settings.htm'">
                                    <span>Back</span>
                                </button>
                                <button class="rounded" type="submit">
                                    <span>Next</span>
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </jsp:body>    
</t:page_tabbed>

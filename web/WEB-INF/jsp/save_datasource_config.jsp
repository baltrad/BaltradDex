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
Document   : Save data source page
Created on : Apr 28, 2011, 8:35 AM
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
                Save data source
            </div>
            <div class="blttext">
                Save data source status.
            </div>
            <div class="table">
                <div class="addradar">
                    <%@include file="/WEB-INF/jsp/generic_messages.jsp"%>
                    <div class="tablefooter">
                        <div class="buttons">
                            <button class="rounded" type="button"
                                onclick="window.location.href='edit_datasource.htm'">
                                <span>OK</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:page_tabbed>    

                   
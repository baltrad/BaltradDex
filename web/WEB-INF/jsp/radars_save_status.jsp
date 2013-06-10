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
Document   : Display save radar station status
Created on : May 21, 2013, 3:03 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Save radar station">
    <jsp:body>
        <div class="show-radars">
            <div class="table">
                <div class="header">
                    <div class="row">Save radar station</div>
                </div>
                <div class="header-text">
                    &nbsp;
                </div>
                <t:message_box msgHeader="Success."
                               msgBody="${radar_save_success}"
                               errorHeader="Problems encountered."
                               errorBody="${radar_save_error}"/>    
                <div class="table-footer">
                    <div class="buttons">
                        <div class="button-wrap">
                            <input class="button" type="button" 
                                   value="OK" 
                                   onclick="window.location.href='radars_show.htm'"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:generic_page>

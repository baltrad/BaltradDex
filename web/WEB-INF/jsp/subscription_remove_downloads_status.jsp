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
Document   : Remove subscription status
Created on : June 3, 2013, 11:42 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Remove subscriptions">
    <jsp:body>
        <div class="show-radars">
            <div class="table">
                <div class="header">
                    <div class="row">Remove downloads</div>
                </div>
                <div class="header-text">
                    Subscription removal status.
                </div>
                <t:message_box msgHeader="Success."
                               msgBody="${subscription_remove_success}"
                               errorHeader="Problems encountered."
                               errorBody="${subscription_remove_error}"/>
                <div class="table-footer">
                    <div class="buttons">
                        <div class="button-wrap">
                            <input class="button" type="button" 
                                   value="OK" 
                                   onclick="window.location.href='subscription_remove_downloads_peers.htm'"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:generic_page>

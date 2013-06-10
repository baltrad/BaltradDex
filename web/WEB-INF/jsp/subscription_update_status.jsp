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
Document   : UPdate subscription status
Created on : June 3, 2013, 9:54 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Subscriptions">
    <jsp:body>
        <div class="show-radars">
            <div class="table">
                <div class="header">
                    <div class="row">Subscription status</div>
                </div>
                <div class="header-text">
                    Data sources subscribed at ${peer_name}.
                </div>
                <t:message_box msgHeader="Success."
                               msgBody="${success_message}"
                               errorHeader="Problems encountered."
                               errorBody="${error_message}"/>
                <div class="table-footer">
                    <div class="buttons">
                        <div class="button-wrap">
                            <input class="button" type="button" 
                                   value="OK" 
                                   onclick="window.location.href='subscription_show.htm?peer_name=${peer_name}'"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:generic_page>

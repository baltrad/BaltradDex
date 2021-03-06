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
Document   : Latest system messages
Created on : May 6, 2013, 9:54 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Latest messages">
    <jsp:body>
        <div class="system-log">
            <div class="table">
                <div class="header">
                    <div class="row">
                        Latest system messages
                        <a href="messages_browser.htm">
                            Browse  
                        </a>
                    </div>
                </div>
                <div class="header-text">
                    Page is refreshed automatically if auto-update option is on.
                    <input type="button" value="Auto-update on" 
                           id="auto-update-toggle" 
                           onclick="toggleTimeout('updateMessages()', 1000);"/>
                </div>
                <div class="body" id="message-table"></div>
            </div>
        </div>
    </jsp:body>
</t:generic_page>



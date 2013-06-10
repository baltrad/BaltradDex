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
    <jsp:attribute name="extraHeader">
        <script type="text/javascript" language="Javascript">
            function view_logs() {
                var self = this;
                // Non-IE browser
                if (window.XMLHttpRequest) {
                    self.ajaxRequest = new XMLHttpRequest();
                }
                // IE
                else if (window.ActiveXObject) {
                    self.ajaxRequest = new ActiveXObject("Microsoft.XMLHTTP");
                }
                self.ajaxRequest.open('POST', 'messages_table.htm', true);
                self.ajaxRequest.setRequestHeader('Content-Type',
                    'application/x-www-form-urlencoded');

                var LOADED = 4;
                self.ajaxRequest.onreadystatechange = function() {
                    if (self.ajaxRequest.readyState == LOADED) {
                        do_update(self.ajaxRequest.responseText);
                    }
                }
                queryString = '';
                self.ajaxRequest.send(queryString);
                if ('${auto_update}' == 'on') {
                    self.setTimeout('view_logs()', 1000);
                }
            }
            function do_update(message) {
                document.getElementById("log").innerHTML = message;
            }
            $(document).ready(function() {
                view_logs();
            });
        </script>
    </jsp:attribute>
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
                    <c:choose>
                        <c:when test="${auto_update == 'on'}">
                            <a href="messages_live.htm?refresh=off">
                                Auto-update on
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="messages_live.htm?refresh=on">
                                Auto update off
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="body" id="log"></div>
            </div>
        </div>
    </jsp:body>
</t:generic_page>



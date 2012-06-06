<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW

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
Document   : Latest system messages
Created on : Jun 2, 2011, 9:54 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="System messages" activeTab="home">
  <jsp:attribute name="extraBottom">
    <script type="text/javascript" language="Javascript">
        <!--
        function viewLogs() {
            var self = this;
            // Non-IE browser
            if( window.XMLHttpRequest ) {
                self.ajaxRequest = new XMLHttpRequest();
            }
            // IE
            else if( window.ActiveXObject ) {
                self.ajaxRequest = new ActiveXObject( "Microsoft.XMLHTTP" );
            }
            self.ajaxRequest.open( 'POST', 'logtable.htm', true );
            self.ajaxRequest.setRequestHeader( 'Content-Type',
                'application/x-www-form-urlencoded' );

            var LOADED = 4;
            self.ajaxRequest.onreadystatechange = function() {
                if(self.ajaxRequest.readyState == LOADED ) {
                    doUpdate(self.ajaxRequest.responseText);
                }
            }
            queryString = '';
            self.ajaxRequest.send( queryString );
            self.setTimeout( 'viewLogs()', 1000 );
        }
        function doUpdate( message ) {
            document.getElementById( "logtable" ).innerHTML = message;
        }
        
        $(document).ready(function() {
          viewLogs();
        });
        -->
    </script>
  </jsp:attribute>
  <jsp:body>
    <div class="left">
      <t:menu_home/>
    </div>
    <div class="right">
        <div class="blttitle">
            Latest system messages
        </div>
        <div class="blttext">
            Most recent set of system messages. Display is refreshed automatically.
        </div>
        <div class="table">
            <div class="log">
                <div id="logtable"></div>
            </div>
        </div>
    </div>
  </jsp:body>
</t:page_tabbed>

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
Document   : System messages
Created on : Sep 30, 2010, 13:34 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | System messages</title>
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
            -->
        </script>
    </head>
    <body>
        <div id="container">
            <div id="header">
                <script type="text/javascript" src="includes/header.js"></script>
            </div>
            <div id="content">
                <div id="left">
                    <%@include file="/WEB-INF/jsp/mainMenu.jsp"%>
                </div>
                <div id="right">
                    <div id="page-title">
                        <div class="left">
                            Latest system messages
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        Latest system messages. Click the button below to view full system log.
                    </div>
                    <div id="table">
                        <div id="logtable">
                            <script type="text/javascript">
                                viewLogs();
                            </script>
                        </div>
                        <div class="footer">
                            <div class="right">
                                <form action="journal.htm">
                                    <button class="rounded" type="submit">
                                        <span>Show all</span>
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <script type="text/javascript" src="includes/footer.js"></script>
        </div>
    </body>
</html>
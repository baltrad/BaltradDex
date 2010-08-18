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
Document   : Page displaying system messages
Created on : Jun 22, 2010, 11:57:02 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Current system messages</title>

    <script language="Javascript">
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
            self.ajaxRequest.setRequestHeader( 'Content-Type', 'application/x-www-form-urlencoded' );

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
    </script>

</head>

<body>
    <div id="container">
        <div id="header"></div>
        <div id="nav">
            <script type="text/javascript" src="includes/navigation.js"></script>
        </div>
        <div class="outer">
            <div class="inner">
                <div class="float-wrap">
                    <div id="main">
                        <h1>Current system messages</h1>
                        <br/>
                        <h2>
                            <p>
                            Latest system messages. Click <a href="journal.htm">here</a> to see
                            full message stack.
                            </p>
                        </h2>
                        <div id="logtable"></div>
                        <script type="text/javascript">
                                viewLogs();
                        </script>
                    </div>
                    <div id="left">
                        <%@ include file="/WEB-INF/jsp/mainMenu.jsp"%>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
    </div>
    <div id="footer">
        <script type="text/javascript" src="includes/footer.js"></script>
    </div>
</body>
</html>












        



   

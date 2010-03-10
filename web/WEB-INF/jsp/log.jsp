<%--
    Document   : Log message page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css">
    <title>Baltrad Data Exchange System</title>

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
            self.setTimeout( 'viewLogs()', 5000 );
        }
        function doUpdate( message ) {
            document.getElementById( "logtable" ).innerHTML = message;
        }
    </script>

</head>

<div id="content">
    <div id="header">
        <img src="includes/images/baltrad_header.png">
    </div>
    <div id="container1">
        <div id="container2">
            <div id="leftcol">
                <script type="text/javascript" src="includes/mainmenu.js"></script>
            </div>
            <div id="rightcol">
                <div id="table-info">
                    Latest system messages. Click <a href="journal.htm">here</a> to see
                    full message stack.
                </div>
                <div id="table-content">
                    <div id="logtable"></div>
                    <script>
                        viewLogs();
                    </script>
                </div>
            </div>
        </div>
    </div>
    <div id="footer">
        <script type="text/javascript" src="includes/footer.js"></script>
    </div>
</div>
</html>












        



   

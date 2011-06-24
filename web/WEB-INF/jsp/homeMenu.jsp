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
Document   : Context menu
Created on : Jun 1, 2011, 9:43 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <script src="includes/js/jQuery.js" type="text/javascript"></script>
        <script src="includes/js/menu.js" type="text/javascript"></script>
        <script src="includes/js/load_menu.js" type="text/javascript"></script>
    </head>
    <body>
        <div class="menu">
            <ul>
                <li>
                    <img src="includes/images/icons/messages.png" alt="">
                    <a href="#">System messages</a>
                    <ul>
                        <li><a href="latestMessages.htm">Latest</a></li>
                        <li><a href="allMessages.htm">All</a></li>
                    </ul>
                </li>
                <li>
                    <img src="includes/images/icons/connection.png" alt="">
                    <a href="#">Exchange status</a>
                    <ul>
                        <li><a href="dataDownload.htm">Download</a></li>
                        <li><a href="dataUpload.htm">Upload</a></li>
                    </ul>
                </li>
                <li>
                    <img src="includes/images/icons/data-source.png" alt="">
                    <a href="dsShow.htm">Data sources</a>
                </li>
            </ul>
        </div>
    </body>
</html>

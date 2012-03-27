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
Created on : May 31, 2011, 9:28 AM
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
                    <img src="includes/images/icons/radar.png" alt="">
                    <a href="#">Radars</a>
                    <ul>
                        <li><a href="editRadar.htm">Edit</a></li>
                        <li><a href="saveRadar.htm">Add</a></li>
                        <li><a href="removeRadar.htm">Remove</a></li>
                    </ul>
                </li>
                <li>
                    <img src="includes/images/icons/data-source.png" alt="">
                    <a href="#">Data sources</a>
                    <ul>
                        <li><a href="dsSelectEdit.htm">Edit</a></li>
                        <li><a href="dsSaveName.htm">Add</a></li>
                        <li><a href="dsSelectRemove.htm">Remove</a></li>
                    </ul>
                </li>
                <li>
                    <img src="includes/images/icons/subscription.png" alt="">
                    <a href="#">Subscriptions</a>
                    <ul>
                        <li><a href="removeDownloadSubscriptions.htm">Download</a></li>
                        <li><a href="removeUploadSubscriptions.htm">Upload</a></li>
                    </ul>
                </li>
                <li>
                    <img src="includes/images/icons/register.png" alt="">
                    <a href="#">Delivery registry</a>
                    <ul>
                        <li><a href="configureRegistry.htm">Configure</a></li>
                        <li><a href="removeRegisterEntries.htm">Clear</a></li>
                    </ul>
                </li>
                <li>
                    <img src="includes/images/icons/connection.png" alt="">
                    <a href="#">Exchange</a>
                    <ul>
                        <li><a href="removeNodeConnections.htm">Connections</a></li>
                    </ul>
                </li>
                <li>
                    <img src="includes/images/icons/messages.png" alt="">
                    <a href="#">System messages</a>
                    <ul>
                        <li><a href="configureMessages.htm">Configure</a></li>
                        <li><a href="removeMessages.htm">Clear</a></li>
                    </ul>
                </li>
                <li>
                    <img src="includes/images/icons/user.png" alt="">
                    <a href="#">User accounts</a>
                    <ul>
                        <li><a href="editAccount.htm">Edit</a></li>
                        <li><a href="saveAccount.htm">Add</a></li>
                        <li><a href="removeAccount.htm">Remove</a></li>
                    </ul>
                </li>
                <li>
                    <img src="includes/images/icons/settings.png" alt="">
                    <a href="nodeProperties.htm">Node settings</a>
                </li>
            </ul>
        </div>
    </body>
</html>

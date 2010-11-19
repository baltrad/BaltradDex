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
Document   : Local node configuration
Created on : Oct 4, 2010, 8:49 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Configuration</title>
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
                            Configuration options
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        Select configuration options below.
                    </div>
                    <div id="table">
                        <div class="config">
                            <div class="left">
                                <div class="tile">
                                    <div class="icon">
                                        <img src="includes/images/icons/user-admin.png"
                                             alt="user_accounts"/>
                                    </div>
                                    <div class="header">
                                        User Accounts
                                    </div>
                                    <div class="text">
                                        <div class="row">
                                            <a href="editUser.htm">Edit</a>
                                        </div>
                                        <div class="row">
                                            <a href="saveUser.htm">Add</a>
                                        </div>
                                        <div class="row">
                                            <a href="showUsers.htm">Remove</a>
                                        </div>
                                    </div>
                                </div>
                                <div class="tile">
                                    <div class="icon">
                                        <img src="includes/images/icons/graph.png"
                                                     alt="data_delivery_register"/>
                                    </div>
                                    <div class="header">
                                        Delivery Register
                                    </div>
                                    <div class="text">
                                        <div class="row">
                                            <a href="showRegister.htm">Show</a>
                                        </div>
                                        <div class="row">
                                            <a href="clearRegister.htm">Clear</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="middle">
                                <div class="tile">
                                    <div class="icon">
                                        <img src="includes/images/icons/radar.png"
                                                     alt="local_radars"/>
                                    </div>
                                    <div class="header">
                                        Radars
                                    </div>
                                    <div class="text">
                                        <div class="row">
                                            <a href="editLocalChannel.htm">Edit</a>
                                        </div>
                                        <div class="row">
                                            <a href="saveLocalChannel.htm">Add</a>
                                        </div>
                                        <div class="row">
                                            <a href="showLocalChannels.htm">Remove</a>
                                        </div>
                                    </div>
                                </div>
                                <div class="tile">
                                    <div class="icon">
                                        <img src="includes/images/icons/arrow-transfer.png"
                                                     alt="node_connections"/>
                                    </div>
                                    <div class="header">
                                        Node Connections
                                    </div>
                                    <div class="text">
                                        <div class="row">
                                            <a href="showNodeConnections.htm">Remove</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="right">
                                <div class="tile">
                                    <div class="icon">
                                        <img src="includes/images/icons/subscription.png"
                                                     alt="subscribed_radars"/>
                                    </div>
                                    <div class="header">
                                        Subscriptions
                                    </div>
                                    <div class="text">
                                        <div class="row">
                                            <a href="selectRemoveSubscriptions.htm">
                                                Subscribed radars</a>
                                        </div>
                                        <div class="row">
                                            <a href="showPeersSubscriptions.htm">
                                                Peer's subscriptions</a>
                                        </div>
                                    </div>
                                </div>
                                <div class="tile">
                                    <div class="icon">
                                        <img src="includes/images/icons/chat.png"
                                                     alt="messages"/>
                                    </div>
                                    <div class="header">
                                        System Messages
                                    </div>
                                    <div class="text">
                                        <div class="row">
                                            <a href="clearmessages.htm">Clear</a>
                                        </div>
                                    </div>
                                </div>
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

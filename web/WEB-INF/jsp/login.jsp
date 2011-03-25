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
Document   : Log in page
Created on : Sep 22, 2010, 1:51 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="eu.baltrad.dex.util.InitAppUtil"%>
<%
    Date now = new Date();
    SimpleDateFormat format = new SimpleDateFormat( "dd MMMM yyyy, h:mm aa" );
    String dateAndTime = format.format( now );
    String operator = InitAppUtil.getOrgName();
    String nodeName = InitAppUtil.getNodeName();
    String nodeVersion = InitAppUtil.getNodeVersion();
    String nodeType = InitAppUtil.getNodeType();
    String address = InitAppUtil.getOrgAddress();
    String timeZone = InitAppUtil.getTimeZone();
    String adminEmail = InitAppUtil.getAdminEmail();
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" language="javascript" src="includes/tooltip.js"></script>
        <title>Baltrad | Log in</title>
        <!-- tooltips -->
        <script type="text/javascript">
            var t1 = null;
            var t2 = null;
            var l1 = "Enter your user name";
            var l2 = "Enter password";
            function initTooltips() {
                t1 = new ToolTip( "user_name_tooltip", false );
                t2 = new ToolTip( "password_tooltip", false );
            }
        </script>
    </head>
    <body onload="initTooltips()">
        <div id="container">
            <div id="header">
                <script type="text/javascript" src="includes/header_login.js"></script>
            </div>
            <div id="content">
                <div id="left">
                    <div id="clear"></div>
                </div>
                <div id="right">
                    <div id="page-title">
                        <div class="left">
                            Log in
                        </div>
                        <div class="right">
                            <%= dateAndTime %>
                        </div>
                    </div>
                    <div id="text-box">
                        Please enter your user name, password and e-mail address in the boxes below.
                    </div>
                    <div id="text-box">
                        <a href="recovery.htm">Click here if you have forgotten
                            your security details.
                        </a>
                    </div>
                    
                    <div id="login">
                        <form method="post">
                            <div class="login-top">
                                <%@include file="/WEB-INF/jsp/messages.jsp"%>
                            </div>
                            <div class="login-left">
                                <div class="login-row">User Name</div>
                                <div class="login-row">Password</div>
                            </div>
                            <div class="login-right">
                                <div class="login-row">
                                    <form:input path="command.name"/>
                                    <div class="help-icon" onmouseover="if(t1)t1.Show(event,l1)"
                                        onmouseout="if(t1)t1.Hide(event)">
                                        <img src="includes/images/help-icon.png" alt="help_icon"/>
                                    </div>
                                </div>
                                <div class="login-row">
                                    <form:password path="command.password"/>
                                    <div class="help-icon" onmouseover="if(t2)t2.Show(event,l2)"
                                        onmouseout="if(t2)t2.Hide(event)">
                                        <img src="includes/images/help-icon.png" alt="help_icon"/>
                                    </div>
                                </div>
                            </div>
                            <div class="login-bottom">
                                <button class="rounded" type="submit">
                                    <span>Submit</span>
                                </button>
                                <button class="rounded" type="reset">
                                    <span>Clear</span>
                                </button>
                            </div>
                        </form>
                    </div>
                    <div id="separator"></div>
                    <div id="text-box">
                        <div class="title">
                            Welcome to Baltrad Data Exchange System!
                        </div>
                    </div>
                        <div id="welcome-msg">
                            <div class="left">
                                <div class="row">
                                    This node is operated by:
                                </div>
                                <div class="row">
                                    Node location:
                                </div>
                                <div class="row">
                                    Node name:
                                </div>
                                <div class="row">
                                    Node version:
                                </div>
                                <div class="row">
                                    Node type:
                                </div>
                                <div class="row">
                                    Local time zone:
                                </div>
                                <div class="row">
                                    Node administrator's e-mail:
                                </div>
                            </div>
                            <div class="right">
                                <div class="row">
                                    <%= operator %>
                                </div>
                                <div class="row">
                                    <%= address %>
                                </div>
                                <div class="row">
                                    <%= nodeName %>
                                </div>
                                <div class="row">
                                    <%= nodeVersion %>
                                </div>
                                <div class="row">
                                    <%= nodeType %>
                                </div>
                                <div class="row">
                                    <%= timeZone %>
                                </div>
                                <div class="row">
                                    <%= adminEmail %>
                                </div>
                            </div>
                        </div>      
                    </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
        <div id="user_name_tooltip" class="tooltip" style="width: 160px; height: 22px;"></div>
        <div id="password_tooltip" class="tooltip" style="width: 160px; height: 22px;"></div>
    </body>
</html>

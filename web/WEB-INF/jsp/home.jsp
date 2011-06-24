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
Document   : Home page
Created on : Sep 22, 2010, 1:51 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="eu.baltrad.dex.util.InitAppUtil"%>

<jsp:useBean id="initAppUtil" scope="session" class="eu.baltrad.dex.util.InitAppUtil">
</jsp:useBean>
<jsp:useBean id="securityManager" scope="session"
             class="eu.baltrad.dex.util.ApplicationSecurityManager"></jsp:useBean>
<%
    User user = ( User )securityManager.getUser( request );
    String userName = user.getName();
    String nodeName = initAppUtil.getNodeName();
    String operator = initAppUtil.getOrgName();
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
        <title>Baltrad | Home</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/homeTab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/homeMenu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            Welcome to Baltrad Radar Data Exchange and Processing System!
                        </div>
                        <div class="blttext">
                            <p>
                                Baltrad is running on <%=nodeName%> operated by <%=operator%>.
                            </p>
                            <p>
                                You have signed in as user <%=userName%>.
                            </p>
                            <p>
                                Following is the information about local Baltrad node.
                            </p>
                        </div>
                        <div class="bltseparator"></div>
                        <div class="table">
                            <div class="leftcol">
                                <div class="row">
                                    Operator:
                                </div>
                                <div class="row">
                                    Node location:
                                </div>
                                <div class="row">
                                    Node name:
                                </div>
                                <div class="row">
                                    Software version:
                                </div>
                                <div class="row">
                                    Node type:
                                </div>
                                <div class="row">
                                    Local time zone:
                                </div>
                                <div class="row">
                                    Admin e-mail:
                                </div>
                            </div>
                            <div class="rightcol">
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
                </div>
            </div>
        </div>
        <div id="bltfooter">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>

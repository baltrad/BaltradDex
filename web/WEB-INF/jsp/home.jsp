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
Document   : System home page
Created on : Sep 23, 2010, 12:20 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Home</title>
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
                            Home
                        </div>
                        <div class="right">

                        </div>
                    </div>
                    <div id="text-box">
                        Welcome to Baltrad Data Exchange System!
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




<%--!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<jsp:useBean id="initAppUtil" scope="session" class="eu.baltrad.dex.util.InitAppUtil">
</jsp:useBean>

<jsp:useBean id="securityManager" scope="session" class="eu.baltrad.dex.util.ApplicationSecurityManager">
</jsp:useBean>

<%
    User user = ( User )securityManager.getUser( request );
    String userName = user.getName();
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Home</title>
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
                        <div id="welcome">
                            <h1>Welcome to the Baltrad Data Exchange System!</h1>
                            <br/>
                            <h2>
                                <p>
                                You are signed in as user <% out.println( userName + "."); %>
                                </p>
                                <p>
                                Use the system functionality to browse and download data or establish
                                operational data exchange links by subscribing to the desired data
                                channel.
                                </p>
                                <p>
                                Following is the detailed information about this node.
                                </p>
                            </h2>
                        </div>
                        <table>
                            <tr class="even">
                                <td class="welcome-leftcol">Node name</td>
                                <td class="welcome-rightcol">
                                    <% out.println( initAppUtil.getNodeName() ); %>
                                </td>
                            </tr>
                            <tr class="odd">
                                <td class="welcome-leftcol">Node type</td>
                                <td class="welcome-rightcol">
                                    <% out.println( initAppUtil.getNodeType() ); %>
                                </td>
                            </tr>
                            <tr class="even">
                                <td class="welcome-leftcol">Organization name</td>
                                <td class="welcome-rightcol">
                                    <% out.println( initAppUtil.getOrgName() ); %>
                                </td>
                            </tr>
                            <tr class="odd">
                                <td class="welcome-leftcol">Address</td>
                                <td class="welcome-rightcol">
                                    <% out.println( initAppUtil.getOrgAddress() ); %>
                                </td>
                            </tr>
                            <tr class="even">
                                <td class="welcome-leftcol">Time zone</td>
                                <td class="welcome-rightcol">
                                    <% out.println( initAppUtil.getTimeZone() ); %>
                                </td>
                            </tr>
                            <tr class="odd">
                                <td class="welcome-leftcol">Node administrator's email</td>
                                <td class="welcome-rightcol">
                                    <% out.println( initAppUtil.getAdminEmail() ); %>
                                </td>
                            </tr>
                        </table>
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
</html --%>
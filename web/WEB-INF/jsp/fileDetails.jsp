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
Document   : Data file detailed information
Created on : Nov 15, 2010, 9:53 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@ page import="eu.baltrad.dex.data.model.Data" %>
<%@ page import="java.io.File" %>

<%
    Data data = ( Data )request.getAttribute( "file_details" );
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | File details</title>
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
                            File details
                        </div>
                        <div class="right">
                        </div>
                    </div>
                        <div id="text-box">
                            Detailed data file information.
                        </div>
                    <div id="table">
                        <div class="left">
                            <div class="row">File entry ID</div>
                            <div class="row">File name</div>
                            <div class="row">Stored at</div>
                            <div class="row">Source</div>
                            <div class="row">Date</div>
                            <div class="row">Time</div>
                            <div class="row">Data type</div>
                        </div>
                        <div class="right">
                            <div class="row">
                                <% out.println( data.getUuid() ); %>
                            </div>
                            <div class="row">
                                <% out.println( data.getPath().substring(
                                    data.getPath().lastIndexOf( File.separator ) + 1,
                                    data.getPath().length() ) ); %>
                            </div>
                            <div class="row">
                                <% out.println( data.getTimeStamp() ); %>
                            </div>
                            <div class="row">
                                <% out.println( data.getRadarName() ); %>
                            </div>
                            <div class="row">
                                <% out.println( data.getDate() ); %>
                            </div>
                            <div class="row">
                                <% out.println( data.getTime() ); %>
                            </div>
                            <div class="row">
                                <% out.println( data.getType() ); %>
                            </div>
                        </div>
                    </div>    
                    <div class="footer">
                        <div class="right">
                            <form action="radars.htm">
                                <button class="rounded" type="button"
                                    onclick="history.go(-1);">
                                    <span>Back</span>
                                </button>
                            </form>
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





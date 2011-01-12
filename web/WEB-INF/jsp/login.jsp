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
Document   : Log in page
Created on : Sep 22, 2010, 1:51 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
    Date now = new Date();
    SimpleDateFormat format = new SimpleDateFormat( "dd MMMM yyyy, h:mm aa" );
    String dateAndTime = format.format( now );
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" language="javascript" src="includes/tooltip.js"></script>
        <!-- tooltips -->
        <script type="text/javascript">
            var t1 = null;
            var t2 = null;
            var l1 = "Submit your security details";
            var l2 = "Clear input fields";
            function initTooltips() {
                t1 = new ToolTip( "submit_btn_tooltip", false );
                t2 = new ToolTip( "clear_btn_tooltip", false );
            }
        </script>
        <title>Baltrad | Log in</title>
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
                            <div class="top">
                                <%@include file="/WEB-INF/jsp/messages.jsp"%>
                            </div>
                            <div class="left">
                                <div class="row">User Name</div>
                                <div class="row">Password</div>
                                <div class="row">Email Address</div>
                            </div>
                            <div class="right">
                                <div class="row">
                                    <form:input path="command.name"/>
                                </div>
                                <div class="row">
                                    <form:password path="command.password"/>
                                </div>
                                <div class="row">
                                    <form:input path="command.email"/>
                                </div>
                            </div>
                            <div class="bottom">
                                <button class="rounded" type="submit"
                                        onmouseover="if(t1)t1.Show(event,l1)"
                                        onmouseout="if(t1)t1.Hide(event)">
                                    <span>Submit</span>
                                </button>
                                <button class="rounded" type="reset" 
                                        onmouseover="if(t2)t2.Show(event,l2)"
                                        onmouseout="if(t2)t2.Hide(event)">
                                    <span>Clear</span>
                                </button>
                            </div>
                        </form>
                    </div>
                    <div id="separator"></div>
                    <div id="text-box">
                        <div class="title">
                            Latest messages
                        </div>
                    </div>
                        <div id="log-preview-table">
                            <display:table name="log_entries" id="logEntry"
                                requestURI="login.htm" cellpadding="0" cellspacing="2"
                                export="false"  defaultsort="0">
                                <%-- ! String cell_style = ""; --%>
                                <%--c:choose>
                                    <c:when test="${logEntry.type == 'INFO'}">
                                        <%
                                            cell_style = "info";
                                        %>
                                    </c:when>
                                    <c:when test="${logEntry.type == 'WARNING'}">
                                        <%
                                            cell_style = "warning";
                                        %>
                                    </c:when>
                                    <c:when test="${logEntry.type == 'ERROR'}">
                                        <%
                                            cell_style = "error";
                                        %>
                                    </c:when>
                                </c:choose --%>
                                <display:column sortable="false" paramId="timeStamp"
                                    paramProperty="timeStamp" class="tdcenter"
                                    value="${logEntry.timeStamp}" format="{0,date,yy/MM/dd}"
                                    headerClass="tdhidden">
                                </display:column>
                                <display:column sortable="false" paramId="timeStamp"
                                    paramProperty="timeStamp" class="tdcenter"
                                    value="${logEntry.timeStamp}" format="{0,date,HH:mm:ss}"
                                    headerClass="tdhidden">
                                </display:column>
                                <display:column sortable="false" paramId="message"
                                    paramProperty="message" class="tdcenter"
                                    value="${logEntry.message}" headerClass="tdhidden">
                                </display:column>
                            </display:table>
                        </div>
                    <div id="clear"></div>
                </div>
            </div>
        </div>
        <div id="footer">
            <script type="text/javascript" src="includes/footer.js"></script>
        </div>
        <div id="clear_btn_tooltip" class="tooltip" style="width: 150px; height: 22px;"></div>
        <div id="submit_btn_tooltip" class="tooltip" style="width: 150px; height: 44px;"></div>
    </body>
</html>
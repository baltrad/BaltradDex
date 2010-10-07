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
Document   : Data delivery register
Created on : Oct 6, 2010, 10:49 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="java.util.List" %>
<%
    // get delivery register
    List deliveryRegister = ( List )request.getAttribute( "register_records" );
    if( deliveryRegister == null || deliveryRegister.size() <= 0 ) {
        request.getSession().setAttribute( "register_status", 0 );
    } else {
        request.getSession().setAttribute( "register_status", 1 );
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Delivery register</title>
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
                            Data delivery register
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <c:choose>
                        <c:when test="${register_status == 1}">
                            <div id="text-box">
                                Data delivery register listing.
                            </div>
                            <div id="table">
                                <display:table name="register_records" id="registerRecord" defaultsort="1"
                                    requestURI="showregister.htm" cellpadding="0" cellspacing="2"
                                    export="false" class="tableborder" pagesize="12">
                                    <display:column sortProperty="dataId" sortable="true"
                                        title="Data ID" class="tdcenter">
                                        <fmt:formatNumber value="${registerRecord.dataId}" pattern="00" />
                                    </display:column>
                                    <display:column sortable="true" title="Source data channel"
                                        sortProperty="channelName" class="tdcenter"
                                        value="${registerRecord.channelName}">
                                    </display:column>
                                    <display:column sortable="true" title="User ID" sortProperty="userId"
                                        class="tdcenter">
                                        <fmt:formatNumber value="${registerRecord.userId}" pattern="00" />
                                    </display:column>
                                    <display:column sortable="true" title="Receiver address"
                                        sortProperty="receiverAddress" class="tdcenter"
                                        value="${registerRecord.receiverAddress}">
                                    </display:column>
                                </display:table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="message">
                                <div class="icon">
                                    <img src="includes/images/icons/circle-alert.png"
                                         alt="no_entries"/>
                                </div>
                                <div class="text">
                                    No entries found in data delivery register.
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>  
                    <div class="footer">
                        <div class="right">
                            <button class="rounded" type="button"
                                onclick="window.location='configuration.htm'">
                                <span>Back</span>
                            </button>
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

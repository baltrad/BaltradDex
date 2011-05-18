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
Document   : Data upload status page
Created on : Apr 1, 2011, 12:13 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<jsp:useBean id="initAppUtil" scope="session" class="eu.baltrad.dex.util.InitAppUtil">
</jsp:useBean>
<jsp:useBean id="securityManager" scope="session"
             class="eu.baltrad.dex.util.ApplicationSecurityManager"></jsp:useBean>
<%
    User user = ( User )securityManager.getUser( request );
    String userName = user.getName();
    String nodeName = initAppUtil.getNodeName();
    String operator = initAppUtil.getOrgName();
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <meta name="save" content="history" />
        <noscript>
            <style type="text/css"><!--.dspcont{display:block;}--></style>
        </noscript>
        <script type="text/javascript" language="javascript" src="includes/expandable.js"></script>
        <script type="text/javascript" language="javascript" src="includes/tooltip.js"></script>
        <title>Baltrad | Home</title>
        <!-- tooltips -->
        <script type="text/javascript">
            <!--
            var t1 = null;
            var l1 = "Shows list of users subscribing data sources at this node. Clicking on a " +
                " user name expands the detailed list of data sources from which they receive data";
            function initTooltips() {
                t1 = new ToolTip( "upload_tooltip", false );
            }
            -->
        </script>
    </head>
    <body onload="initTooltips()">
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
                            Local node
                        </div>
                        <div class="right">

                        </div>
                    </div>
                    <div id="text-box">
                        <div class="title">
                            Welcome to Baltrad Radar Data Exchange & Processing System!
                        </div>
                    </div>
                    <div id="text-box">
                        Baltrad is running on <%=nodeName%> operated by <%=operator%>.
                    </div>
                    <div id="text-box">
                        You are logged in as user <%=userName%>.
                    </div>
                    <div id="text-box">
                        <div class="title">
                            Data exchange status
                        </div>
                    </div>
                    <div id="tabs">
                        <div id="tab">
                            <div class="icon">
                                <img src="includes/images/icons/arrow-down-small.png"
                                     alt="download"/>
                            </div>
                            <div class="link">
                                <a href="dataDownload.htm">
                                    Download
                                </a>
                            </div>
                        </div>

                        <div id="tab" class="active">
                            <div class="icon">
                                <img src="includes/images/icons/arrow-up-small.png"
                                     alt="upload"/>
                            </div>
                            <div class="link">
                                <a href="dataUpload.htm">
                                    Upload
                                </a>
                            </div>
                        </div>

                    </div>
                    <div id="tabcontent">
                        <div id="text-box">
                            <div class="title">
                                Data upload status | Data sent to subscribers
                                <div class="help-icon-right" onmouseover="if(t1)t1.Show(event,l1)"
                                        onmouseout="if(t1)t1.Hide(event)">
                                    <img src="includes/images/help-icon.png" alt="help_icon"/>
                                </div>
                            </div>
                        </div>
                        <c:choose>
                            <c:when test="${not empty users}">
                                <div>
                                Click on user name to view detailed information on subscriber.
                                </div>
                                <c:forEach var="user" items="${users}">
                                <c:set var="usr" scope="page" value="${user}"></c:set>
                                <div class="expandable">
                                    <div class="save">
                                        <div class="expandable-hdr">
                                            <a href="javascript:void(0)" class="dsphead"
                                            onclick="dsp(this)">
                                                <span class="dspchar">+</span>
                                                <c:out value="${user}"/>
                                            </a>
                                        </div>
                                        <div class="dspcont">
                                            <c:choose>
                                            <c:when test="${not empty remote}">
                                                <div id="statustable">
                                                    <div class="table-hdr">
                                                        <div class="station">
                                                            Data source
                                                        </div>
                                                        <div class="timestamp">
                                                            Started on
                                                        </div>
                                                    </div>
                                                    <c:forEach var="sub" items="${remote}">  
                                                        <c:if test="${sub.userName == usr}">
                                                            <div class="table-row">
                                                                <div class="station">
                                                                    <c:out value="${sub.dataSourceName}"/>
                                                                </div>
                                                                <div class="timestamp">
                                                                    <c:out value="${sub.dateStr}"/>,
                                                                    <c:out value="${sub.timeStr}"/>
                                                                </div>
                                                            </div>
                                                        </c:if>
                                                    </c:forEach>
                                                </div>
                                            </c:when>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="message">
                                    <div class="icon">
                                        <img src="includes/images/icons/circle-alert.png"
                                             alt="no_data_sources"/>
                                    </div>
                                    <div class="text">
                                        Your local data sources are currently not subscribed
                                        by peers.
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    <div class="footer"></div>
                    </div>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
        <div id="upload_tooltip" class="tooltip" style="width: 240px; height: 88px;"></div>
    </body>
</html>

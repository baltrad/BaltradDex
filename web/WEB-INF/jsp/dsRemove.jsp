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
Document   : Delete data source page
Created on : Apr 28, 2011, 8:34 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Remove data source</title>
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
                            Data source removal status
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        <c:choose>
                            <c:when test="${not empty dsRemoveError}">
                                <div class="message">
                                    <div class="icon">
                                         <img src="includes/images/icons/circle-delete.png"
                                             alt="save_error"/>
                                    </div>
                                    <div class="text">
                                        Failed to remove data source. Check system messages
                                        for details.
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="message">
                                    <div class="icon">
                                        <img src="includes/images/icons/circle-check.png"
                                             alt="save_ok"/>
                                    </div>
                                    <div class="text">
                                        Data source successfully removed.
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="footer">
                        <div class="right">
                            <button class="rounded" type="button"
                                onclick="window.location='configuration.htm'">
                                <span>OK</span>
                            </button>
                        </div>
                    </div>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>
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
Document   : Save data source name and description page
Created on : Apr 22, 2011, 9:04 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="java.util.List" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" language="javascript" src="includes/tooltip.js"></script>
        <script type="text/javascript">
            <!--
            var t1 = null;
            var t2 = null;
            var l1 = "Unique name of this data source";
            var l2 = "Verbose description of data source characteristics";
            function initTooltips() {
                t1 = new ToolTip( "ttName", false );
                t2 = new ToolTip( "ttDescription", false );
            }
            -->
        </script>
        <title>Baltrad | Configure data source</title>
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
                            Configure data source
                        </div>
                        <div class="right">
                        </div>
                    </div>
                        <div id="text-box">
                            Step 1. Enter data source name and description
                        </div>
                        <div id="table">
                            <form method="post" action="dsSaveRadars.htm">
                                <div id="dsConfig">
                                    <div class="row">
                                        <div class="left">
                                            Data source name
                                        </div>
                                        <div class="right">
                                            <input type="text" name="dsName" class="dsNameInput"
                                                   value="${dsName}">
                                            <div class="helpIconLeft"
                                                    onmouseover="if(t1)t1.Show(event,l1)"
                                                    onmouseout="if(t1)t1.Hide(event)">
                                                <img src="includes/images/help-icon.png"
                                                    alt="helpIcon"/>
                                            </div>
                                            <c:if test="${not empty dsNameError}">
                                                <div class="errors">
                                                    <c:out value="${dsNameError}"/>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="left">
                                            Description
                                        </div>
                                        <div class="right">
                                            <textarea name="dsDescription" rows="4" cols="10"
                                                      class="dsDescriptionInput">
                                                <c:out value="${dsDescription}"/>
                                            </textarea>
                                            <div class="helpIconLeft"
                                                    onmouseover="if(t2)t2.Show(event,l2)"
                                                    onmouseout="if(t2)t2.Hide(event)">
                                                <img src="includes/images/help-icon.png"
                                                    alt="helpIcon"/>
                                            </div>
                                            <c:if test="${not empty dsDescriptionError}">
                                                <div class="errors">
                                                    <c:out value="${dsDescriptionError}"/>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="footer">
                                        <div class="right">
                                            <button class="rounded" type="button"
                                                onclick="window.location='configuration.htm'">
                                                <span>Back</span>
                                            </button>
                                            <button class="rounded" type="submit">
                                                <span>Next</span>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
        <div id="ttName" class="tooltip" style="width:170px; height:40px;"></div>
        <div id="ttDescription" class="tooltip" style="width:210px; height:40px;"></div>
    </body>
</html>
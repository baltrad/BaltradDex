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
List of adaptors
@date 2010-03-23
@author Anders Henja
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Adaptors</title>
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
                            Adaptors
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        List of adaptors. Press on adaptor name to modify/delete or press
                        Create to create a new adaptor.
                    </div>
                    <div id="table">
                        <form name="createAdaptorForm" action="createadaptor.htm">
                            <display:table name="adaptors" id="adaptor" defaultsort="1"
                                requestURI="adaptors.htm" cellpadding="5" cellspacing="0"
                                export="false" class="tableborder">
                              <display:column sortable="true" title="Name"
                                  sortProperty="name" href="showadaptor.htm" paramId="name"
                                  paramProperty="name" class="tdcenter" value="${adaptor.name}">
                              </display:column>
                              <display:column sortable="true" title="Type"
                                  sortProperty="type" paramId="type" paramProperty="type"
                                  class="tdcenter" value="${adaptor.type}">
                              </display:column>
                            </display:table>
                            <div class="footer">
                                <div class="right">
                                    <button class="rounded" type="button" onclick="history.go(-1);">
                                        <span>Back</span>
                                    </button>
                                    <button class="rounded" type="submit">
                                        <span>Create</span>
                                    </button>
                                </div>
                            </div>
                        </form>
                        <%if (request.getAttribute("emessage") != null) {%>
                        <div class="beast-error"><%=request.getAttribute("emessage")%></div>
                        <%}%>
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

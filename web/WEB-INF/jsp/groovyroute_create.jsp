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
Creates a groovy route
@date 2010-03-25
@author Anders Henja
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Create route</title>
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
                            Create route
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        Create a Groovy scripted routing rule.
                    </div>
                    <div id="table">
                        <div class="props">
                            <form name="createRouteForm" action="groovyroute_create.htm">
                                <div class="left">
                                    <%
                                        List<String> adaptors = (List<String>)request.getAttribute("adaptors");
                                        String name = (String)request.getAttribute("name");
                                        String author = (String)request.getAttribute("author");
                                        Boolean active = (Boolean)request.getAttribute("active");
                                        String description = (String)request.getAttribute("description");
                                        List<String> recipients = (List<String>)request.getAttribute("recipients");
                                        String typdef = (String)request.getAttribute("typdef");
                                        String activestr = (active == true)?"checked":"";
                                      %>
                                    <div class="row">Name</div>
                                    <div class="row">Author</div>
                                    <div class="row">Active</div>
                                    <div class="row">Description</div>
                                    <div class="row">Recipients</div>
                                    <div class="row">Script</div>
                                </div>
                                <div class="right">
                                    <div class="row">
                                        <input type="text" name="name" value="<%=name%>"/>
                                    </div>
                                    <div class="row">
                                        <input type="text" name="author" value="<%=author%>"/>
                                    </div>
                                    <div class="row">
                                        <input type="checkbox" name="active" <%=activestr%>/>
                                    </div>
                                    <div class="row">
                                        <input type="text" name="description" value="<%=description%>"/>
                                    </div>
                                    <div class="row">
                                        <select multiple size="4" name="recipients">
                                        <%
                                          for (String adaptor : adaptors) {
                                            String selectstr = "";
                                            if (recipients.contains(adaptor)) {
                                              selectstr = "selected";
                                            }
                                        %>
                                            <option value="<%=adaptor%>" <%=selectstr%>><%=adaptor%></option>
                                        <%
                                          }
                                        %>
                                        </select>
                                    </div>
                                    <div class="row">
                                        <textarea class="routedefinition" name="typdef"><%=typdef%></textarea>
                                    </div>
                                </div>
                                <div class="footer">
                                    <div class="right">
                                        <button class="rounded" type="submit">
                                            <span>Add</span>
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    <%if (request.getAttribute("emessage") != null) {%>
                            <div class="routererror"><%=request.getAttribute("emessage")%></div>
                    <%}%>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <script type="text/javascript" src="includes/footer.js"></script>
        </div>
    </body>
</html>

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
Creates a volume route
@date 2011-01-06
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
                        Create a volume routing rule.
                    </div>
                    <div id="table">
                        <div class="props">
                            <form name="createRouteForm" action="volumeroute_create.htm">
                                <div class="left">
                                    <%
                                        List<String> adaptors = (List<String>)request.getAttribute("adaptors");
                                        List<String> sourceids = (List<String>)request.getAttribute("sourceids");
                                        List<Integer> intervals = (List<Integer>)request.getAttribute("intervals");

                                        String name = (String)request.getAttribute("name");
                                        String author = (String)request.getAttribute("author");
                                        Boolean active = (Boolean)request.getAttribute("active");
                                        String description = (String)request.getAttribute("description");
                                        Boolean ascending = (Boolean)request.getAttribute("ascending");
                                        Double mine = (Double)request.getAttribute("mine");
                                        Double maxe = (Double)request.getAttribute("maxe");
                                        List<String> recipients = (List<String>)request.getAttribute("recipients");
                                        Integer interval = (Integer)request.getAttribute("interval");
                                        Integer timeout = (Integer)request.getAttribute("timeout");
                                        List<String> sources = (List<String>)request.getAttribute("sources");

                                        String activestr = (active == true)?"checked":"";
                                        String ascendingstr = (ascending == true)?"checked":"";
                                    %>
                                    <div class="row">Name</div>
                                    <div class="row">Author</div>
                                    <div class="row">Active</div>
                                    <div class="row">Description</div>
                                    <div class="row">Ascending</div>
                                    <div class="row">Min elevation</div>
                                    <div class="row">Max elevation</div>
                                    <div class="row4">Recipients</div>
                                    <div class="row">Interval</div>
                                    <div class="row">Timeout</div>
                                    <div class="row6">Sources</div>
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
                                        <input type="checkbox" name="ascending" <%=ascendingstr%>/>
                                    </div>
                                    <div class="row">
                                        <input type="text" name="mine" value="<%=mine%>"/>
                                    </div>
                                    <div class="row">
                                        <input type="text" name="maxe" value="<%=maxe%>"/>
                                    </div>
                                    <div class="row4">
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
                                        <select name="interval">
                                        <%
                                          for (Integer iv : intervals) {
                                            String selectstr = "";
                                            if (iv.equals(interval)) {
                                              selectstr = "selected";
                                            }
                                        %>
                                            <option value="<%=iv%>" <%=selectstr%>><%=iv%></option>
                                        <%
                                          }
                                        %>
                                        </select>
                                    </div>
                                    <div class="row">
                                        <input type="text" name="timeout" value="<%=timeout%>"/>
                                    </div>
                                    <div class="row6">
                                        <select multiple size="6" name="sources">
                                        <%
                                          for (String id : sourceids) {
                                            String selectstr = "";
                                            if (sources.contains(id)) {
                                              selectstr = "selected";
                                            }
                                        %>
                                            <option value="<%=id%>" <%=selectstr%>><%=id%></option>
                                        <%
                                          }
                                        %>
                                        </select>
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
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>

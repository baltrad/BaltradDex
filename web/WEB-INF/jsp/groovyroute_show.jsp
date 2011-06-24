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
Modifies a groovy route
@date 2010-03-25
@author Anders Henja
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="eu.baltrad.beast.router.RouteDefinition"%>
<%@page import="eu.baltrad.beast.rules.IRule"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Modify Groovy route</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/processingTab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/processingMenu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            Modify Groovy route
                        </div>
                        <div class="blttext">
                            Modify or delete a Groovy routing rule.
                        </div>
                        <div class="table">
                            <%if (request.getAttribute("emessage") != null) {%>
                                <div class="systemerror">
                                    <div class="header">
                                        Problems encountered.
                                    </div>
                                    <div class="message">
                                        <%=request.getAttribute("emessage")%>
                                    </div>
                                </div>
                            <%}%>
                            <div class="modifyroute">
                                <form name="showRouteForm" action="groovyroute_show.htm">
                                    <div class="leftcol">
                                        <%
                                            List<String> adaptors = (List<String>)request.getAttribute("adaptors");
                                            String name = (String)request.getAttribute("name");
                                            String author = (String)request.getAttribute("author");
                                            Boolean active = (Boolean)request.getAttribute("active");
                                            String description = (String)request.getAttribute("description");
                                            List<String> recipients = (List<String>)request.getAttribute("recipients");
                                            String definition = (String)request.getAttribute("definition");
                                            String activestr = active==true?"checked":"";
                                        %>
                                        <div class="row">Name</div>
                                        <div class="row">Author</div>
                                        <div class="row">Active</div>
                                        <div class="row">Description</div>
                                        <div class="row4">Recipients</div>
                                        <div class="row">Script</div>
                                    </div>
                                    <div class="rightcol">
                                        <div class="row">
                                            <input type="text" name="name" value="<%=name%>" disabled/>
                                            <input type="hidden" name="name" value="<%=name%>"/>
                                            <div class="hint">
                                               Route name
                                            </div>
                                        </div>
                                        <div class="row">
                                            <input type="text" name="author" value="<%=author%>"/>
                                            <div class="hint">
                                               Route author's name
                                            </div>
                                        </div>
                                        <div class="row">
                                            <input type="checkbox" name="active" <%=activestr%>/>
                                            <div class="hint">
                                               Check to activate route
                                            </div>
                                        </div>
                                        <div class="row">
                                            <input type="text" name="description" value="<%=description%>"/>
                                            <div class="hint">
                                               Verbose description
                                            </div>
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
                                            <div class="hint">
                                               Select target adaptors
                                            </div>
                                        </div>
                                        <div class="textrow">
                                            <textarea class="routedefinition" name="typdef"><%=definition%></textarea>
                                            <div class="hint">
                                               Groovy route definition
                                            </div>
                                        </div>
                                    </div>
                                    <div class="tablefooter">
                                       <div class="buttons">
                                           <button class="rounded" name="submitButton" type="submit"
                                                   value="Modify">
                                               <span>Modify</span>
                                           </button>
                                           <button class="rounded" name="submitButton" type="submit"
                                                   value="Delete">
                                               <span>Delete</span>
                                           </button>
                                       </div>
                                   </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div id="bltfooter">
            <%@include file="/WEB-INF/jsp/footer.jsp"%>
        </div>
    </body>
</html>
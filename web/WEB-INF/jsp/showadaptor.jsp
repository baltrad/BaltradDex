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

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>BALTRAD | Modify adaptor</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/processing_tab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/processing_menu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            Modify adaptor
                        </div>
                        <div class="blttext">
                            Modify an adaptor. Depending on type of adaptor, different choices will
                            be available. However, right now, you are only able to choose XMLRPC.
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
                            <div class="modifyadaptor">
                                 <form name="showAdaptorForm" action="modifyadaptor.htm">
                                    <div class="leftcol">
                                        <%
                                            List<String> types = (List<String>)request.getAttribute("types");
                                            String uri = (String)request.getAttribute("uri");
                                            Long timeout = (Long)request.getAttribute("timeout");
                                            String timeoutstr = "";
                                            if (uri == null) {
                                              uri = "";
                                            }
                                            if (timeout != null) {
                                              timeoutstr = "" + timeout;
                                            }
                                        %>
                                        <div class="row">Name</div>
                                        <div class="row">Type</div>
                                        <div class="row">URI</div>
                                        <div class="row">Timeout</div>
                                    </div>
                                    <div class="rightcol">
                                        <div class="row">
                                            <div class="name">
                                                <input type="text" name="name"
                                                       value="<%=request.getAttribute("name")%>"
                                                       disabled/>
                                                <input type="hidden" name="name"
                                                       value="<%=request.getAttribute("name")%>"/>
                                                <div class="hint">
                                                   Adaptor name
                                                </div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="type">
                                                <select name="type">
                                                <%
                                                  for (String adtype : types) {
                                                %>
                                                    <option value="<%=adtype%>"><%=adtype%></option>
                                                <%
                                                  }
                                                %>
                                                </select>
                                                <div class="hint">
                                                   Select adaptor type
                                                </div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="uri">
                                                <input type="text" name="uri" value="<%=uri%>"/>
                                                <div class="hint">
                                                   Adaptor URI
                                                </div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="timeout">
                                                <input type="text" name="timeout" value="<%=timeoutstr%>"/>
                                                <div class="hint">
                                                   Timeout in milliseconds
                                                </div>
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
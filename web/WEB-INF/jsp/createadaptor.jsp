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
Create adaptors
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
        <title>Baltrad | Create adaptor</title>
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
                            Create adaptor
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        Create an adaptor. Depending on type of adaptor, different choices will
                        be available. However, right now, you are only able to choose XMLRPC.
                    </div>
                    <div id="table">
                        <div class="props">
                            <form name="createAdaptorForm" action="createadaptor.htm">
                              <%
                                List<String> types = (List<String>)request.getAttribute("types");
                                String name = (String)request.getAttribute("name");
                                String type = (String)request.getAttribute("type");
                                String uri = (String)request.getAttribute("uri");
                                Long timeout = (Long)request.getAttribute("timeout");
                                String timeoutstr = "5000";

                                name = (name == null)?"":name;
                                type = (type == null)?"":type;
                                uri = (uri == null)?"":uri;
                                if (timeout != null) {
                                  timeoutstr = "" + timeout;
                                }
                              %>

                              <div class="left">
                                  <div class="row">Name</div>
                                  <div class="row">Type</div>
                                  <div class="row">URI</div>
                                  <div class="row">Timeout</div>
                              </div>
                              <div class="right">
                                  <div class="row">
                                      <input size="50" type="text" name="name" value="<%=name%>"/>
                                  </div>
                                  <div class="row">
                                      <select name="type">
                                        <%
                                          for (String adtype : types) {
                                            String selected = "";
                                            if (adtype.equals(type)) {
                                              selected = "selected";
                                            }
                                        %>
                                            <option value="<%=adtype%>" <%=selected%>><%=adtype%>
                                            </option>
                                        <%
                                          }
                                        %>
                                      </select>
                                  </div>
                                  <div class="row">
                                      <input type="text" name="uri" value="<%=uri%>"/>
                                  </div>
                                  <div class="row">
                                      <input type="text" name="timeout" value="<%=timeoutstr%>"/>
                                  </div>
                              </div> 
                              <div class="footer">
                                    <div class="right">
                                        <button class="rounded" name="submitButton" type="submit" value="Add">
                                            <span>Add</span>
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                      </div>
                      <%if (request.getAttribute("emessage") != null) {%>
                          <div class="beast-error"><%=request.getAttribute("emessage")%></div>
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

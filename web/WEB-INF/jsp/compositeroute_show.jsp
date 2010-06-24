<%--
Copyright (C) 2009-2010 Swedish Meteorological and Hydrological Institute, SMHI,

This file is part of the BaltradDex.

BaltradDex is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BaltradDex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex.  If not, see <http://www.gnu.org/licenses/>.
-------------------------------------------------------------------
Creates a composite route
@date 2010-05-13
@author Anders Henja
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<style type="text/css">
.form-content {
  width:98%;
  padding: 6px;
  margin: 2px;
  background-color: #CACACA;
}

.form-content ul {
  padding:2px;
  margin: 2px;
  list-style:none;
  overflow: auto;
}

.form-content ul li {
  padding:2px;
  margin: 2px;
}

.form-content ul li span {
  float:left;
  width:5em;
  text-align:left;	
  padding-right:30px;
  font-weight:bold;
}

.routererror {
  font-weight:bold;
  color:#c80000;
}

.routedefinition {
  font-size:100%;
  float:left;
  width:100%;
  height:400px;
}

</style>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Create route</title>
</head>

<body>
    <div id="container">
        <div id="header"></div>
        <div id="nav">
            <script type="text/javascript" src="includes/navigation.js"></script>
        </div>
        <div class="outer">
            <div class="inner">
                <div class="float-wrap">
                    <div id="main">
                        <h1>Create route</h1>
                          <br/>
                          <h2>
                            Modify/delete a Composite routing rule.
                          </h2>
                          <div class="form-content">
                            <form name="showRouteForm" action="compositeroute_show.htm">
                              <%
                                List<String> adaptors = (List<String>)request.getAttribute("adaptors");
                                List<String> sourceids = (List<String>)request.getAttribute("sourceids");
                                List<Integer> intervals = (List<Integer>)request.getAttribute("intervals");

                                String name = (String)request.getAttribute("name");
                                String author = (String)request.getAttribute("author");
                                Boolean active = (Boolean)request.getAttribute("active");
                                String description = (String)request.getAttribute("description");
                                List<String> recipients = (List<String>)request.getAttribute("recipients");
                                String areaid = (String)request.getAttribute("areaid");
                                Integer interval = (Integer)request.getAttribute("interval");
                                Integer timeout = (Integer)request.getAttribute("timeout");
                                List<String> sources = (List<String>)request.getAttribute("sources");

                                String activestr = (active == true)?"checked":"";
                              %>
                              <ul>
                                <li><span>Name:</span> <input size="50" type="text" name="name" value="<%=name%>" disabled/></li>
                                <input type="hidden" name="name" value="<%=name%>"/>
                                <li><span>Author:</span> <input size="50" type="text" name="author" value="<%=author%>"/></li>
                                <li><span>Active:</span> <input type="checkbox" name="active" <%=activestr%>/></li>
                                <li><span>Description:</span> <input size="50" type="text" name="description" value="<%=description%>"/></li>
                                <li><span>Recipients:</span><select multiple size="4" name="recipients">
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
                                </select></li>
                                <li><span>Areaid:</span> <input size="50" type="text" name="areaid" value="<%=areaid%>"/></li>
                                <li><span>Interval:</span> <select name="interval">
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
                                </select></li>
                                <li><span>Timeout:</span> <input size="15" type="text" name="timeout" value="<%=timeout%>" disabled/></li>
                                <li><span>Sources:</span><select multiple size="6" name="sources">
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
                                </select></li>
                              </ul>
                              <div id="table-footer">
                                <input type="submit" value="Modify" name="submitButton"/>
                                <input type="submit" value="Delete" name="submitButton"/>
                              </div>
                            </form>
                          </div>
                          <%if (request.getAttribute("emessage") != null) {%>
                            <span class="routererror"><%=request.getAttribute("emessage")%></span>
                          <%}%>
                    </div>
                    <div id="left">
                        <%@ include file="/WEB-INF/jsp/mainMenu.jsp"%>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
    </div>
    <div id="footer">
        <script type="text/javascript" src="includes/footer.js"></script>
    </div>
</body>
</html>

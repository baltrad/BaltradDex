<%--
Copyright (C) 2009-2010 Swedish Meteorological and Hydrological Institute, SMHI,

This file is part of the Beast library.

Beast library is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Beast library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the Beast library library.  If not, see <http://www.gnu.org/licenses/>.
-------------------------------------------------------------------
List of adaptors
@date 2010-03-23
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
  margin:2px;
  background-color: #CACACA;
}

.form-content ul {
  padding:2px;
  margin:2px;
  list-style:none;
  overflow:auto;
}

.form-content ul li {
  padding:2px;
  margin:2px;
}

.form-content ul span {
  float:left;
  width:5em;
  text-align:left;	
  padding-right:30px;
  font-weight:bold;
}

.adaptorerror {
  font-weight:bold;
  color:#c80000;
};

</style>


<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Modify adaptor</title>
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
                        <h1>Modify adaptor</h1>
                          <br/>
                          <h2>
                            Modify an adaptor. Depending on type of adaptor, different choices will
                            be available. However, right now, you are only able to choose XMLRPC.
                          </h2>
                          <div class="form-content">
                            <form name="showAdaptorForm" action="modifyadaptor.htm">
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
                              <ul>
                                <li><span>Name:</span> <input size="50" type="text" name="name" value="<%=request.getAttribute("name")%>" disabled/></li>
                                <input type="hidden" name="name" value="<%=request.getAttribute("name")%>"/>
                                <li><span>Type:</span> <select name="type">
                                <%
                                  for (String adtype : types) {
                                %>
                                    <option value="<%=adtype%>"><%=adtype%></option>
                                <%
                                  }
                                %>
                                </select></li>
                                <li><span>URI:</span> <input size="50" type="text" name="uri" value="<%=uri%>"/></li>
                                <li><span>Timeout:</span> <input size="10" type="text" name="timeout" value="<%=timeoutstr%>"/></li>
                              </ul>
                              <div id="table-footer">
                                <input type="submit" value="Modify" name="submitButton"/>
                                <input type="submit" value="Delete" name="submitButton"/>
                              </div>
                            </form>
                          </div>
                          <%if (request.getAttribute("emessage") != null) {%>
                            <span class="adaptorerror"><%=request.getAttribute("emessage")%></span>
                          <%}%>
                    </div>
                    <div id="left">
                        <script type="text/javascript" src="includes/mainmenu.js"></script>
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
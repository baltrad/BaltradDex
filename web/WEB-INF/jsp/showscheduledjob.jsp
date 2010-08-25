<%--
Copyright (C) 2009-2010 Swedish Meteorological and Hydrological Institute, SMHI,

This file is part of the baltrad dex.

baltrad dex is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

baltrad dex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the baltrad dex library.  If not, see <http://www.gnu.org/licenses/>.
-------------------------------------------------------------------
Shows a scheduled job
@date 2010-08-23
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
  margin:2px;
  list-style:none;
  overflow:auto;
}

.form-content ul li {
  padding:2px;
  margin:2px;
}

.form-content ul li span {
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
    <title>Create adaptor</title>
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
                        <h1>Modify/delete scheduled job</h1>
                          <br/>
                          <h2>
                            Modify/delete scheduled job.
                          </h2>
                          <div class="form-content">
                            <form name="showScheduledJobForm" action="showscheduledjob.htm">
                              <%
                                Integer iid = (Integer)request.getAttribute("id");
                                List<String> jobnames = (List<String>)request.getAttribute("jobnames");
                                String expression = (String)request.getAttribute("expression");
                                String jobname = (String)request.getAttribute("jobname");

                                int id = (iid == null)?0:iid.intValue();
                                jobnames = (jobnames == null)?new ArrayList<String>():jobnames;
                                expression = (expression == null)?"":expression;
                                jobname = (jobname == null)?"":jobname;
                              %>
                              <ul>
                                <li><span>Expression:</span> <input size="50" type="text" name="expression" value="<%=expression%>"/></li>
                                <li><span>Jobname:</span> <select name="jobname">
                                <%
                                  for (String job : jobnames) {
                                    String selected = "";
                                    if (job.equals(jobname)) {
                                      selected = "selected";
                                    }
                                %>
                                    <option value="<%=job%>" <%=selected%>><%=job%></option>
                                <%
                                  }
                                %>
                                </select></li>
                                <input type="hidden" value="<%=id%>" name="id"/>
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

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
Shows a scheduled job
@date 2010-08-23
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
        <title>Baltrad | Modify or delete scheduled job</title>
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
                            Modify or delete scheduled job
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        Modify or delete a scheduled job.
                    </div>
                    <div id="table">
                        <div class="props">
                            <form name="showScheduledJobForm" action="showscheduledjob.htm">
                                <div class="left">
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
                                    <div class="row">Expression</div>
                                    <div class="row">Job name</div>
                                </div>
                                <div class="right">
                                    <div class="row">
                                        <input type="text" name="expression" value="<%=expression%>"/>
                                    </div>
                                    <div class="row">
                                        <select name="jobname">
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
                                        </select>
                                        <input type="hidden" value="<%=id%>" name="id"/>
                                    </div>
                                </div>
                                <div class="footer">
                                    <div class="right">
                                        <button class="rounded" name="submitButton" type="submit" value="Modify">
                                            <span>Modify</span>
                                        </button>
                                        <button class="rounded" name="submitButton" type="submit" value="Delete">
                                            <span>Delete</span>
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                     <%if (request.getAttribute("emessage") != null) {%>
                            <div class="adaptorerror"><%=request.getAttribute("emessage")%></div>
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

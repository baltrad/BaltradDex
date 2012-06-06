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
<%@page import="eu.baltrad.beastui.web.pojo.CronEntryMapping"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>BALTRAD | Modify job</title>
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
                            Modify job
                        </div>
                        <div class="blttext">
                            Modify or delete a scheduled job.
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
                            <div class="modifyjob">
                                <form name="showScheduledJobForm" action="showscheduledjob.htm">
                                    <div class="leftcol">
                                        <%
                                          Integer iid = (Integer)request.getAttribute("id");
                                          List<String> jobnames = (List<String>)request.getAttribute("jobnames");
                                          List<CronEntryMapping> selectableSeconds = (List<CronEntryMapping>)request.getAttribute("selectableSeconds");
                                          List<CronEntryMapping> selectableMinutes = (List<CronEntryMapping>)request.getAttribute("selectableMinutes");
                                          List<CronEntryMapping> selectableHours = (List<CronEntryMapping>)request.getAttribute("selectableHours");
                                          List<CronEntryMapping> selectableDaysOfMonth = (List<CronEntryMapping>)request.getAttribute("selectableDaysOfMonth");
                                          List<CronEntryMapping> selectableMonths = (List<CronEntryMapping>)request.getAttribute("selectableMonths");
                                          List<CronEntryMapping> selectableDaysOfWeek = (List<CronEntryMapping>)request.getAttribute("selectableDaysOfWeek");
                                          List<String> seconds = (List<String>)request.getAttribute("seconds");
                                          List<String> minutes = (List<String>)request.getAttribute("minutes");
                                          List<String> hours = (List<String>)request.getAttribute("hours");
                                          List<String> daysOfMonth = (List<String>)request.getAttribute("daysOfMonth");
                                          List<String> months = (List<String>)request.getAttribute("months");
                                          List<String> daysOfWeek = (List<String>)request.getAttribute("daysOfWeek");
                                          String jobname = (String)request.getAttribute("jobname");

                                          int id = (iid == null)?0:iid.intValue();
                                          jobnames = (jobnames == null)?new ArrayList<String>():jobnames;
                                          jobname = (jobname == null)?"":jobname;
                                        %>
                                        <div class="row4">Seconds</div>
                                        <div class="row4">Minutes</div>
                                        <div class="row4">Hours</div>
                                        <div class="row4">Days of month</div>
                                        <div class="row4">Months</div>
                                        <div class="row4">Days of week</div>
                                        <div class="row">Job name</div>
                                    </div>
                                    <div class="rightcol">
                                        <div class="row4">
                                            <select multiple size="4" name="seconds">
                                              <%
                                              for (CronEntryMapping entry : selectableSeconds) {
                                                String selectstr = "";
                                                if (seconds.contains(entry.getValue())) {
                                                  selectstr = "selected";
                                                }
                                              %>
                                              <option value="<%=entry.getValue()%>" <%=selectstr%>><%=entry.getName()%></option>
                                              <%
                                              }
                                              %>
                                            </select>
                                            <div class="hint">
                                               Seconds
                                            </div>
                                          </div>
                                          <div class="row4">
                                            <select multiple size="4" name="minutes">
                                              <%
                                              for (CronEntryMapping entry : selectableMinutes) {
                                                String selectstr = "";
                                                if (minutes.contains(entry.getValue())) {
                                                  selectstr = "selected";
                                                }
                                              %>
                                              <option value="<%=entry.getValue()%>" <%=selectstr%>><%=entry.getName()%></option>
                                              <%
                                              }
                                              %>
                                            </select>
                                            <div class="hint">
                                               Minutes
                                            </div>
                                          </div>
                                          <div class="row4">
                                            <select multiple size="4" name="hours">
                                              <%
                                              for (CronEntryMapping entry : selectableHours) {
                                                String selectstr = "";
                                                if (hours.contains(entry.getValue())) {
                                                  selectstr = "selected";
                                                }
                                              %>
                                              <option value="<%=entry.getValue()%>" <%=selectstr%>><%=entry.getName()%></option>
                                              <%
                                              }
                                              %>
                                            </select>
                                            <div class="hint">
                                               Hours
                                            </div>
                                          </div>
                                          <div class="row4">
                                            <select multiple size="4" name="daysOfMonth">
                                              <%
                                              for (CronEntryMapping entry : selectableDaysOfMonth) {
                                                String selectstr = "";
                                                if (daysOfMonth.contains(entry.getValue())) {
                                                  selectstr = "selected";
                                                }
                                              %>
                                              <option value="<%=entry.getValue()%>" <%=selectstr%>><%=entry.getName()%></option>
                                              <%
                                              }
                                              %>
                                            </select>
                                            <div class="hint">
                                               Days of month
                                            </div>
                                          </div>
                                          <div class="row4">
                                            <select multiple size="4" name="months">
                                              <%
                                              for (CronEntryMapping entry : selectableMonths) {
                                                String selectstr = "";
                                                if (months.contains(entry.getValue())) {
                                                  selectstr = "selected";
                                                }
                                              %>
                                              <option value="<%=entry.getValue()%>" <%=selectstr%>><%=entry.getName()%></option>
                                              <%
                                              }
                                              %>
                                            </select>
                                            <div class="hint">
                                               Months
                                            </div>
                                          </div>
                                          <div class="row4">
                                            <select multiple size="4" name="daysOfWeek">
                                              <%
                                              for (CronEntryMapping entry : selectableDaysOfWeek) {
                                                String selectstr = "";
                                                if (daysOfWeek.contains(entry.getValue())) {
                                                  selectstr = "selected";
                                                }
                                              %>
                                              <option value="<%=entry.getValue()%>" <%=selectstr%>><%=entry.getName()%></option>
                                              <%
                                              }
                                              %>
                                            </select>
                                            <div class="hint">
                                               Days of week
                                            </div>
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
                                            <div class="hint">
                                               Select a job to execute
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
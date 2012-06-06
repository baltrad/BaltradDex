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
Create anomaly detectors
@date 2011-09-22
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
        <title>BALTRAD | Create quality control</title>
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
                            Create quality control
                        </div>
                        <div class="blttext">
                            Create quality control string. The string is a PGF-specific value and
                            must be supported by the targeted PGF. Add a describing text so that it is
                            possible to see what the control is supposed to be doing.
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
                            <div class="create_anomaly_detector">
                                 <form name="createAnomalyDetectorForm" action="create_anomaly_detector.htm">
                                    <div class="leftcol">
                                        <%
                                            String name = (String)request.getAttribute("name");
                                            String description = (String)request.getAttribute("description");
                                            name = (name == null)?"":name;
                                            description = (description == null)?"":description;
                                        %>
                                        <div class="row">Name</div>
                                        <div class="row">Description</div>
                                    </div>
                                    <div class="rightcol">
                                        <div class="row">
                                            <div class="name">
                                                <input type="text" name="name" value="<%=name%>"/>
                                                <div class="hint">
                                                   Quality controls name, valid characters are [A-Za-z0-9_.-]+
                                                </div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="description">
                                                <input type="text" name="description" value="<%=description%>"/>
                                                <div class="hint">
                                                   Description of the quality control
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="tablefooter">
                                       <div class="buttons">
                                           <button class="rounded" name="submitButton" type="submit"
                                                   value="Add">
                                               <span>Add</span>
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
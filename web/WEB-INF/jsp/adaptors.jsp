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
<%@ page import="java.util.List" %>
<%
    // Check if there are adaptors available to display
    List adaptors = ( List )request.getAttribute( "adaptors" );
    if( adaptors == null || adaptors.size() <= 0 ) {
        request.getSession().setAttribute( "adaptors_status", 0 );
    } else {
        request.getSession().setAttribute( "adaptors_status", 1 );
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>BALTRAD | Adaptors</title>
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
                            Adaptors
                        </div>
                        <div class="blttext">
                            List of adaptors. Click on adaptor name to modify or delete or click
                            Create to create a new adaptor.
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
                            <form name="createAdaptorForm" action="createadaptor.htm">
                                <c:choose>
                                    <c:when test="${adaptors_status == 1}">
                                        <div class="adaptors">
                                            <div class="tableheader">
                                                <div id="cell" class="count">&nbsp;</div>
                                                <div id="cell" class="name">
                                                    Name
                                                </div>
                                                <div id="cell" class="type">
                                                    Type
                                                </div>
                                            </div>
                                            <c:set var="count" scope="page" value="1"/>
                                            <c:forEach var="adaptor" items="${adaptors}">
                                                <div class="entry">
                                                    <div id="cell" class="count">
                                                        <c:out value="${count}"/>
                                                        <c:set var="count" value="${count + 1}"/>
                                                    </div>
                                                    <div id="cell" class="name">
                                                        <a href="showadaptor.htm?name=${adaptor.name}">
                                                            <c:out value="${adaptor.name}"/>
                                                        </a>
                                                    </div>
                                                    <div id="cell" class="type">
                                                        <c:out value="${adaptor.type}"/>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                </c:choose>
                                <div class="tablefooter">
                                   <div class="buttons">
                                       <button class="rounded" type="button"
                                           onclick="window.location.href='processing.htm'">
                                           <span>Back</span>
                                       </button>
                                       <button class="rounded" type="submit">
                                           <span>Create</span>
                                       </button>
                                   </div>
                               </div>
                            </form>
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
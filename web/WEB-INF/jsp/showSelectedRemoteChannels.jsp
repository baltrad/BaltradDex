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
Document   : Remote data channel subscription page
Created on : Aug 2, 2010, 2:46:32 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ page import="java.util.List" %>
<%
    // Get remote node name
    String remoteNodeName = ( String )request.getAttribute( "sender_node_name" );
    // Check selection list is not empty
    List selChannels = ( List )request.getAttribute( "selected_channels" );
    // Subscription status is not changed
    if( selChannels == null || selChannels.size() <= 0 ) {
        request.getSession().setAttribute( "selection_status", 0 );
    } else {
        request.getSession().setAttribute( "selection_status", 1 );
    }
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Remote channel selection</title>
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
                        <h1>Remote channel selection</h1>
                        <br/>
                        <div id="message-box">
                            <h2>
                            You are connected to <% out.println( remoteNodeName );%>
                            </h2>
                        </div>
                        <c:choose>
                            <c:when test="${selection_status == 1}">
                                <h2>
                            <p>
                            Confirm your selection by clicking OK.
                            </p>
                            </h2>
                            <form action="showRemoteChannelSelectionStatus.htm">
                                <display:table name="selected_channels" id="channel" defaultsort="1"
                                    requestURI="showSelectedRemoteChannels.htm" cellpadding="0"
                                    cellspacing="2" export="false" class="tableborder">
                                    <display:column sortProperty="id" sortable="true"
                                        title="Channel ID" class="tdcenter">
                                        <fmt:formatNumber value="${channel.id}"
                                        pattern="00" />
                                    </display:column>
                                    <display:column sortable="true" title="Channel WMO number"
                                        sortProperty="wmoNumber" paramId="wmoNumber"
                                        paramProperty="wmoNumber"
                                        class="tdcenter" value="${channel.wmoNumber}">
                                    </display:column>
                                    <display:column sortable="true" title="Channel name"
                                        sortProperty="channelName" paramId="channelName"
                                        paramProperty="channelName" class="tdcenter"
                                        value="${channel.channelName}">
                                    </display:column>
                                </display:table>
                                <div id="table-footer-leftcol">
                                    <input type="submit" value="Submit" name="submit_button"/>
                                </div>
                            </form>
                            <div id="table-footer-rightcol">
                                <form action="connectToNode.htm">
                                    <input type="submit" value="Cancel" name="cancel_button"/>
                                </form>
                            </div>
                            </c:when>
                            <c:otherwise>
                                <div id="message-box">
                                    No channels have been selected.
                                    Click on OK button to go back to node connection page.
                                </div>
                                <div id="table-footer">
                                    <div id="table-footer-rightcol">
                                        <form action="connectToNode.htm">
                                            <input type="submit" value="OK" name="cancel_button"/>
                                        </form>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
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

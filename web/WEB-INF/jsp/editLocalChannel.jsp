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
Document   : Edit data channel page
Created on : Jun 22, 2010, 11:57:02 AM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Modify local data channel</title>
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
                        <h1>Modify local data channel</h1>
                        <br/>
                        <h2>
                            <p>
                            Click on data channel ID to modify selected channel.
                            </p>
                        </h2>
                        <display:table name="registered_channels" id="channel" defaultsort="1"
                            cellpadding="0" cellspacing="2" export="false" class="tableborder"
                            pagesize="10">
                            <display:column sortable="true" title="ID" href="saveLocalChannel.htm"
                                sortProperty="id" class="tdcenter" paramProperty="id"
                                paramId="id" value="${channel.id}">
                            </display:column>
                            <display:column sortable="true" title="Channel name" 
                                sortProperty="channelName" class="tdcenter"
                                value="${channel.channelName}">
                            </display:column>
                            <display:column sortable="true" title="WMO number"
                                sortProperty="wmoNumber" class="tdcenter"
                                value="${channel.wmoNumber}">
                            </display:column>
                        </display:table>
                        <div id="table-footer">
                            <form action="adminControls.htm">
                                <input type="submit" value="Back" name="admin_button"/>
                            </form>
                        </div>
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

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
Document   : Page displaying a list of user accounts
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
    <title>Remove user account</title>
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
                        <h1>Remove user account</h1>
                        <br/>
                        <h2>
                            <p>
                            Select user account to remove.
                            </p>
                        </h2>
                        <form action="showSelectedUsers.htm">
                            <display:table name="users" id="user" defaultsort="1"
                                requestURI="showUsers.htm" cellpadding="0" cellspacing="2"
                                export="false" class="tableborder" pagesize="10">
                                <display:column sortProperty="id" sortable="true"
                                    title="ID" class="tdcenter">
                                    <fmt:formatNumber value="${user.id}" pattern="00" />
                                </display:column>
                                <display:column sortable="true" title="User name"
                                    sortProperty="name" class="tdcenter"
                                    value="${user.name}">
                                </display:column>
                                <display:column sortable="true" title="Role" sortProperty="role"
                                    class="tdcenter" value="${user.roleName}">
                                </display:column>
                                <display:column sortable="true" title="Node address"
                                    sortProperty="nodeAddress" class="tdcenter"
                                    value="${user.nodeAddress}">
                                </display:column>
                                <display:column sortable="true" title="Company"
                                    sortProperty="factory" class="tdcenter"
                                    value="${user.factory}">
                                </display:column>
                                <display:column sortable="false" title="Select" class="tdcheck">
                                    <input type="checkbox" name="selected_users"
                                        value="${user.id}"/>
                                </display:column>
                            </display:table>
                            <div id="table-footer-rightcol">
                                <input type="submit" value="Submit" name="submitButton"/>
                            </div>
                        </form>
                        <form action="adminControl.htm">
                            <div id="table-footer-leftcol">
                                <input type="submit" value="Back" name="cancelButton"/>
                            </div>
                        </form>

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

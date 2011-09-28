<%--------------------------------------------------------------------------------------------------
Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW

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
Document   : Save user account page
Created on : Oct 4, 2010, 2:42 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<%
    String userId = request.getParameter( "userId" );
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Save user account</title>
    </head>
    <body>
        <div id="bltcontainer">
            <div id="bltheader">
                <script type="text/javascript" src="includes/js/header.js"></script>
            </div>
            <div id="bltmain">
                <div id="tabs">
                    <%@include file="/WEB-INF/jsp/settingsTab.jsp"%>
                </div>
                <div id="tabcontent">
                    <div class="left">
                        <%@include file="/WEB-INF/jsp/settingsMenu.jsp"%>
                    </div>
                    <div class="right">
                        <div class="blttitle">
                            Save user account
                        </div>
                        <div class="blttext">
                            Save new user account or modify an existing one.
                        </div>
                        <div class="table">
                            <c:set var="editMode" scope="page" value="<%=userId%>"/>
                            <div class="addaccount">
                                <%@include file="/WEB-INF/jsp/formMessages.jsp"%>
                                <form method="post">
                                    <div class="leftcol">
                                        <div class="row">User name</div>
                                        <c:if test="${editMode == null}">
                                            <div class="row">Password</div>
                                            <div class="row">Confirm password</div>
                                        </c:if>
                                        <div class="row">Role</div>
                                        <div class="row">Node address</div>
                                        <div class="row">Organization</div>
                                        <div class="row">Country</div>
                                        <div class="row">City</div>
                                        <div class="row">Zip code</div>
                                        <div class="row">Address / Street</div>
                                        <div class="row">Address / Number</div>
                                        <div class="row">Phone</div>
                                        <div class="row">Email</div>
                                    </div>
                                    <div class="rightcol">
                                        <div class="row">
                                            <div class="username">
                                                <form:input path="command.name"
                                                    title="Enter unique user name"/>
                                                <div class="hint">
                                                   Unique user name
                                                </div>
                                            </div>
                                            <form:errors path="command.name" cssClass="error"/>
                                        </div>
                                        <c:if test="${editMode == null}">
                                            <div class="row">
                                                <div class="password">
                                                    <form:password path="command.password"
                                                        title="Enter password"/>
                                                    <div class="hint">
                                                       Password
                                                    </div>
                                                </div>
                                                <form:errors path="command.password"
                                                             cssClass="error"/>
                                            </div>
                                            <div class="row">
                                                <div class="password">
                                                    <form:password path="command.confirmPassword"
                                                        title="Repeat password"/>
                                                    <div class="hint">
                                                       Retype password here
                                                    </div>
                                                </div>
                                                <form:errors path="command.confirmPassword"
                                                             cssClass="error"/>
                                            </div>
                                        </c:if>
                                        <div class="row">
                                            <div class="rolename">
                                                <spring:bind path="command.roleName">
                                                    <select name='<c:out value="${status.expression}"/>'
                                                            title="Select user's role">
                                                        <c:forEach items="${roles}" var="role">
                                                            <option value='<c:out value="${role.role}"/>'
                                                                <c:if test="${role.role == status.value}">
                                                                    SELECTED</c:if>>
                                                                    <c:out value="${role.role}"/>
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                </spring:bind>
                                                <div class="hint">
                                                   Select role to define permission level
                                                </div>
                                            </div>
                                            <form:errors path="command.roleName" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="protocol-prefix">https://</div>
                                            <div class="shortaddress">
                                                <form:input path="command.hostAddress"
                                                    title="Enter host address"/>
                                                <div class="hint">
                                                    Host address, e.g. baltrad.org
                                                </div>
                                            </div>
                                            <div class="port-separator">:</div>
                                            <div class="portnumber">
                                                <form:input path="command.port"
                                                    title="Enter port number"/>
                                                <div class="hint">
                                                    Port number
                                                </div>
                                            </div>
                                            <form:errors path="command.hostAddress" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="orgname">
                                                <form:input path="command.factory"
                                                    title="Enter organization name"/>
                                                <div class="hint">
                                                    Name of user's organization
                                                </div>
                                            </div>
                                            <form:errors path="command.factory" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="country">
                                                <form:input path="command.country"
                                                    title="Enter country name"/>
                                                <div class="hint">
                                                    Country name
                                                </div>
                                            </div>
                                            <form:errors path="command.country" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="city">
                                                <form:input path="command.city"
                                                    title="Enter city name"/>
                                                <div class="hint">
                                                    City name
                                                </div>
                                            </div>
                                            <form:errors path="command.city" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="zipcode">
                                                <form:input path="command.cityCode"
                                                    title="Enter zip code"/>
                                                <div class="hint">
                                                    Zip code
                                                </div>
                                            </div>
                                            <form:errors path="command.cityCode" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="street">
                                                <form:input path="command.street"
                                                    title="Enter street name"/>
                                                <div class="hint">
                                                    Street name
                                                </div>
                                            </div>
                                            <form:errors path="command.street" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="streetno">
                                                <form:input path="command.number"
                                                    title="Enter address number"/>
                                                <div class="hint">
                                                    Address number
                                                </div>
                                            </div>
                                            <form:errors path="command.number" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="phone">
                                                <form:input path="command.phone"
                                                    title="Enter phone number"/>
                                                <div class="hint">
                                                    Contact phone number
                                                </div>
                                            </div>
                                            <form:errors path="command.phone" cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="email">
                                                <form:input path="command.email"
                                                    title="Enter email address"/>
                                                <div class="hint">
                                                    Email address
                                                </div>
                                            </div>
                                            <form:errors path="command.email" cssClass="error"/>
                                        </div>
                                    </div>
                                    <div class="tablefooter">
                                       <div class="buttons">
                                           <button class="rounded" type="button"
                                               onclick="window.location.href='settings.htm'">
                                               <span>Back</span>
                                           </button>
                                           <button class="rounded" type="submit">
                                               <span>Save</span>
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
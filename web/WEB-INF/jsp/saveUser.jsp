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
Document   : Save user account
Created on : Oct 4, 2010, 2:42 PM
Author     : szewczenko
--------------------------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
        <title>Baltrad | Save account</title>
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
                            Save user account
                        </div>
                        <div class="right">
                        </div>
                    </div>
                    <div id="text-box">
                        Create new user account or modify existing account.
                    </div>
                    <div id="table">
                        <div class="props">
                            <form method="post">
                                <div class="left">
                                    <div class="row">User name</div>
                                    <div class="row">Password</div>
                                    <div class="row">Retype password</div>
                                    <div class="row">Role</div>
                                    <div class="row">Node HTTP address</div>
                                    <div class="row">Organization</div>
                                    <div class="row">Country</div>
                                    <div class="row">City</div>
                                    <div class="row">Postcode</div>
                                    <div class="row">Street name</div>
                                    <div class="row">Street number</div>
                                    <div class="row">Phone</div>
                                    <div class="row">Email address</div>
                                </div>
                                <div class="right">
                                    <div class="row">
                                        <form:input path="command.name"/>
                                        <form:errors path="command.name" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:password path="command.password"/>
                                        <form:errors path="command.password" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:password path="command.retPassword"/>
                                        <form:errors path="command.retPassword" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <spring:bind path="command.roleName">
                                        <select name='<c:out value="${status.expression}"/>'>
                                            <c:forEach items="${roles}" var="role">
						<option value='<c:out value="${role.role}"/>'
                                                    <c:if test="${role.role == status.value}">
                                                        SELECTED</c:if>>
                                                        <c:out value="${role.role}"/></option>
                                            </c:forEach>
					</select>
                                        </spring:bind>
                                        <form:errors path="command.roleName" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.nodeAddress"/>
                                        <form:errors path="command.nodeAddress" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.factory"/>
                                        <form:errors path="command.factory" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.country"/>
                                        <form:errors path="command.country" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.city"/>
                                        <form:errors path="command.city" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.cityCode"/>
                                        <form:errors path="command.cityCode" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.street"/>
                                        <form:errors path="command.street" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.number"/>
                                        <form:errors path="command.number" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.phone"/>
                                        <form:errors path="command.phone" cssClass="errors"/>
                                    </div>
                                    <div class="row">
                                        <form:input path="command.email"/>
                                        <form:errors path="command.email" cssClass="errors"/>
                                    </div>
                                </div>
                                <div class="footer">
                                    <div class="right">
                                        <button class="rounded" type="button"
                                            onclick="history.go(-1);">
                                            <span>Back</span>
                                        </button>
                                        <button class="rounded" type="submit">
                                            <span>Submit</span>
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                <div id="clear"></div>
            </div>
        </div>
        <div id="footer">
            <script type="text/javascript" src="includes/footer.js"></script>
        </div>
    </body>
</html>

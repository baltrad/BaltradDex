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
        <title>BALTRAD | Save user account</title>
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
                        <div class="blttext">
                            <c:if test="${command.roleName == 'peer'}">
                                Note: Peer account is read-only.
                            </c:if>
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
                                        <c:if test="${command.roleName != 'peer'}">
                                            <div class="row">Organization name</div>
                                            <div class="row">Unit name</div>
                                            <div class="row">Locality name (City)</div>
                                            <div class="row">State name (Country)</div>
                                            <div class="row">Country code</div>
                                        </c:if>
                                        <div class="row">Node address</div>
                                    </div>
                                    <div class="rightcol">
                                        <div class="row">
                                            <div class="username">
                                                <c:choose>
                                                    <c:when test="${command.roleName != 'peer'}">
                                                        <form:input path="command.name"
                                                            title="Enter unique user name"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" value="${command.name}"
                                                               readonly="true">
                                                    </c:otherwise>
                                                </c:choose>
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
                                                <c:choose>
                                                    <c:when test="${command.roleName != 'peer'}">
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
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" 
                                                               value="${command.roleName}"
                                                               readonly="true">
                                                    </c:otherwise>
                                                </c:choose>
                                                <div class="hint">
                                                   User role
                                                </div>
                                            </div>
                                            <form:errors path="command.roleName" cssClass="error"/>
                                        </div>
                                        <c:if test="${command.roleName != 'peer'}">
                                                         
                                        <div class="row">
                                            <div class="orgname">
                                                
                                                    <form:input path="command.organizationName"
                                                        title="Enter organization name"/>
                                                    <div class="hint">
                                                        Name of organization
                                                    </div>
                                            </div>
                                            <form:errors path="command.organizationName" 
                                                         cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="orgname">
                                                
                                                    <form:input path="command.organizationUnit"
                                                        title="Enter unit name"/>
                                                    <div class="hint">
                                                        Unit name, e.g. Forecast Department
                                                    </div>

                                            </div>
                                            <form:errors path="command.organizationUnit" 
                                                         cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="city">
                                               
                                                    <form:input path="command.localityName"
                                                        title="Enter locality name (City)"/>
                                                    <div class="hint">
                                                        Locality name (City)
                                                    </div>
                                                
                                            </div>
                                            <form:errors path="command.localityName" 
                                                         cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="country">
                                               
                                                    <form:input path="command.stateName"
                                                        title="Enter state name (Country)"/>
                                                    <div class="hint">
                                                        State name (Country)
                                                    </div>
                                                
                                            </div>
                                            <form:errors path="command.stateName" 
                                                         cssClass="error"/>
                                        </div>
                                        <div class="row">
                                            <div class="zipcode">
                                                
                                                    <form:input path="command.countryCode"
                                                        title="Enter two-letter country code"/>
                                                    <div class="hint">
                                                        Two-letter country code
                                                    </div>
                                                
                                            </div>
                                            <form:errors path="command.countryCode" 
                                                         cssClass="error"/>
                                        </div>
                                        </c:if>          
                                        <div class="row">
                                            <div class="nodeaddress">
                                                <c:choose>
                                                    <c:when test="${command.roleName != 'peer'}">
                                                        <form:input path="command.nodeAddress"
                                                            title="Enter user node's address"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="text" 
                                                               value="${command.nodeAddress}"
                                                               readonly="true">
                                                    </c:otherwise>
                                                </c:choose>
                                                <div class="hint">
                                                    Node address, 
                                                    e.g. http://baltrad.eu:8084
                                                </div>
                                            </div>
                                            <form:errors path="command.nodeAddress" cssClass="error"/>
                                        </div>                    
                                    </div>
                                    <div class="tablefooter">
                                       <div class="buttons">
                                           <button class="rounded" type="button"
                                               onclick="window.location.href='settings.htm'">
                                               <span>Back</span>
                                           </button>
                                           <c:choose>
                                                <c:when test="${command.roleName == 'peer'}">
                                                    <button class="rounded" type="button"
                                                            onclick="window.location.href='editAccount.htm'">
                                                        <span>OK</span>
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button class="rounded" type="submit">
                                                        <span>Save</span>
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
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
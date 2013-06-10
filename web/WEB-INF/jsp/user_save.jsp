<%------------------------------------------------------------------------------
Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW

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
--------------------------------------------------------------------------------
Document   : User account configuration apge
Created on : May 23, 2013, 8:30 AM
Author     : szewczenko
------------------------------------------------------------------------------%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<t:generic_page pageTitle="Save user account">
    <jsp:body>
        <div class="user-save">
            <div class="table">
                <c:choose>
                    <c:when test="${user_account.role == 'peer'}">
                        <div class="header">
                            <div class="row">Peer account details</div>
                        </div>
                        <div class="header-text">
                            Peer account settings are read-only.  
                        </div>
                        <form:form method="POST" commandName="user_account">
                            <div class="body">
                                <div class="row">
                                    <div class="leftcol">
                                        User name:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="name" 
                                                    title="User name"
                                                    readonly="true"/>
                                        <form:errors path="name" 
                                                     cssClass="error"/>   
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Role:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="role"
                                                    title="User role"
                                                    readonly="true"/>  
                                        <form:errors path="role" 
                                                     cssClass="error"/>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Node address (URL):
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="nodeAddress"
                                                        title="Node URL address"
                                                        readonly="true"/> 
                                        <form:errors path="nodeAddress" 
                                                     cssClass="error"/>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Organization:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="orgName"
                                                    title="Organization name"
                                                    readonly="true"/>
                                        <form:errors path="orgName" 
                                                     cssClass="error"/>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Unit:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="orgUnit"
                                                    title="Name of unit or department"
                                                    readonly="true"/>
                                        <form:errors path="orgUnit" 
                                                     cssClass="error"/>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Address:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="locality"
                                                    title="Organization's address"
                                                    readonly="true"/>
                                        <form:errors path="locality" 
                                                     cssClass="error"/>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Country:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="state"
                                                    title="Country name"
                                                    readonly="true"/>
                                        <form:errors path="state" 
                                                     cssClass="error"/>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Country code:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="countryCode"
                                                        title="2-letter country code"
                                                        readonly="true"/>
                                        <form:errors path="countryCode" 
                                                     cssClass="error"/>
                                    </div>
                                </div>
                            </div>
                            <div class="table-footer">
                                <div class="buttons">
                                    <div class="button-wrap">
                                        <input class="button" type="button" 
                                               value="OK"
                                               onclick="window.location.href='user_edit.htm'"/>
                                    </div>
                                </div>
                            </div>                                   
                        </form:form>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${user_account.id == 0}">
                                <div class="header">
                                    <div class="row">Add user account</div>
                                </div>
                                <div class="header-text">
                                    Configure new user account.  
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="header">
                                    <div class="row">Edit user account</div>
                                </div>
                                <div class="header-text">
                                    Edit existing user account.  
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <form:form method="POST" commandName="user_account">
                            <div class="body">
                                <div class="row">
                                    <div class="leftcol">
                                        User name:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="name" 
                                                    title="Unique user name"/>
                                        <form:errors path="name" 
                                                     cssClass="error"/>   
                                    </div>
                                </div>
                                <c:if test="${user_account.id == 0}">
                                    <div class="row">
                                        <div class="leftcol">
                                            Password:
                                        </div>
                                        <div class="rightcol">
                                            <form:password path="password"
                                                           title="Password"/>
                                            <form:errors path="password" 
                                                         cssClass="error"/>   
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="leftcol">
                                            Repeat password:
                                        </div>
                                        <div class="rightcol">
                                            <input type="password" 
                                                   name="repeat_password"
                                                   title="Repeat password"/>
                                        </div>
                                    </div>
                                </c:if>
                                <div class="row">
                                    <div class="leftcol">
                                        Role:
                                    </div>
                                    <div class="rightcol">
                                        <form:select path="role" 
                                                     title="User role">
                                            <form:options items="${roles}"/>
                                        </form:select>    
                                        <form:errors path="role" 
                                                     cssClass="error"/>
                                    </div>
                                </div>        
                                <div class="row">
                                    <div class="leftcol">
                                        Organization:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="orgName"
                                                    title="Organization name"/>
                                        <form:errors path="orgName" 
                                                     cssClass="error"/>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Unit:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="orgUnit"
                                                    title="Name of unit or department"/>
                                        <form:errors path="orgUnit" 
                                                     cssClass="error"/>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Address:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="locality"
                                                    title="Organization's address"/>
                                        <form:errors path="locality" 
                                                     cssClass="error"/>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Country:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="state"
                                                    title="Country name"/>
                                        <form:errors path="state" 
                                                     cssClass="error"/>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="leftcol">
                                        Country code:
                                    </div>
                                    <div class="rightcol">
                                        <form:input path="countryCode"
                                                    title="2-letter country code"/>
                                        <form:errors path="countryCode" 
                                                     cssClass="error"/>
                                    </div>
                                </div>    
                            </div>              
                            <div class="table-footer">
                                <div class="buttons">
                                    <div class="button-wrap">
                                        <input class="button" type="submit" 
                                               value="Save"/>
                                    </div>
                                </div>
                            </div>                  
                        </form:form>               
                    </c:otherwise>
                </c:choose>    
            </div>
        </div>
    </jsp:body>
</t:generic_page>

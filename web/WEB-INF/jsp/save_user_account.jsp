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
Document   : Save user account page
Created on : Oct 4, 2012, 2:42 PM
Author     : szewczenko
------------------------------------------------------------------------------%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="/WEB-INF/jsp/include.jsp"%>

<t:page_tabbed pageTitle="Save user account" activeTab="settings">
    <jsp:body>
        <div class="left">
            <t:menu_settings/>
        </div>
        <div class="right">
            <c:choose>
                <c:when test="${user_account.role == 'peer'}">
                    <div class="blttitle">
                        Peer account details
                    </div>
                    <div class="blttext">
                        Peer account settings are read-only.  
                    </div>
                    <form:form method="POST" commandName="user_account">
                        <div class="table">
                            <div class="addaccount">
                                <div class="leftcol">        
                                    <div class="row">User name</div>
                                    <div class="row">Role</div>
                                    <div class="row">Node address</div>
                                    <div class="row">Organization name</div>
                                    <div class="row">Unit name</div>
                                    <div class="row">Locality name (City)</div>
                                    <div class="row">State name (Country)</div>
                                    <div class="row">Country code</div>
                                </div>
                                <div class="rightcol">
                                    <div class="row">
                                        <div class="username">
                                            <form:input path="name"
                                                        title="User name"
                                                        readonly="true"/>
                                            <div class="hint">
                                                Unique user name
                                            </div>
                                        </div>
                                        <form:errors path="name" cssClass="error"/>   
                                    </div>
                                    <div class="row">
                                        <div class="rolename">
                                            <form:input path="role"
                                                        title="User's role"
                                                        readonly="true"/>  
                                            <div class="hint">
                                                User's role 
                                            </div>
                                        </div>    
                                        <form:errors path="role" cssClass="error"/>
                                    </div>
                                    <div class="row">
                                        <div class="address">
                                            <form:input path="nodeAddress"
                                                        title="Node address"
                                                        readonly="true"/> 
                                            <div class="hint">
                                                Node address
                                            </div>
                                        </div>    
                                        <form:errors path="nodeAddress" cssClass="error"/>
                                    </div>
                                    <div class="row">
                                        <div class="orgname">
                                            <form:input path="orgName"
                                                        title="Organization name"
                                                        readonly="true"/>
                                            <div class="hint">
                                                Name of organization
                                            </div>
                                        </div>
                                        <form:errors path="orgName" cssClass="error"/>
                                    </div>
                                    <div class="row">
                                        <div class="orgname">
                                            <form:input path="orgUnit"
                                                        title="Unit name"
                                                        readonly="true"/>
                                            <div class="hint">
                                                Unit name, e.g. Forecast Department
                                            </div>
                                        </div>
                                        <form:errors path="orgUnit" cssClass="error"/>
                                    </div>
                                    <div class="row">
                                        <div class="city">
                                            <form:input path="locality"
                                                        title="Address"
                                                        readonly="true"/>
                                            <div class="hint">
                                                Address
                                            </div>
                                        </div>
                                        <form:errors path="locality" cssClass="error"/>
                                    </div>
                                    <div class="row">
                                        <div class="country">
                                            <form:input path="state"
                                                        title="State name"
                                                        readonly="true"/>
                                            <div class="hint">
                                                State name (Country)
                                            </div>
                                        </div>
                                        <form:errors path="state" cssClass="error"/>
                                    </div>
                                    <div class="row">
                                        <div class="zipcode">
                                            <form:input path="countryCode"
                                                        title="Country code"
                                                        readonly="true"/>
                                            <div class="hint">
                                                Two-letter country code
                                            </div>
                                        </div>
                                        <form:errors path="countryCode" cssClass="error"/>
                                    </div>
                                </div>
                                <div class="tablefooter">
                                    <div class="buttons">
                                        <button class="rounded" type="button"
                                            onclick="window.location.href='edit_user_account.htm'">
                                            <span>OK</span>
                                        </button>
                                    </div>
                                </div> 
                            </div>
                        </div>    
                    </form:form>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${user_account.id == 0}">
                            <div class="blttitle">
                                Add user account
                            </div>
                            <div class="blttext">
                                Configure new user account.  
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="blttitle">
                                Edit user account
                            </div>
                            <div class="blttext">
                                Edit selected user account.  
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <form:form method="POST" commandName="user_account">
                        <div class="table">
                            <div class="addaccount">
                                <div class="leftcol">        
                                    <div class="row">User name</div>
                                    <c:if test="${user_account.id == 0}">
                                        <div class="row">Password</div>
                                        <div class="row">Repeat password</div>
                                    </c:if>
                                    <div class="row">Role</div>
                                    <div class="row">Organization name</div>
                                    <div class="row">Unit name</div>
                                    <div class="row">Locality name (City)</div>
                                    <div class="row">State name (Country)</div>
                                    <div class="row">Country code</div>
                                </div>
                                <div class="rightcol">
                                    <%@include file="/WEB-INF/jsp/form_messages.jsp"%>
                                    <div class="row">
                                        <div class="username">
                                            <form:input path="name"
                                                        title="Enter unique user name"/>
                                            <div class="hint">
                                                Unique user name
                                            </div>
                                        </div>
                                        <form:errors path="name" cssClass="error"/>   
                                    </div>
                                    <c:if test="${user_account.id == 0}">
                                        <div class="row">
                                            <div class="password">
                                                <form:password path="password"
                                                            title="Enter password"/>
                                                <div class="hint">
                                                    User's password
                                                </div>
                                            </div>
                                            <form:errors path="password" cssClass="error"/>   
                                        </div>
                                        <div class="row">
                                            <div class="password">
                                                <input type="password" 
                                                       name="repeat_password"
                                                       title="Repeat password"/>
                                                <div class="hint">
                                                    Repeat password
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                    <div class="row">
                                        <div class="rolename">
                                            <form:select path="role" 
                                                        title="Select user's role">
                                                <form:options items="${roles}"/>
                                            </form:select>    
                                            <div class="hint">
                                                User's role 
                                            </div>
                                        </div>    
                                        <form:errors path="role" cssClass="error"/>
                                    </div>
                                    <div class="row">
                                        <div class="orgname">
                                            <form:input path="orgName"
                                                        title="Enter organization name"/>
                                            <div class="hint">
                                                Name of organization
                                            </div>
                                        </div>
                                        <form:errors path="orgName" cssClass="error"/>
                                    </div>
                                    <div class="row">
                                        <div class="orgname">
                                            <form:input path="orgUnit"
                                                        title="Enter unit name"/>
                                            <div class="hint">
                                                Unit name, e.g. Forecast Department
                                            </div>
                                        </div>
                                        <form:errors path="orgUnit" cssClass="error"/>
                                    </div>
                                    <div class="row">
                                        <div class="city">
                                            <form:input path="locality"
                                                        title="Enter address"/>
                                            <div class="hint">
                                                Address
                                            </div>
                                        </div>
                                        <form:errors path="locality" cssClass="error"/>
                                    </div>
                                    <div class="row">
                                        <div class="country">
                                            <form:input path="state"
                                                        title="State name"/>
                                            <div class="hint">
                                                State name (Country)
                                            </div>
                                        </div>
                                        <form:errors path="state" cssClass="error"/>
                                    </div>
                                    <div class="row">
                                        <div class="zipcode">
                                            <form:input path="countryCode"
                                                        title="Enter two-letter country code"/>
                                            <div class="hint">
                                                Two-letter country code
                                            </div>
                                        </div>
                                        <form:errors path="countryCode" cssClass="error"/>
                                    </div> 
                                </div>
                                <div class="tablefooter">
                                    <div class="buttons">
                                        <button class="rounded" type="button"
                                            onclick="history.back()">
                                            <span>Back</span>
                                        </button>
                                        <button class="rounded" type="submit">
                                            <span>Save</span>
                                        </button>
                                    </div>
                                </div>    
                            </div>
                        </div>    
                    </form:form>        
                </c:otherwise>
            </c:choose>
        </div>                                    
    </jsp:body>
</t:page_tabbed>

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
Document   : Sign in page
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
    <title>Sign in</title>
</head>

<body>
    <div id="signin-container">
        <div id="header"></div>
            <div class="signin-inner">
                <div class="signin-float-wrap">
                    <div id="signin-main">
                        <div id="signin-box">
                            <div id="signin-welcome">
                                Welcome to Baltrad Data Exchange System!
                            </div>
                            <div id="signin-info">
                                This node is operated by
                            </div>
                            <div id="signin-operator">
                                <fmt:setLocale value="en"/>
                                <fmt:setBundle basename="messages"/>
                                <fmt:message key="message.operator"/>
                            </div>
                            <div id="signin-info">
                                Please sign in.
                            </div>
                            <form method="post" action="signin.htm">
                                <div id ="signin-form">
                                    <%@ include file="/WEB-INF/jsp/includeMessages.jsp" %>
                                    <div id="signin-form-leftcol">
                                        <div id="signin-form-elem">
                                            User name:
                                        </div>
                                        <div id="signin-form-elem">
                                            Password:
                                        </div>
                                    </div>
                                    <div id="signin-form-rightcol">
                                        <div id="signin-form-elem">
                                            <form:input path="command.name"/>
                                        </div>
                                        <div id="signin-form-elem">
                                            <form:password path="command.password"/>
                                        </div>
                                    </div>
                                </div>
                                <div id="signin-submit">
                                    <input type="submit" value="Submit" name="loginButton" />
                                </div>
                            </form>
                        </div>
                    </div>
                    <div class="clear"></div>
                </div>
            <div class="clear"></div>
        </div>
    </div>
    <div id="footer">
        <script type="text/javascript" src="includes/footer.js"></script>
    </div>
</body>
</html>
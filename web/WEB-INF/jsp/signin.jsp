<%--
    Document   : System welcome page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

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
                                <fmt:message key="message.operator.pl"/>
                            </div>
                            <div id="signin-info">
                                Please sign in.
                            </div>
                            <form method="post" action="signin.htm">
                                <div id ="signin-form">
                                    <%@ include file="/WEB-INF/jsp/includemessages.jsp"%>
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
                                            <spring:bind path="command.name">
                                                <input type="text"
                                                    name='<c:out value="${status.expression}"/>'
                                                    value='<c:out value="${status.value}"/>'
                                                    size="20" />
                                            </spring:bind>
                                        </div>
                                        <div id="signin-form-elem">
                                            <spring:bind path="command.password">
                                                <input type="password"
                                                    name='<c:out value="${status.expression}"/>'
                                                    value='<c:out value="${status.value}"/>'
                                                    size="20" />
                                            </spring:bind>
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
<%--
    Document   : Save user account page
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
    <title>Manage user account</title>
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
                        <h1>Manage account</h1>
                        <br/>
                        <h2>
                            <p>
                            Create new account / manage existing user account
                            </p>
                        </h2>
                        <form method="post">
                            <table>
                                <caption>User account information</caption>
                                <tr class="even">
                                    <td class="left">User name</td>
                                    <td class="right">
                                        <form:input path="command.name"/>
                                        <form:errors path="command.name" cssClass="errors"/>
                                    </td>
                                </tr>
                                <tr class="odd">
                                    <td class="left">Password</td>
                                    <td class="right">
                                        <form:password path="command.password"/>
                                        <form:errors path="command.password" cssClass="errors"/>
                                    </td>
                                </tr>
                                <tr class="even">
                                    <td class="left">Retype password</td>
                                    <td class="right">
                                        <form:password path="command.retPassword"/>
                                        <form:errors path="command.retPassword" cssClass="errors"/>
                                    </td>
                                </tr>
                                <tr class="odd">
                                    <td class="left">User role</td>
                                    <td class="right">
                                    <spring:bind path="command.role">
                                        <select name='<c:out value="${status.expression}"/>'>
                                            <c:forEach items="${roles}" var="role">
						<option value='<c:out value="${role.role}"/>'
                                                    <c:if test="${role.role == status.value}">
                                                        SELECTED</c:if>>
                                                        <c:out value="${role.role}"/></option>
                                            </c:forEach>
					</select>
                                    </spring:bind>
                                    <form:errors path="command.role" cssClass="errors"/>
                                    </td>
                                </tr>
                                <tr class="even">
                                    <td class="left">Local Baltrad node address</td>
                                    <td class="right">
                                        <form:input path="command.nodeAddress"/>
                                        <form:errors path="command.nodeAddress" cssClass="errors"/>
                                    </td>
                                </tr>
                                <tr class="odd">
                                    <td class="left">Company name</td>
                                    <td class="right">
                                        <form:input path="command.factory"/>
                                        <form:errors path="command.factory" cssClass="errors"/>
                                    </td>
                                </tr>
                                <tr class="even">
                                    <td class="left">Country</td>
                                    <td class="right">
                                        <form:input path="command.country"/>
                                        <form:errors path="command.country" cssClass="errors"/>
                                    </td>
                                </tr>
                                <tr class="odd">
                                    <td class="left">City</td>
                                    <td class="right">
                                        <form:input path="command.city"/>
                                        <form:errors path="command.city" cssClass="errors"/>
                                    </td>
                                </tr>
                                <tr class="even">
                                    <td class="left">City code</td>
                                    <td class="right">
                                        <form:input path="command.cityCode"/>
                                        <form:errors path="command.cityCode" cssClass="errors"/>
                                    </td>
                                </tr>
                                <tr class="odd">
                                    <td class="left">Street</td>
                                    <td class="right">
                                        <form:input path="command.street"/>
                                        <form:errors path="command.street" cssClass="errors"/>
                                    </td>
                                </tr>
                                <tr class="even">
                                    <td class="left">Number</td>
                                    <td class="right">
                                        <form:input path="command.number"/>
                                        <form:errors path="command.number" cssClass="errors"/>
                                    </td>
                                </tr>
                                <tr class="odd">
                                    <td class="left">Phone number</td>
                                    <td class="right">
                                        <form:input path="command.phone"/>
                                        <form:errors path="command.phone" cssClass="errors"/>
                                    </td>
                                </tr>
                                <tr class="even">
                                    <td class="left">Email address</td>
                                    <td class="right">
                                        <form:input path="command.email"/>
                                        <form:errors path="command.email" cssClass="errors"/>
                                    </td>
                                </tr>
                            </table>
                            <div id="table-footer-rightcol">
                                <input type="submit" value="Submit" name="submit_button"/>
                            </div> 
                        </form>
                        <div id="table-footer">
                            <a href="admin.htm">&#60&#60 System management</a>
                        </div>
                    </div>
                    <div id="left">
                        <script type="text/javascript" src="includes/mainmenu.js"></script>
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
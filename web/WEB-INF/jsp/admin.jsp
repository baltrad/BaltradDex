<%--
    Document   : System welcome page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ page import="eu.baltrad.dex.model.user.User" %>
<%@ page import="java.util.Date" %>

<jsp:useBean id="applicationSecurityManager" scope="session"
                                    class="eu.baltrad.dex.util.ApplicationSecurityManager">
</jsp:useBean>
<jsp:useBean id="userManager" scope="session" class="eu.baltrad.dex.model.user.UserManager">
</jsp:useBean>
<jsp:useBean id="logManager" scope="session" class="eu.baltrad.dex.model.log.LogManager">
</jsp:useBean>

<%
    User sessionUser = ( User )applicationSecurityManager.getUser( request );
    User dbUser = userManager.getUserByName( "admin" );
    if( !sessionUser.getName().equals( "admin" ) ||
                    !applicationSecurityManager.authenticateSessionUser( sessionUser, dbUser ) ) {
        request.getSession().setAttribute( "is_user_admin", 0 );
        logManager.addEntry( new Date(), logManager.MSG_WRN, "User failed to access " +
                "system management area");
    } else {
        request.getSession().setAttribute( "is_user_admin", 1 );
    }
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>System management</title>
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
                        <c:choose>
                            <c:when test="${is_user_admin == 1}">
                                <h1>System management options</h1>
                                <br/>
                                <h2>
                                </h2>
                                <table>
                                    <div id="message-text">
                                        <c:if test="${not empty message}">
                                            <c:out value="${message}" />
                                            <c:set var="message" value="" scope="session" />
                                        </c:if>
                                    </div>
                                    <caption>Control features</caption>
                                    <tr class="even">
                                        <td class="left">Data delivery register</td>
                                        <td class="right">
                                            <a href="showregister.htm">Show register</a>
                                            <a href="clearregister.htm">Clear</a>
                                        </td>
                                    </tr>
                                    <tr class="odd">
                                        <td class="left">Log messages</td>
                                        <td class="right">
                                            <a href="clearmessages.htm">Clear messages</a>
                                        </td>
                                    </tr>
                                    <tr class="even">
                                        <td class="left">User management</td>
                                        <td class="right">
                                            <a href="edituser.htm">Edit</a>
                                            <a href="saveuser.htm">Add</a>
                                            <a href="removeusers.htm">Remove</a>
                                        </td>
                                    </tr>
                                    <tr class="odd">
                                        <td class="left">Data channel management</td>
                                        <td class="right">
                                            <a href="editchannel.htm">Edit</a>
                                            <a href="savechannel.htm">Add</a>
                                            <a href="removechannels.htm">Remove</a>
                                        </td>
                                    </tr>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div id="message-box">
                                    Access to administrative area is restricted.
                                    Please sign in as administrator and try again.
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <div id="table-footer">
                            <a href="welcome.htm">&#60&#60 Home</a>
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
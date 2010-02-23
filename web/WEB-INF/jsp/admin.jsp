<%--
    Document   : System welcome page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ page import="pl.imgw.baltrad.dex.model.User" %>

<jsp:useBean id="applicationSecurityManager" scope="session"
                                    class="pl.imgw.baltrad.dex.util.ApplicationSecurityManager">
</jsp:useBean>

<jsp:useBean id="userManager" scope="session" class="pl.imgw.baltrad.dex.model.UserManager">
</jsp:useBean>

<%
    User sessionUser = ( User )applicationSecurityManager.getUser( request );
    User dbUser = userManager.getUserByName( "admin" );
    if( !sessionUser.getName().equals( "admin" ) ||
                    !applicationSecurityManager.authenticateSessionUser( sessionUser, dbUser ) ) {
        request.getSession().setAttribute( "is_user_admin", 0 );
    } else {
        request.getSession().setAttribute( "is_user_admin", 1 );
    }
    String serverStatus = applicationSecurityManager.getServerRunning() ? "Running..." : "Idle";
    String controlColor = applicationSecurityManager.getServerRunning() ? "#99CC66" : "#FFFFFF";
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css">
    <title>Baltrad Data Exchange System</title>
</head>

<div id="content">
    <div id="header">
        <img src="includes/images/baltrad_header.png">
    </div>
    <div id="container1">
        <div id="container2">
            <div id="leftcol">
                <script type="text/javascript" src="includes/mainmenu.js"></script>
            </div>
            <div id="rightcol">
                <c:choose>
                    <c:when test="${is_user_admin == 1}">
                        <div id="table-info">
                            Node control options:
                        </div>
                        <div id="table-content">
                            <div id="admin-leftcol">
                                <div class="admin-elem">
                                    Server process control:
                                </div>
                                <div class="admin-elem">
                                    Data delivery register:
                                </div>
                                <div class="admin-elem">
                                    Log messages:
                                </div>
                                <div class="admin-elem">
                                    User management:
                                </div>
                            </div>
                            <div id="admin-rightcol">
                                <div class="admin-elem">
                                    <a href="on.htm">ON</a>
                                    <a href="off.htm">OFF</a>
                                    <input type="submit"
                                    value="<% out.println( serverStatus ); %>"
                                    style="background-color:
                                            <% out.println( controlColor ); %>;
                                    width:100px">
                                </div>
                                <div class="admin-elem">
                                    <a href="clear_register.htm">Clear register</a>
                                </div>
                                <div class="admin-elem">
                                    <a href="clear_log_messages.htm">Clear messages</a>
                                </div>
                                <div class="admin-elem">
                                    <a href="add_user.htm">Add</a>
                                    <a href="edit_user.htm">Edit</a>
                                    <a href="remove_user.htm">Remove</a>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div id="message-box">
                            Access to administrative area is restricted.
                            Please sign in as administrator and try again.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    <div id="footer">
        <script type="text/javascript" src="includes/footer.js"></script>
    </div>
</div>
</html>
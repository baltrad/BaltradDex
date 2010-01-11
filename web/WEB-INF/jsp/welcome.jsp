<%-- 
    Document   : System welcome page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@page import="pl.imgw.baltrad.dex.model.User"%>

<jsp:useBean id="userManager" scope="session" class="pl.imgw.baltrad.dex.model.UserManager">
</jsp:useBean>

<%
    User operator = userManager.getUserByRole( "operator" );
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
                    <a href="welcome.htm">Home</a>
                    <br>
                    <a href="channels.htm">Data channels</a>
                    <a href="subscriptions.htm">Subscriptions</a>
                    <a href="log.htm">View logs</a>
                    <a href="welcome.htm">Help</a>
                    <a href="welcome.htm">Links</a>
                    <br>
                    <a href="admin.htm">System management</a>
                    <br>
                    <a href="signout.htm">Logout</a>
                    <br>
                </div>
                <div id="rightcol">
                    <h1>Welcome to the Baltrad Data Exchange System!</h1>
                    <br>
                    <h2>Information on this node:</h2>
                    <div id="operator">
                        <div id="operator-leftcol">
                            <div id="operator-elem" class="odd">
                                Operator name:
                            </div>
                            <div id="operator-elem" class="even">
                                Country:
                            </div>
                            <div id="operator-elem" class="odd">
                                Address / city:
                            </div>
                            <div id="operator-elem" class="even">
                                Address / code:
                            </div>
                            <div id="operator-elem" class="odd">
                                Address / street:
                            </div>
                            <div id="operator-elem" class="even">
                                Contact phone:
                            </div>
                            <div id="operator-elem" class="odd">
                                E-mail:
                            </div>
                        </div>
                        <div id="operator-rightcol">
                            <div id="operator-elem" class="odd">
                                <% out.println( operator.getFactory() ); %>
                            </div>
                            <div id="operator-elem" class="even">
                                <% out.println( operator.getCountry() ); %>
                            </div>
                            <div id="operator-elem" class="odd">
                                <% out.println( operator.getCity() ); %>
                            </div>
                            <div id="operator-elem" class="even">
                                <% out.println( operator.getZipCode() ); %>
                            </div>
                            <div id="operator-elem" class="odd">
                                <% out.println( operator.getNumber() + ", "
                                                                        + operator.getStreet() );
                                %>
                            </div>
                            <div id="operator-elem" class="even">
                                <% out.println( operator.getPhone() ); %>
                            </div>
                            <div id="operator-elem" class="odd">
                                <a href="mailto:baltrad.admin@imgw.pl">
                                    <% out.println( operator.getEmail() ); %>
                                </a>
                            </div>
                        </div>
                    </div>
                    <div id="operator-logo">
                        <img src="includes/images/logo.png">
                    </div>
                </div>
            </div>
        </div>
    <div id="footer">
        <div class="leftcol">
            Baltrad DEX v.0.1
        </div>
        <div class="rightcol">
            BALTRAD Project Group &#169 2009
        </div>
    </div>
</div>
</html>
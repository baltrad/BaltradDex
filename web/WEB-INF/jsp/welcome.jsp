<%-- 
    Document   : System welcome page
    Created on : December 9, 2009, 13:56:14 PM
    Author     : szewczenko
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                                                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB">

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@page import="eu.baltrad.dex.model.user.User"%>

<jsp:useBean id="userManager" scope="session" class="eu.baltrad.dex.model.user.UserManager">
</jsp:useBean>

<%
    User operator = userManager.getUserByRole( "operator" );
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link href="includes/baltraddex.css" rel="stylesheet" type="text/css"/>
    <title>Home</title>
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
                        <div id="welcome">
                            <h1>Welcome to the Baltrad Data Exchange System!</h1>
                            <br/>
                            <h2>
                                <p>
                                Use the system functionality to browse and download data or establish
                                operational data exchange links by subscribing to the desired data
                                channel.
                                </p>
                                <p>
                                Following is the detailed information about this node.
                                </p>
                            </h2>
                        </div>
                        <table>
                            <tr class="even">
                                <td class="left">Operator name</td>
                                <td class="right">
                                    <% out.println( operator.getFactory() ); %>
                                </td>
                            </tr>
                            <tr class="odd">
                                <td class="left">Country</td>
                                <td class="right">
                                    <% out.println( operator.getCountry() ); %>
                                </td>
                            </tr>
                            <tr class="even">
                                <td class="left">Address :: City</td>
                                <td class="right">
                                    <% out.println( operator.getCity() ); %>
                                </td>
                            </tr>
                            <tr class="odd">
                                <td class="left">Address :: Code</td>
                                <td class="right">
                                    <% out.println( operator.getCityCode() ); %>
                                </td>
                            </tr>
                            <tr class="even">
                                <td class="left">Address :: Street</td>
                                <td class="right">
                                    <% out.println( operator.getStreet() ); %>
                                </td>
                            </tr>
                            <tr class="odd">
                                <td class="left">Phone number</td>
                                <td class="right">
                                    <% out.println( operator.getPhone() ); %>
                                </td>
                            </tr>
                            <tr class="even">
                                <td class="left">E-mail</td>
                                <td class="right">
                                    <a href="mailto:<% out.println( operator.getEmail() );%>">
                                        <% out.println( operator.getEmail() );%>
                                    </a>
                                </td>
                            </tr>
                        </table>
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
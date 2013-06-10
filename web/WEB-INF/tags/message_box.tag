<%-- 
    Document   : Message box
    Created on : Apr 4, 2013, 3:13:42 PM
    Author     : szewczenko
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ tag description="Displays system messages and errors" 
        pageEncoding="UTF-8" %>

<%@ attribute name="msgHeader" required="false" %>
<%@ attribute name="msgBody" required="false" %>
<%@ attribute name="errorHeader" required="false" %>
<%@ attribute name="errorBody" required="false" %>
<%@ attribute name="email" required="false" %>
<%@ attribute name="link" required="false" %>

<c:if test="${not empty msgBody}">
    <div class="messagebox" id="msg">
        <div class="header">
           <c:out value="${msgHeader}"/>
        </div>
        <div class="body">
            <c:out value="${msgBody}"/>
        </div>
    </div>                      
</c:if> 
<c:if test="${not empty errorBody}">
    <div class="messagebox" id="error">
        <div class="header">
            <c:out value="${errorHeader}"/>
        </div>
        <div class="body">
            <c:out value="${errorBody}"/>
            <c:if test="${not empty email && not empty link}">
                <a href="mailto:${email}">
                    ${link}
                </a>
            </c:if>
        </div>
    </div>                       
</c:if>

                     
                
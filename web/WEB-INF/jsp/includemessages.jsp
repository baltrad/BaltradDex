<%-- 
    Document   : Page displays status and error messages
    Created on : Aug 18, 2009, 12:38:13 PM
    Author     : szewczenko
--%>

<center>
<!-- Error message -->
<spring:bind path="command.*">
	<c:if test="${not empty status.errorMessages}">
		<c:forEach var="error" items="${status.errorMessages}">
			<font color="red"><c:out value="${error}" escapeXml="false" />
			</font>
			<br />
		</c:forEach>
	</c:if>
</spring:bind>

<!-- Status messages -->
 <c:if
	test="${not empty message}">
	<font color="green"><c:out value="${message}" /></font>
	<c:set var="message" value="" scope="session" />
</c:if>
</center>


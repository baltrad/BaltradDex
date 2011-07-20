<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="message"%>

<c:if test="${emessage != null}">
    <div class="systemerror">
        <div class="header">Problems encountered.</div>
        <div class="message">${message}</div>
    </div>
</c:if>

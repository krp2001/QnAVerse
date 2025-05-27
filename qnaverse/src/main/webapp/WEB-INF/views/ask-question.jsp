<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>${question.id == null ? 'Ask a Question' : 'Edit Question'}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
	<jsp:include page="/WEB-INF/views/header.jsp"/>

    <c:choose>
        <c:when test="${question.id == null}">
            <c:set var="formAction"
                   value="${pageContext.request.contextPath}/questions/post"/>
        </c:when>
        <c:otherwise>
            <c:set var="formAction"
                   value="${pageContext.request.contextPath}/questions/${fn:escapeXml(question.id)}/update"/>
        </c:otherwise>
    </c:choose>

    <form:form method="post"
               modelAttribute="question"
               action="${formAction}">
			   <sec:csrfInput />
        <div class="mb-3">
            <form:label  path="title">Title:</form:label>
            <form:input  path="title" cssClass="form-control" required="true"/>
        </div>

        <div class="mb-3">
            <form:label  path="content">Content:</form:label>
            <form:textarea path="content" rows="6" cssClass="form-control" required="true"/>
        </div>

        <button class="btn btn-primary" type="submit">
            ${question.id == null ? 'Post' : 'Update'}
        </button>
    </form:form>
</div>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="card shadow rounded-4">
                <div class="card-body p-4">
                    <h2 class="text-center mb-4">Register</h2>

					<form:errors path="*" cssClass="alert alert-danger" element="div" />
					<c:if test="${not empty error}">
					    <div class="alert alert-danger" role="alert">
					        ${error}
					    </div>
					</c:if>
					
					<form:form action="${pageContext.request.contextPath}/users/register" method="post" modelAttribute="user">
						<sec:csrfInput />    
					<div class="mb-3">
					        <label for="email">Email:</label>
					        <form:input path="email" class="form-control"/>
					        <form:errors path="email" cssClass="text-danger"/>
					    </div>
					    <div class="mb-3">
					        <label for="password">Password:</label>
					        <form:password path="password" class="form-control"/>
					        <form:errors path="password" cssClass="text-danger"/>
					        <small class="form-text text-muted">
					            Password must be at least 8 characters long and contain at least one digit, 
					            one lowercase letter, one uppercase letter, and one special character.
					        </small>
					    </div>
						<div class="d-grid">
					    <button type="submit" class="btn btn-primary mt-3">Register</button>
						</div>
					</form:form>

                    <p class="text-center mt-3 mb-0">
                        <a href="${pageContext.request.contextPath}/users/login">Already have an account? Login</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Optional Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <!-- Bootstrap CSS CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="card shadow rounded-4">
                <div class="card-body p-4">
                    <h2 class="text-center mb-4">Login</h2>
					
					<c:if test="${param.error != null}">
					    <div class="alert alert-danger" role="alert">
					        Invalid email or password. Please try again.
					    </div>
					</c:if>
					
					<c:if test="${param.logout != null}">
					    <div class="alert alert-success" role="alert">
					        You have been logged out successfully.
					    </div>
					</c:if>

                    <form action="${pageContext.request.contextPath}/users/login" method="post">
						<sec:csrfInput />
                        <div class="mb-3">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" name="email" required />
                        </div>

                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" class="form-control" id="password" name="password" required />
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary">Login</button>
                        </div>
                    </form>

                    <p class="text-center mt-3 mb-0">
                        <a href="${pageContext.request.contextPath}/users/register">Don't have an account? Register</a>
                    </p>
					<div class="form-group text-center">
					    <a href="${pageContext.request.contextPath}/users/forgot-password">Forgot Password?</a>
					</div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS  -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html>
<head>
    <title>Reset Password - QnAVerse</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header">
                        <h4 class="mb-0">Reset Password</h4>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">${error}</div>
                        </c:if>
                        
                        <form action="${pageContext.request.contextPath}/users/reset-password" method="post">
                            <input type="hidden" name="token" value="${token}">
							<sec:csrfInput />
                            <div class="mb-3">
                                <label for="password" class="form-label">New Password</label>
                                <input type="password" name="password" id="password" class="form-control" required minlength="8">
                                <small class="text-muted">Password must be at least 8 characters and include uppercase, lowercase, number and special character.</small>
                            </div>
                            
                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label">Confirm Password</label>
                                <input type="password" name="confirmPassword" id="confirmPassword" class="form-control" required>
                            </div>
                            
                            <button type="submit" class="btn btn-primary">Reset Password</button>
                        </form>
                        
                        <div class="mt-3">
                            <a href="${pageContext.request.contextPath}/users/login">Back to Login</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
    document.getElementById('password').addEventListener('input', function() {
        validatePassword();
    });
    
    document.querySelector('form').addEventListener('submit', function(e) {
        if (!validatePassword() || !validatePasswordMatch()) {
            e.preventDefault();
        }
    });
    
    function validatePassword() {
        const password = document.getElementById('password').value;
        const regex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,}$/;
        
        if (!regex.test(password)) {
            document.getElementById('password').setCustomValidity('Password must be at least 8 characters and include uppercase, lowercase, number and special character');
            return false;
        } else {
            document.getElementById('password').setCustomValidity('');
            return true;
        }
    }
    
    function validatePasswordMatch() {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        
        if (password !== confirmPassword) {
            document.getElementById('confirmPassword').setCustomValidity('Passwords do not match');
            return false;
        } else {
            document.getElementById('confirmPassword').setCustomValidity('');
            return true;
        }
    }
    </script>
</body>
</html>
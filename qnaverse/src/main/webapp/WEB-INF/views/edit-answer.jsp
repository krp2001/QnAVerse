<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Answer - QnAVerse</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <style>
        body {
            padding-top: 70px;
            background-color: #f8f9fa;
        }
        .card {
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        }
        .question-title {
            font-weight: bold;
            color: #0d6efd;
        }
    </style>
</head>
<body>
    <!-- shared navigation / header -->
    <jsp:include page="/WEB-INF/views/header.jsp"/>
    
    <div class="container mt-4">
        <h2>Edit Your Answer</h2>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>
        
        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <div>
                    <span class="text-muted">Question:</span>
                    <span class="question-title">${answer.question.title}</span>
                </div>
                <a href="${pageContext.request.contextPath}/questions/${answer.question.id}" class="btn btn-sm btn-outline-primary">
                    <i class="bi bi-arrow-left"></i> Back to Question
                </a>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/answers/${answer.id}/update" method="post">
					<sec:csrfInput />
					<div class="mb-3">
                        <label for="content" class="form-label">Your Answer</label>
                        <textarea name="content" id="content" class="form-control" rows="12" required>${answer.content}</textarea>
                    </div>
                    <div class="d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/questions/${answer.question.id}" class="btn btn-secondary">
                            Cancel
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-check-lg"></i> Save Changes
                        </button>
                    </div>
                </form>
            </div>
        </div>
        
        <div class="card">
            <div class="card-header">
                <i class="bi bi-info-circle"></i> Guidelines for answering
            </div>
            <div class="card-body">
                <ul>
                    <li>Answer the question with specific details and explain why</li>
                    <li>Provide examples when possible</li>
                    <li>Cite references or sources if applicable</li>
                    <li>Be respectful and constructive</li>
                </ul>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
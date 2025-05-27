<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <title>${fn:substringBefore(user.email, '@')} - QnAVerse</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .profile-header {
            padding: 20px;
            border-bottom: 1px solid #eee;
            margin-bottom: 20px;
        }
        .activity-section {
            margin-top: 30px;
        }
        .activity-item {
            padding: 10px;
            border-bottom: 1px solid #eee;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/views/header.jsp"/>
    
    <div class="container mt-4">
        <div class="profile-header">
            <div class="row">
                <div class="col">
                    <h2>${fn:substringBefore(user.email, '@')}</h2>
                    <p class="reputation">Reputation: <strong>${user.reputation}</strong></p>
                </div>
            </div>
        </div>
        
        <ul class="nav nav-tabs" id="activityTabs" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="questions-tab" data-bs-toggle="tab" data-bs-target="#questions" type="button" role="tab">Questions (${userQuestions.size()})</button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="answers-tab" data-bs-toggle="tab" data-bs-target="#answers" type="button" role="tab">Answers (${userAnswers.size()})</button>
            </li>
        </ul>
        
        <div class="tab-content" id="activityTabsContent">
            <div class="tab-pane fade show active" id="questions" role="tabpanel">
                <div class="activity-section">
                    <c:if test="${empty userQuestions}">
                        <p class="text-muted">No questions asked yet.</p>
                    </c:if>
                    <c:forEach var="question" items="${userQuestions}">
                        <div class="activity-item">
                            <div class="d-flex justify-content-between">
                                <h5><a href="${pageContext.request.contextPath}/questions/${question.id}">${question.title}</a></h5>
                            </div>
                            <p class="text-truncate">${fn:substring(question.content, 0, 150)}${fn:length(question.content) > 150 ? '...' : ''}</p>
                            <div class="text-muted small">Asked ${question.formattedDate}</div>
                        </div>
                    </c:forEach>
                </div>
            </div>
            
            <div class="tab-pane fade" id="answers" role="tabpanel">
                <div class="activity-section">
                    <c:if test="${empty userAnswers}">
                        <p class="text-muted">No answers provided yet.</p>
                    </c:if>
                    <c:forEach var="answer" items="${userAnswers}">
                        <div class="activity-item">
                            <div class="d-flex justify-content-between">
                                <h5><a href="${pageContext.request.contextPath}/questions/${answer.question.id}#answer-${answer.id}">
                                    ${answer.question.title}
                                </a></h5>
                            </div>
                            <p class="text-truncate">${fn:substring(answer.content, 0, 150)}${fn:length(answer.content) > 150 ? '...' : ''}</p>
                            <div class="text-muted small">Answered ${answer.formattedDate}</div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
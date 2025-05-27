<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title><c:out value="${question.title}"/> - QnAVerse</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <style>
        body {
            padding-top: 70px; /* For fixed navbar */
            background-color: #f8f9fa;
        }
        .question-header {
            border-bottom: 1px solid #dee2e6;
            margin-bottom: 20px;
            padding-bottom: 10px;
        }
        .question-metadata {
            color: #6c757d;
            margin-bottom: 20px;
            font-size: 0.9rem;
        }
        .vote-controls {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin-right: 20px;
        }
        .vote-count {
            font-size: 24px;
            font-weight: bold;
            margin: 10px 0;
        }
        .answer-section {
            margin-top: 40px;
            border-top: 1px solid #dee2e6;
            padding-top: 20px;
        }
        .answer {
            margin: 20px 0;
            padding: 15px;
            border-bottom: 1px solid #eee;
            background-color: white;
            border-radius: 8px;
        }
		.answer-text {
		    white-space: pre-line; 
		}
		
        .question-content {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        }
        .related-questions {
            margin-top: 30px;
            background-color: white;
            padding: 15px;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        }
        .vote-button {
            cursor: pointer;
            transition: color 0.2s;
        }
        .vote-button:hover {
            color: #0d6efd;
        }
    </style>
</head>
<body>

    <!-- shared navigation / header -->
    <jsp:include page="/WEB-INF/views/header.jsp"/>

    <div class="container mt-4">
		<!-- Flash messages -->
		    <c:if test="${not empty errorMessage}">
		        <div class="alert alert-danger">${errorMessage}</div>
		    </c:if>
		    <c:if test="${not empty successMessage}">
		        <div class="alert alert-success">${successMessage}</div>
		    </c:if>
        
    <c:choose>
        <c:when test="${editingQuestion}">
            <h2>Edit Question</h2>
            
            <form:form method="post"
                       modelAttribute="question" 
                       action="${pageContext.request.contextPath}/questions/${question.id}/update">
                <sec:csrfInput /> 

                <div class="mb-3">
                    <form:label path="title">Title:</form:label>
                    <form:input path="title" cssClass="form-control" required="true"/>
                    <form:errors path="title" cssClass="text-danger" />
                </div>

                <div class="mb-3">
                    <form:label path="content">Content:</form:label>
                    <form:textarea path="content" rows="10" cssClass="form-control" required="true"/>
                     <form:errors path="content" cssClass="text-danger" />
                </div>

                <div class="d-flex justify-content-between">
                     <a href="${pageContext.request.contextPath}/questions/${question.id}" class="btn btn-secondary">
                         Cancel
                     </a>
                    <button class="btn btn-primary" type="submit">
                         Update Question
                    </button>
                </div>
            </form:form>
            <hr class="mt-4"> 
        </c:when>

        <c:otherwise>
            <div class="question-header">
                <h1><c:out value="${question.title}"/></h1>
                <div class="question-metadata">
                    Asked ${question.formattedDate} by 
                    <strong>
						${fn:substringBefore(question.postedBy,'@')}</strong> 
					<c:if test="${not empty question.author}"> 
					    <span class="badge bg-secondary ms-1" title="Reputation">${question.author.reputation}</span>
					</c:if>
                    • Viewed ${question.views != null ? question.views : 0} times
                </div>
            </div>

            <div class="row">
                <div class="col-auto">
                    <div class="vote-controls">
                       <div class="col-auto text-center pe-4">
                           <form action="${pageContext.request.contextPath}/questions/${question.id}/vote" method="post" class="mb-1">
                               <sec:csrfInput />
                               <input type="hidden" name="vote" value="1"/>
                               <button class="btn btn-outline-secondary p-1" type="submit" title="Up‑vote">
                                   <i class="bi bi-caret-up-fill"></i>
                               </button>
                           </form>
                           <div class="fw-bold">${question.votes != null ? question.votes : 0}</div>
                           <form action="${pageContext.request.contextPath}/questions/${question.id}/vote" method="post" class="mt-1">
                               <sec:csrfInput />
                               <input type="hidden" name="vote" value="-1"/>
                               <button class="btn btn-outline-secondary p-1" type="submit" title="Down‑vote">
                                   <i class="bi bi-caret-down-fill"></i>
                               </button>
                           </form>
                       </div>
                    </div>
                </div>
                
                <div class="col">
                    <div class="question-content">
                        <p class="answer-text"><c:out value="${question.content}" escapeXml="false"/></p>

						<sec:authorize access="isAuthenticated() and principal.username == '${question.postedBy}'">
						     <div class="d-flex justify-content-end gap-2 mt-2">
						         <a class="btn btn-sm btn-outline-primary"
						            href="${pageContext.request.contextPath}/questions/${question.id}?editQuestion=true"> <%-- Corrected Link for inline edit --%>
						             Edit
						         </a>
						         <form method="post"
						               action="${pageContext.request.contextPath}/questions/${question.id}/delete"
						               onsubmit="return confirm('Delete this question?');">
						             <sec:csrfInput />
						             <button class="btn btn-sm btn-outline-danger" type="submit">
						                 Delete
						             </button>
						         </form>
						     </div>
						 </sec:authorize>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

    <div class="answer-section">
    </div>
        <!-- Answers Section -->
        <div class="answer-section">
            <h3>${fn:length(question.answers)} Answer<c:if test="${fn:length(question.answers) != 1}">s</c:if></h3>
			<!-- List of answers -->
			<c:forEach var="a" items="${question.answers}">
			    <div class="answer" id="answer-${a.id}">
			        <div class="row">
						<!-- For answer votes -->
						<div class="col-auto">
						<div class="vote-controls">
						    <form action="${pageContext.request.contextPath}/answers/${a.id}/vote" method="post" style="display: inline;">
								<sec:csrfInput />
						        <input type="hidden" name="vote" value="1" />
						        <input type="hidden" name="questionId" value="${question.id}" />
						        <button type="submit" class="btn btn-sm btn-outline-secondary" title="Upvote answer">
						            <i class="bi bi-caret-up-fill"></i>
						        </button>
						    </form>
						    
						    <div class="vote-count">${a.votes != null ? a.votes : 0}</div>
						    
						    <form action="${pageContext.request.contextPath}/answers/${a.id}/vote" method="post" style="display: inline;">
								<sec:csrfInput />
						        <input type="hidden" name="vote" value="-1" />
						        <input type="hidden" name="questionId" value="${question.id}" />
						        <button type="submit" class="btn btn-sm btn-outline-secondary" title="Downvote answer">
						            <i class="bi bi-caret-down-fill"></i>
						        </button>
						    </form>
						</div>
					</div>
						
			            
			            <!-- Answer content -->
			            <div class="col">
			                <!-- Check if this answer is being edited -->
			                <c:choose>
			                    <c:when test="${editingAnswerId eq a.id}">
			                        <!-- Edit mode -->
			                        <form action="${pageContext.request.contextPath}/answers/${a.id}/update" method="post">
										<sec:csrfInput />
			                            <input type="hidden" name="questionId" value="${question.id}" />
			                            <textarea class="form-control mb-2" rows="6" name="content" required>${a.content}</textarea>
			                            <div>
			                                <button type="submit" class="btn btn-sm btn-primary me-2">
			                                    <i class="bi bi-check"></i> Save
			                                </button>
			                                <a href="${pageContext.request.contextPath}/questions/${question.id}" class="btn btn-sm btn-secondary">
			                                    <i class="bi bi-x"></i> Cancel
			                                </a>
			                            </div>
			                        </form>
			                    </c:when>
								<c:otherwise>
		                           <p class="answer-text"><c:out value="${a.content}" escapeXml="false"/></p>
			                           <div class="text-muted mt-2">
	    	                           Answered ${a.formattedDate} by 
	        	                       <strong>
										${fn:substringBefore(a.postedBy,'@')}</strong>
										   
										   <c:if test="${not empty a.author}"> 
										       <span class="badge bg-secondary ms-1" title="Reputation">${a.author.reputation}</span>
										   </c:if>
										   <sec:authorize access="isAuthenticated() and (
										       (principal.username == '${a.answeredBy}') or 
										       (principal.username == '${a.postedBy}')
										   )">	                                   
										   <div class="mt-2 d-inline-block"> 
                                       <a href="${pageContext.request.contextPath}/questions/${question.id}?editAnswer=${a.id}" class="btn btn-sm btn-outline-primary me-2">
                                          <i class="bi bi-pencil"></i> Edit
                                       </a>
	                                       <form action="${pageContext.request.contextPath}/answers/${a.id}/delete" method="post" style="display: inline;">
                                            <sec:csrfInput /> 
	                                           <input type="hidden" name="questionId" value="${question.id}" /> 
	                                           <button type="submit" class="btn btn-sm btn-outline-danger"
                                                 onclick="return confirm('Are you sure you want to delete this answer?');">
                                           <i class="bi bi-trash"></i> Delete
                                       </button>
                                       </form>
	                                   </div>
		                               </sec:authorize>
			                       </div>
		                       </c:otherwise>
			                </c:choose>
			            </div>
			        </div>
			    </div>
			</c:forEach>
            
           
            
            <c:if test="${empty question.answers}">
                <div class="alert alert-info mt-3">No answers yet. Be the first to post an answer!</div>
            </c:if>

            <!-- Post Answer Form -->
			<div class="mt-5">
			                 <sec:authorize access="!isAuthenticated()">
			                     <h4>Your Answer</h4>
			                     <div class="alert alert-warning">
			                         Please <a href="${pageContext.request.contextPath}/users/login">sign in</a> or
			                         <a href="${pageContext.request.contextPath}/users/register">register</a> to post an answer.
			                     </div>
			                 </sec:authorize>

			                 <sec:authorize access="isAuthenticated()">
			                     <h4>Your Answer</h4>
			                     <form action="${pageContext.request.contextPath}/answers/post" method="post">
			                         <sec:csrfInput /> 
			                         <input type="hidden" name="questionId" value="${question.id}" />

			                         <div class="form-group mb-3"> 
			                             <textarea name="content" class="form-control" rows="8" required
			                                 placeholder="Write your answer here..."></textarea>
			                         </div>

			                         <button type="submit" class="btn btn-primary">Post Your Answer</button>
			                     </form>
			                 </sec:authorize>
			            </div>

						<!-- Related Questions -->
						<div class="related-questions">
						    <h4>Related Questions</h4>
						    <div class="list-group">
						        <c:forEach var="rq" items="${relatedQuestions}">
						            <a href="${pageContext.request.contextPath}/questions/${rq.id}" class="list-group-item list-group-item-action">
						                <c:out value="${rq.title}"/>
						                <span class="text-muted float-end">${rq.votes} votes • ${rq.answersCount} answers</span>
						            </a>
						        </c:forEach>
						        
						        <c:if test="${empty relatedQuestions}">
						            <div class="list-group-item">No related questions found</div>
						        </c:if>
						    </div>
						    
							<c:if test="${hasMoreRelatedQuestions}">
							    <div class="text-center mt-3">
									<a href="${pageContext.request.contextPath}/questions/${question.id}?relatedPage=${relatedPage + 1}" class="btn btn-link">
							            <i class="bi bi-chevron-down"></i> Load more related questions
							        </a>
							    </div>
							</c:if>           
                        </div>
		



        <!-- Share this question -->
        <div class="mt-4">
            <h5>Know someone who can answer?</h5>
            <p>Share a link to this question via 
               <a href="mailto:?subject=Question: ${question.title}&body=Check out this question: ${pageContext.request.contextPath}/questions/${question.id}">email</a>, 
               <a href="https://twitter.com/intent/tweet?url=${pageContext.request.contextPath}/questions/${question.id}&text=${question.title}" target="_blank">Twitter</a>, 
               or <a href="https://www.facebook.com/sharer/sharer.php?u=${pageContext.request.contextPath}/questions/${question.id}" target="_blank">Facebook</a>.
            </p>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    
    
   
</body>
</html>
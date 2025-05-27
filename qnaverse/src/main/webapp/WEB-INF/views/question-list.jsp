<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>All Questions</title>
	<meta name="_csrf_parameter" content="${_csrf.parameterName}"/>
	<meta name="_csrf_header" content="${_csrf.headerName}"/>
	<meta name="_csrf" content="${_csrf.token}"/>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet">
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
      rel="stylesheet">
</head>
<body>
<div class="container mt-4">

    <h2>All Questions</h2>
    <a href="${pageContext.request.contextPath}/questions/new"
       class="btn btn-success mb-3">
        Ask New Question
    </a>

    <!-- LIST -->
    <c:forEach var="q" items="${questions}">
        <div class="row align-items-start py-3 border-bottom">

            <!-- votes + buttons -->
            <div class="col-auto text-center pe-4">
                <button type="button"
						class="btn btn-sm p-0 vote-row"
                        data-qid="${q.id}" data-vote="1"
                        title="Up-vote">
                    <i class="bi bi-caret-up-fill"></i>
                </button>

                <div id="votes-${q.id}" class="fw-bold my-1">
                    ${empty q.votes ? 0 : q.votes}
                </div>

                <button type="button"
						class="btn btn-sm p-0 vote-row"
                        data-qid="${q.id}" data-vote="-1"
                        title="Down-vote">
                    <i class="bi bi-caret-down-fill"></i>
                </button>
            </div>

            <!-- title + teaser -->
            <div class="col">
                <h5 class="mb-1">
                    <a class="text-decoration-none"
                       href="${pageContext.request.contextPath}/questions/${q.id}">
                        <c:out value="${q.title}"/>
                    </a>
                </h5>

                <p class="text-muted mb-1">
                    asked by <strong>${fn:substringBefore(q.postedBy,'@')}</strong>
                    on <fmt:formatDate value="${q.postedAt}" pattern="yyyy-MM-dd HH:mm"/>
                </p>

                <c:if test="${not empty q.content}">
                    <p class="mb-0">
                        <c:out value="${fn:length(q.content) > 120
                                       ? fn:substring(q.content,0,120) : q.content}"/>…
                    </p>
                </c:if>
            </div>
        </div>
    </c:forEach>
</div>


<!-- jQuery & vote script -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
	$(function () {
	    // Read CSRF token once
	    var csrfToken = $("meta[name='_csrf']").attr("content");
	    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
	    var csrfParameter = $("meta[name='_csrf_parameter']").attr("content"); // Usually '_csrf'

	    $('.vote-row').on('click', function (e) {
	        e.preventDefault();
	        e.stopPropagation();

	        const btn   = $(this);
	        const qId   = btn.data('qid');
	        const vote  = btn.data('vote');
	        const url   = '${pageContext.request.contextPath}/questions/' + qId + '/vote';

	        // Prepare data including CSRF token
	        var postData = {
	            vote: vote
	        };
	        postData[csrfParameter] = csrfToken; // Add CSRF token to data

	        $.post(url, postData, function (res) { // Send data object
	            if (res.success) { 
	                const box   = $('#votes-' + qId);
	                // Optionally update with count from response: box.text(res.newCount);
	                // Simple increment/decrement based on button clicked:
	                const currentCount = parseInt(box.text(), 10);
	                box.text(currentCount + vote); 
	            } else {
	                alert(res.message || 'Vote failed');
	            }
	        }, 'json')
	        .fail(() => alert('Vote request failed'));
	    });
	});
</script>
</body>
</html>


<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <title>Search Results - QnAVerse</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
</head>
<body class="bg-light">

    <!-- shared navigation / header -->
    <jsp:include page="/WEB-INF/views/header.jsp"/>

    <div class="container mt-5">
        <!-- hero -->
        <div class="text-center mb-5">
            <h1>
                <c:choose>
                    <c:when test="${not empty searchQuery}">
                        Search Results for <span class="text-primary">"${searchQuery}"</span>
                    </c:when>
                    <c:otherwise>
                        Browse Questions
                    </c:otherwise>
                </c:choose>
            </h1>
            <p class="lead">Found ${totalQuestions} result(s)</p>
        </div>

        <!-- Filters section -->
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0">Search Filters</h5>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/questions/search" method="get" id="searchForm">
                    <!-- Primary filters row -->
                    <div class="row g-3 mb-4">
                        <div class="col-md-6">
                            <label class="form-label">Sort by date</label>
                            <select name="sortDate" class="form-select" id="sortDate">
                                <option value="newest" ${sortDate == 'newest' || empty sortDate ? 'selected' : ''}>Newest first</option>
                                <option value="oldest" ${sortDate == 'oldest' ? 'selected' : ''}>Oldest first</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Sort by popularity</label>
                            <select name="sortPopularity" class="form-select" id="sortPopularity">
                                <option value="" ${empty sortPopularity ? 'selected' : ''}>No sort</option>
                                <option value="most-votes" ${sortPopularity == 'most-votes' ? 'selected' : ''}>Most votes</option>
                                <option value="least-votes" ${sortPopularity == 'least-votes' ? 'selected' : ''}>Least votes</option>
                            </select>
                        </div>
                    </div>
                    
                    <!-- Advanced filters (initially hidden) -->
                    <div id="advancedFilters" class="mb-3" style="display: none;">
                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label">Min votes</label>
                                <input type="number" name="minVotes" class="form-control" value="${minVotes}">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Max votes</label>
                                <input type="number" name="maxVotes" class="form-control" value="${maxVotes}">
                            </div>
                        </div>
                        
                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label">Has answers</label>
                                <select name="hasAnswers" class="form-select">
                                    <option value="" ${empty hasAnswers ? 'selected' : ''}>Any</option>
                                    <option value="true" ${hasAnswers == true ? 'selected' : ''}>Has answers</option>
                                    <option value="false" ${hasAnswers == false ? 'selected' : ''}>No answers</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Posted by</label>
                                <input type="email" name="userEmail" class="form-control" value="${userEmail}" placeholder="user@example.com">
                            </div>
                        </div>
                    </div>
                    
                    <!-- Toggle for advanced filters -->
                    <div class="mb-3">
                        <button type="button" class="btn btn-sm btn-outline-secondary" id="toggleAdvancedFilters">
                            <span class="show-text">Show advanced filters</span>
                            <span class="hide-text" style="display:none;">Hide advanced filters</span>
                        </button>
                    </div>
                    
                    <!-- Search input -->
                    <div class="input-group mb-3">
                        <span class="input-group-text"><i class="bi bi-search"></i></span>
                        <input type="text" name="q" class="form-control" placeholder="Search questions..." value="${searchQuery}">
                        <button type="submit" class="btn btn-primary">Search</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Questions list -->
        <div id="questions-list">
            <c:forEach var="q" items="${questions}">
                <!-- question row -->
                <div class="row py-3 border-bottom">
                    <!-- votes & answers -->
                    <div class="col-auto text-center pe-4">
                        <div class="fw-bold">
                            <c:out value="${empty q.votes ? 0 : q.votes}"/>
                        </div>
                        <small class="text-muted">votes</small>

                        <hr class="my-1 w-75"/>

                        <div class="fw-bold">
                            <c:out value="${empty q.answersCount ? 0 : q.answersCount}"/>
                        </div>
                        <small class="text-muted">answers</small>
                    </div>

                    <!-- title & teaser -->
                    <div class="col">
                        <h5 class="mb-1">
                            <a class="text-decoration-none"
                               href="${pageContext.request.contextPath}/questions/${q.id}">
                                <c:out value="${q.title}"/>
                            </a>
                        </h5>

                        <!-- body preview (first 120 chars) -->
                        <c:if test="${not empty q.content}">
                            <p class="text-muted mb-1">
                                <c:out value="${
                                    fn:length(q.content) > 120
                                        ? fn:substring(q.content,0,120) : q.content
                                }"/>…
                            </p>
                        </c:if>

                        <!-- meta -->
                        <small class="text-muted">
                            asked by <strong>${fn:substringBefore(q.postedBy,'@')}</strong> on ${q.formattedDate}
                        </small>
                    </div>
                </div>
                <!-- /question row -->
            </c:forEach>
        </div>

        <!-- If no results -->
        <c:if test="${empty questions}">
            <div class="alert alert-info">No questions found matching your search criteria.</div>
        </c:if>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <div class="row mt-4">
                <div class="col-12">
                    <nav aria-label="Search results pagination">
                        <ul class="pagination justify-content-center">
                            <!-- Previous button -->
                            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/questions/search?q=${searchQuery}&page=${currentPage - 1}&sortDate=${sortDate}&sortPopularity=${sortPopularity}&minVotes=${minVotes}&maxVotes=${maxVotes}&hasAnswers=${hasAnswers}&userEmail=${userEmail}" tabindex="-1">Previous</a>
                            </li>
                            
                            <!-- Page numbers -->
                            <c:forEach begin="1" end="${totalPages}" var="pageNum">
                                <li class="page-item ${pageNum == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/questions/search?q=${searchQuery}&page=${pageNum}&sortDate=${sortDate}&sortPopularity=${sortPopularity}&minVotes=${minVotes}&maxVotes=${maxVotes}&hasAnswers=${hasAnswers}&userEmail=${userEmail}">${pageNum}</a>
                                </li>
                            </c:forEach>
                            
                            <!-- Next button -->
                            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/questions/search?q=${searchQuery}&page=${currentPage + 1}&sortDate=${sortDate}&sortPopularity=${sortPopularity}&minVotes=${minVotes}&maxVotes=${maxVotes}&hasAnswers=${hasAnswers}&userEmail=${userEmail}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>
        </c:if>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    
    <script>
    $(document).ready(function() {
        // Toggle advanced filters
        $('#toggleAdvancedFilters').click(function() {
            $('#advancedFilters').toggle();
            $('.show-text, .hide-text').toggle();
        });
        
        // Auto-submit form when filters change
        $('#sortDate, #sortPopularity').change(function() {
            $('#searchForm').submit();
        });
        
        // If any advanced filters are set, show the advanced filters section
        if ('${minVotes}' || '${maxVotes}' || '${hasAnswers}' || '${userEmail}') {
            $('#advancedFilters').show();
            $('.show-text').hide();
            $('.hide-text').show();
        }
    });
    </script>
</body>
</html>
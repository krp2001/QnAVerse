<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>


<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
</head>
<body class="bg-light">

    <!-- shared navigation / header -->
    <jsp:include page="/WEB-INF/views/header.jsp"/>

    <div class="container mt-5" style="margin-top: 5rem !important;">

        <!-- hero -->
        <div class="text-center mb-5">
            <h1>Welcome to <span class="text-primary">QnAVerse</span></h1>
            <p class="lead">Start exploring questions, post answers, and join the discussion!</p>
        </div>
		
		
		<!-- Filters section -->
		<div class="card mb-3">
		  <div class="card-header">
		    <h5 class="mb-0">Search Filters</h5>
		  </div>
		  <div class="card-body">
		    
			<form action="${pageContext.request.contextPath}/home" method="get" id="searchForm">
			  <div class="row g-3 mb-4">
			    <div class="col-md-6">
			      <select name="sortDate" class="form-select" id=sortDate>
					<option value="newest" ${sortDate == 'newest' || empty sortDate ? 'selected' : ''}>Newest first</option>
					<option value="oldest" ${sortDate == 'oldest' ? 'selected' : ''}>Oldest first</option>
			      </select>
			    </div>
			    <div class="col-md-6">
					<select name="sortPopularity" class="form-select" id="sortPopularity">
					            <option value="" ${empty sortPopularity ? 'selected' : ''}>No popularity sort</option>
					            <option value="most-votes" ${sortPopularity == 'most-votes' ? 'selected' : ''}>Most votes</option>
					            <option value="least-votes" ${sortPopularity == 'least-votes' ? 'selected' : ''}>Least votes</option>
								<option value="most-answers" ${sortPopularity == 'most-answers' ? 'selected' : ''}>Most answered</option>
								<option value="least-answers" ${sortPopularity == 'least-answers' ? 'selected' : ''}>Least answered</option>
					          </select>
			    </div>
				</div>
				
				<!-- Toggle for advanced filters -->
				      <div class="mb-3">
				        <button type="button" class="btn btn-sm btn-outline-secondary" id="toggleAdvancedFilters">
				          <span id="filterToggleText">Show advanced filters</span>
				          <i class="bi bi-chevron-down" id="filterToggleIcon"></i>
				        </button>
				      </div>

				      <!-- Advanced filters (initially hidden) -->
				      <div id="advancedFiltersSection" style="display: none;">
				        <!-- Vote range -->
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

				        <!-- Answer filter and user filter -->
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
				            <label class="form-label">Posted by user</label>
				            <input type="text" name="userEmailParam" class="form-control" value="${userEmailParam}" placeholder="username or email">
				          </div>
				        </div>
				      </div>
					  
				<div class="input-group mb-3">
				        <span class="input-group-text"><i class="bi bi-search"></i></span>
				        <input type="text" name="q" class="form-control" placeholder="Search questions..." value="${searchQuery}">
				        <button type="submit" class="btn btn-primary">Search</button>
						

						<c:if test="${isSearch}">
						  <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary">
						    <i class="bi bi-x"></i> Clear
						  </a>
						</c:if>
			  </div>
			</form>
		  </div>
		</div>
		
    <!-- RECENT QUESTIONS -->
		<h3>
		  <c:choose>
		    <c:when test="${isSearch}">
		      <c:if test="${not empty searchQuery}">
		        Search Results for "${searchQuery}"
		      </c:if>
		      <c:if test="${empty searchQuery}">
		        Filtered Questions
		      </c:if>
		      <small class="text-muted">(${totalQuestions} results)</small>
		    </c:when>
		    <c:otherwise>
		      Recent Questions
		    </c:otherwise>
		  </c:choose>
		</h3>
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
						asked by <strong>${fn:substringBefore(q.postedBy,'@')}</strong> 				
						<c:if test="${not empty q.author}"> 
						    <span class="badge bg-secondary ms-1" title="Reputation">${q.author.reputation}</span>
						
					
						</c:if>
						on <fmt:formatDate value="${q.postedAt}" pattern="yyyy-MM-dd HH:mm"/>
					</small>
                </div>

            </div>
			
            <!-- /question row -->

        </c:forEach>	
	</div>
		<!-- Pagination -->
		<div class="row mt-4">
		    <div class="col-12">
		        <nav aria-label="Question pagination">
		            <ul class="pagination justify-content-center">
		                <!-- Previous button -->
		                <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
							<a class="page-link" href="${pageContext.request.contextPath}/home?page=${currentPage - 1}&q=${searchQuery}&sortDate=${sortDate}&sortPopularity=${sortPopularity}" tabindex="-1" aria-disabled="${currentPage <= 1}">Previous</a>
		                </li>
		                
		                <!-- Page numbers -->
		                <c:forEach begin="1" end="${totalPages}" var="pageNum">
		                    <li class="page-item ${pageNum == currentPage ? 'active' : ''}">
								<a class="page-link" href="${pageContext.request.contextPath}/home?page=${pageNum}&q=${searchQuery}&sortDate=${sortDate}&sortPopularity=${sortPopularity}">${pageNum}</a>
		                    </li>
		                </c:forEach>
		                
		                <!-- Next button -->
		                <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
							<a class="page-link" href="${pageContext.request.contextPath}/home?page=${currentPage + 1}&q=${searchQuery}&sortDate=${sortDate}&sortPopularity=${sortPopularity}" aria-disabled="${currentPage >= totalPages}">Next</a>
		                </li>
		            </ul>
		        </nav>
		    </div>
		</div>
		<!-- End Pagination -->
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

	<!-- JavaScript for live search -->
	<script>
	$(document).ready(function() {
		
		// Auto-submit form when filters change
		$('#sortDate, #sortPopularity').change(function() {
		  $('#searchForm').submit();
		});
		
		// Toggle advanced filters
		  $('#toggleAdvancedFilters').on('click', function() {
		    console.log("Toggle button clicked");
		    $('#advancedFiltersSection').toggle();
		    
		    if ($('#advancedFiltersSection').is(':visible')) {
		      $('#filterToggleText').text('Hide advanced filters');
		      $('#filterToggleIcon').removeClass('bi-chevron-down').addClass('bi-chevron-up');
		    } else {
		      $('#filterToggleText').text('Show advanced filters');
		      $('#filterToggleIcon').removeClass('bi-chevron-up').addClass('bi-chevron-down');
		    }
		  });
		  
		  // Show advanced filters section if any advanced filters are set
		    if ('${minVotes}' !== '' || '${maxVotes}' !== '' || '${hasAnswers}' !== '' || '${userEmailParam}' !== '') {
		      $('#advancedFiltersSection').show();
		      $('#filterToggleText').text('Hide advanced filters');
		      $('#filterToggleIcon').removeClass('bi-chevron-down').addClass('bi-chevron-up');
		    }
		  

	  var searchTimeout;
	  
	  $('#live-search').on('input', function() {
	    clearTimeout(searchTimeout);
	    
	    var query = $(this).val().trim();
	    
	    if (query.length >= 1) {
	      searchTimeout = setTimeout(function() {
	        // Get filter values from your form-based filters
	        var sortDate = $('select[name="sortDate"]').val();
	        var sortPopularity = $('select[name="sortPopularity"]').val();
	        
	        // Determine which sort to use
	        var sortBy = sortDate === 'oldest' ? sortDate : sortPopularity;
	            
	        $.ajax({
	          type: "GET",
	          url: "${pageContext.request.contextPath}/questions/search-ajax",
	          data: { 
	            q: query,
	            sort: sortBy
	          },
	          dataType: "json",
	          success: function(data) {
	            displayResults(data);
	          },
	          error: function(xhr, status, error) {
	            console.error("AJAX error:", status, error);
	            console.error("Response:", xhr.responseText);
	          }
	        });
	      }, 300);
	    } else {
	      $('#search-results-container').html('');
	      $('#questions-list').show();
	      $('h3').show();
	    }
	  });

	  
	  // Add event listeners for filter changes with live search
	    $(document).on('change', 'select[name="sortDate"], select[name="sortPopularity"]', function() {
	      var query = $('#live-search').val().trim();
	      if (query.length >= 1) {
	        $('#live-search').trigger('input');
	      }
	    });
	  
	  function displayResults(questions) {
	      var container = $('#search-results-container');
	      container.html('');
	      
	      if (!questions || questions.length === 0) {
	          container.html('<div class="alert alert-info">No questions found</div>');
			  container.show();
			  $('#questions-list').hide();
	          $('h3').hide();
	          return;
	      }
	      
	      var resultsHtml = '<div class="list-group">';
	      
	      for (var i = 0; i < questions.length; i++) {
	          var q = questions[i];
	          var username = q.postedBy.split('@')[0] || q.postedBy;
	          
	          resultsHtml += '<a href="${pageContext.request.contextPath}/questions/' + q.id + '" class="list-group-item list-group-item-action">' +
	                         '<div class="d-flex justify-content-between">' +
	                         '<h6 class="mb-1">' + q.title + '</h6>';
	          
	          // Add badge if it's a user match
	          if (q.matchType === 'user') {
	              resultsHtml += '<span class="badge bg-info">User match</span>';
	          }
	          
	          resultsHtml += '</div>' +
	                         '<small class="text-muted">Asked by ' + username + '</small>' +
	                         '</a>';
	      }

	    resultsHtml += '</div>';
	    
	    container.html(resultsHtml);
	    $('#questions-list').hide();
	    $('h3').hide();
	  }
	});
	</script>	
	
	</body>
</html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<style>
  body {
    padding-top: 80px; /* Increase spacing between navbar and content */
  }
  
  .navbar .input-group {
    width: 400px; /* Make search bar wider */
  }
</style>

<nav class="navbar navbar-expand-lg navbar-dark bg-primary fixed-top">
  <div class="container">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/home">QnAVerse</a>
    

		
		<!--Search bar-->
		<div class="flex-grow-1 me-3">
		  <div class="input-group">
		    <span class="input-group-text"><i class="bi bi-search"></i></span>
		    <input type="text" id="live-search" class="form-control" placeholder="Search questions..." autocomplete="off">
		  </div>
		  <div id="search-results-container" class="mt-2 position-absolute" style="z-index: 1050;"></div>
		</div>
		
    <div class="d-flex align-items-center">
      <!--  Ask Question button - only shown when logged in -->
	  <sec:authorize access="isAuthenticated()">
	    <a href="${pageContext.request.contextPath}/questions/new" class="btn btn-light me-3">Ask Question</a>
        
		<div class="dropdown me-3">
		  <a class="nav-link dropdown-toggle text-white" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
		    <sec:authentication property="principal.username" var="fullUsername" scope="page"/>
		    <i class="bi bi-person-circle"></i> ${fn:substringBefore(fullUsername, '@')}
		  </a>
		  <ul class="dropdown-menu">
		    <li>
		      <a class="dropdown-item" href="${pageContext.request.contextPath}/users/profile-by-email?email=${fullUsername}">
		        <i class="bi bi-person"></i> My Profile
		      </a>
		    </li>
		    <li><hr class="dropdown-divider"></li>
		    <li>
		      <form action="${pageContext.request.contextPath}/users/logout" method="post" class="d-inline">
		        <sec:csrfInput />
		        <button type="submit" class="dropdown-item"><i class="bi bi-box-arrow-right"></i> Logout</button>
		      </form>
		    </li>
		  </ul>
		</div>
		     </sec:authorize>

		     <!--  Login/Register ONLY if NOT authenticated -->
		     <sec:authorize access="!isAuthenticated()">
		       <a href="${pageContext.request.contextPath}/users/login" class="btn btn-outline-light btn-sm me-2">Login</a>
		       <a href="${pageContext.request.contextPath}/users/register" class="btn btn-light btn-sm">Register</a>
		     </sec:authorize>
    </div>
  </div>
</nav>
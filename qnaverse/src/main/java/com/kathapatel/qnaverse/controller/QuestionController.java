package com.kathapatel.qnaverse.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import com.kathapatel.qnaverse.dao.QuestionDAO;
import com.kathapatel.qnaverse.dao.VoteDAO;
import com.kathapatel.qnaverse.model.Question;
import com.kathapatel.qnaverse.model.SearchCriteria;
import com.kathapatel.qnaverse.model.User;
import com.kathapatel.qnaverse.model.Vote;
import com.kathapatel.qnaverse.service.UserService;
import com.kathapatel.qnaverse.service.UserService.Privilege;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//Handles web requests related to Questions: creating, viewing, editing, deleting, searching, voting

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionDAO questionDAO;
    
    @Autowired 
    private VoteDAO voteDAO; 
    
    @Autowired
    private UserService userService;
    

    //Shows the form for asking a new question
    @GetMapping("/new")
    public String showPostForm(Model model, Principal principal, RedirectAttributes redirectAttributes) {
         if (principal == null) {
             redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to ask a question.");
             return "redirect:/users/login";
         }
         // Check reputation privilege using UserService
         if (!userService.hasPrivilege(principal.getName(), Privilege.POST_QUESTION)) {
              redirectAttributes.addFlashAttribute("errorMessage", "You need at least " + Privilege.POST_QUESTION.getRequiredReputation() + " reputation to ask a question.");
              return "redirect:/home"; 
         }

      // Add an empty Question object for the form
        model.addAttribute("question", new Question());
        return "ask-question";
    }

    
    //Processes the submission of a new question
    @PostMapping("/post")
    public String postQuestion(@ModelAttribute("question") Question question, Principal principal, RedirectAttributes redirectAttributes) {
         if (principal == null) {
             return "redirect:/users/login";
         }
         String email = principal.getName();

         if (!userService.hasPrivilege(email, Privilege.POST_QUESTION)) {
             redirectAttributes.addFlashAttribute("errorMessage", "You need at least " + 
                 Privilege.POST_QUESTION.getRequiredReputation() + " reputation to ask a question.");
             return "redirect:/home";
         }

         // Get the User entity for the current user email
         User user = userService.getUserByEmail(email);

        question.setPostedBy(email); // Set the email string
        question.setAuthor(user); // Set the User object association
        question.setPostedAt(LocalDateTime.now());
        question.setVotes(0);

        questionDAO.saveQuestion(question);
        redirectAttributes.addFlashAttribute("successMessage", "Question posted successfully!");
        return "redirect:/home";
    }
    
    @GetMapping
    public String viewAll(Model model) {
        model.addAttribute("questions", questionDAO.getAllQuestions());
        return "question-list";
    }
    
   //Handles search requests that likely update a portion of the page
    @GetMapping("/search")
    public String searchQuestions(
            @RequestParam(value = "q", required = false, defaultValue = "") String query,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortDate", defaultValue = "newest") String sortDate,
            @RequestParam(name = "sortPopularity", defaultValue = "") String sortPopularity,
            @RequestParam(name = "minVotes", required = false) Integer minVotes,
            @RequestParam(name = "maxVotes", required = false) Integer maxVotes,
            @RequestParam(name = "hasAnswers", required = false) Boolean hasAnswers,
            @RequestParam(name = "userEmailParam", required = false) String userEmailSearchParam,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            Model model) {
        
        // Build search criteria
        SearchCriteria criteria = new SearchCriteria();
        criteria.setQuery(query);
        
        // Set search fields (all true by default)
        criteria.setSearchTitle(true);
        criteria.setSearchContent(true);
        criteria.setSearchUser(true);
        
        // Set sorting
        if ("oldest".equals(sortDate)) {
            criteria.setSortField("postedAt");
            criteria.setSortDirection("asc");
        } else if ("most-votes".equals(sortPopularity)) {
            criteria.setSortField("votes");
            criteria.setSortDirection("desc");
        } else if ("least-votes".equals(sortPopularity)) {
            criteria.setSortField("votes");
            criteria.setSortDirection("asc");
        } else {
            // Default: newest first
            criteria.setSortField("postedAt");
            criteria.setSortDirection("desc");
        }
        
        // Set additional filters
        criteria.setMinVotes(minVotes);
        criteria.setMaxVotes(maxVotes);
        criteria.setHasAnswers(hasAnswers);
        criteria.setUserEmail(userEmailSearchParam);
        criteria.setFromDate(fromDate);
        criteria.setToDate(toDate);
        
        // Calculate pagination
        int offset = (page - 1) * size;
        
        // Execute search
        List<Question> questions = questionDAO.searchWithCriteria(criteria, offset, size);
        long totalQuestions = questionDAO.countWithCriteria(criteria);
        
        int totalPages = (int) Math.ceil((double) totalQuestions / size);
        if (totalPages == 0) totalPages = 1;
        
        // Add to model
        model.addAttribute("questions", questions);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("pageSize", size);
        model.addAttribute("searchQuery", query);
        model.addAttribute("sortDate", sortDate);
        model.addAttribute("sortPopularity", sortPopularity);
        model.addAttribute("minVotes", minVotes);
        model.addAttribute("maxVotes", maxVotes);
        model.addAttribute("hasAnswers", hasAnswers);
        model.addAttribute("userEmail", userEmailSearchParam); 
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        
        
        
        return "search-results-fragment"; 
    }
    
    //Handles AJAX search requests for autocomplete suggestions
    @GetMapping("/search-ajax")
    @ResponseBody
    public String searchQuestionsAjax(@RequestParam("q") String query,
    		@RequestParam(value = "title", defaultValue = "true") boolean searchTitle,
            @RequestParam(value = "content", defaultValue = "true") boolean searchContent,
            @RequestParam(value = "user", defaultValue = "true") boolean searchUser,
            @RequestParam(value = "sort", defaultValue = "newest") String sortBy,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            HttpServletResponse response) {
    	
    	//set content type to json
        response.setContentType("application/json");
        
     
        
        List<Question> questions = questionDAO.searchQuestionsWithFilters(
                query, searchTitle, searchContent, searchUser, sortBy, 0, limit); //offset 0
                    
        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            if (i > 0) {
                jsonBuilder.append(",");
            }
            
            // Determine match type
            String matchType = "";
            if (q.getTitle().toLowerCase().contains(query.toLowerCase())) {
                matchType = "title";
            } else if (q.getContent() != null && q.getContent().toLowerCase().contains(query.toLowerCase())) {
                matchType = "content";
            } else if (q.getPostedBy().toLowerCase().contains(query.toLowerCase())) {
                matchType = "user";
            }
            
            // Append JSON object for the question suggestion
            jsonBuilder.append("{\"id\":").append(q.getId())
                      .append(",\"title\":\"").append(escapeJson(q.getTitle())).append("\"")
                      .append(",\"postedBy\":\"").append(escapeJson(q.getPostedBy())).append("\"")
                      .append(",\"matchType\":\"").append(matchType).append("\"")
                      .append("}");
        }
        jsonBuilder.append("]");
        
        return jsonBuilder.toString();
    }

	private String escapeJson(String input) {
	    if (input == null) {
	        return "";
	    }
	    return input.replace("\\", "\\\\")
	               .replace("\"", "\\\"")
	               .replace("\n", "\\n")
	               .replace("\r", "\\r")
	               .replace("\t", "\\t");
	}
	
	//Displays the detailed view for a single question including its answers and related questions
	@GetMapping("/{id}")
	public String viewQuestion(
	        @PathVariable("id") Long id,
	        @RequestParam(value = "editQuestion", required = false) Boolean editQuestion,
	        @RequestParam(value = "editAnswer", required = false) Long editingAnswerId,
	        @RequestParam(value = "relatedPage", defaultValue = "1") int relatedPage,
	        Model model, Principal principal) {
	    
	    Question question = questionDAO.getQuestionById(id);
	    
	    if (question == null) {
	        return "redirect:/home";
	    }
	    
	    // Check if it is editing the question
	    if (editQuestion != null && editQuestion && principal != null) {
	        String email = principal.getName();
	        // Check if the current user is the author
	        if (question.getPostedBy().equals(email)) {
	            model.addAttribute("editingQuestion", true);
	        }
	    }
	    
	    // Increment view count
	    questionDAO.incrementViewCount(id);
	    
	    model.addAttribute("question", question);
	    
	    // If we're editing an answer, set the editingAnswerId attribute
	    if (editingAnswerId != null) {
	        model.addAttribute("editingAnswerId", editingAnswerId);
	    }
	    
	    // Get related questions with pagination
	    int relatedQuestionsPerPage = 5;
	    int offset = (relatedPage - 1) * relatedQuestionsPerPage;
	    List<Question> relatedQuestions = questionDAO.getRelatedQuestions(id, offset, relatedQuestionsPerPage + 1);
	    
	    // Check if there are more related questions
	    boolean hasMoreRelatedQuestions = false;
	    if (relatedQuestions.size() > relatedQuestionsPerPage) {
	        hasMoreRelatedQuestions = true;
            // Trim the list back to the desired page size
	        relatedQuestions = relatedQuestions.subList(0, relatedQuestionsPerPage);
	    }
	    
	    model.addAttribute("relatedQuestions", relatedQuestions);
	    model.addAttribute("relatedPage", relatedPage);
	    model.addAttribute("hasMoreRelatedQuestions", hasMoreRelatedQuestions);
	    
	    // Add user info
	    if (principal != null) {
	        model.addAttribute("userEmail", principal.getName());
	    }
	    
	    return "question-detail";
	}

	//Handles voting (up or down) on a specific question
	@PostMapping("/{id}/vote")
	public String voteQuestion(@PathVariable("id") Long id, 
	                         @RequestParam("vote") int vote,
	                         Principal principal,
	                         HttpServletRequest request, 
	                         RedirectAttributes redirectAttributes) {
	    
	    if (principal == null) {
	        return "redirect:/users/login";
	    }
	    
	    String userEmail = principal.getName();
	    Question question = questionDAO.getQuestionById(id);
	    
	    
	    // Check downvote privilege
	    if (vote == -1 && !userService.hasPrivilege(userEmail, Privilege.DOWNVOTE)) {
	        redirectAttributes.addFlashAttribute("errorMessage", "You need at least " + 
	            Privilege.DOWNVOTE.getRequiredReputation() + " reputation to downvote.");
	        return "redirect:/questions/" + id;
	    }
	    
	    // Process the vote as normal, even for self-votes
	    Vote existingVote = voteDAO.getVoteByUserAndContent(userEmail, 1, id);
	    
	    if (existingVote != null) {
	        // Handle existing vote
	        if (existingVote.getValue() == vote) {
	            redirectAttributes.addFlashAttribute("infoMessage", "You have already voted this way.");
	        } else {
	            // Change vote
	            int previousVoteValue = existingVote.getValue();
	            existingVote.setValue(vote);
	            boolean voteUpdated = voteDAO.updateVote(existingVote);
	            if (voteUpdated) {
	                int scoreChange = vote - previousVoteValue;
	                boolean scoreUpdated = questionDAO.updateVote(id, scoreChange);
	                
	                // Update reputation
	                if (!userEmail.equals(question.getPostedBy())) {
	                    userService.updateReputationForQuestionVoteChange(id, userEmail, previousVoteValue, vote);
	                }
	                
	                if (scoreUpdated) {
	                    redirectAttributes.addFlashAttribute("successMessage", "Vote updated.");
	                }
	            }
	        }
	    } else {
	        // New vote
	        Vote newVote = new Vote(userEmail, 1, id, vote);
	        boolean voteSaved = voteDAO.saveVote(newVote);
	        if (voteSaved) {
	            boolean scoreUpdated = questionDAO.updateVote(id, vote);
	            
	            // Update reputation 
	            if (!userEmail.equals(question.getPostedBy())) {
	                userService.updateReputationForQuestionVote(id, userEmail, vote);
	            }
	            
	            if (scoreUpdated) {
	                redirectAttributes.addFlashAttribute("successMessage", "Vote recorded.");
	            }
	        }
	    }
	    
	    String referer = request.getHeader("Referer");
	    return "redirect:" + (referer != null ? referer : "/questions/" + id);
	}
	
	//Shows the edit form for a question
	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id,
								Principal principal,
								Model model) {

		if (principal == null) {
            return "redirect:/users/login?error=auth";
        }
		String email = principal.getName();
        Question q = questionDAO.getQuestionById(id);  // Fetch the question to edit


	    if (q == null || !q.getPostedBy().equals(email))   // only owner may edit
	        return "redirect:/questions/" + id;

	    model.addAttribute("question", q);
	    return "ask-question";            
	}

	
	//Processes the submission of an edited question
	@PostMapping("/{id}/update")
	public String processEdit(@PathVariable Long id,
	                          @ModelAttribute("question") Question form,
	                          Principal principal,
	                          RedirectAttributes redirectAttributes) {

		if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Authentication error.");
            return "redirect:/users/login?error=auth";
       }
       String email = principal.getName(); // Get email of logged-in user
       
       // Set the ID on the form object, as it's not part of the standard form fields usually
        form.setId(id);
	    boolean ok = questionDAO.updateQuestion(form, email);

	    if (!ok) {  // Update failed, likely because question not found or user is not the owner
            redirectAttributes.addFlashAttribute("errorMessage", "You’re not allowed to edit this question or it was not found.");
	        return "redirect:/questions/" + id;
	    }
        // Update successful
        redirectAttributes.addFlashAttribute("successMessage", "Question updated successfully.");
	    return "redirect:/questions/" + id;
	}

	//Handles the request to delete a question
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id,
								Principal principal,
								RedirectAttributes redirectAttributes) {

		if (principal == null) {
            // Should not happen if endpoint is secured
            redirectAttributes.addFlashAttribute("errorMessage", "Authentication error.");
            return "redirect:/users/login?error=auth";
       }
       String email = principal.getName();
       
       // Attempt to delete the question via DAO, passing email for ownership check
	    boolean ok = questionDAO.deleteById(id, email);

	    if (ok) {
            redirectAttributes.addFlashAttribute("successMessage", "Question deleted");
	        return "redirect:/home";                
	    }
        redirectAttributes.addFlashAttribute("errorMessage", "Not allowed to delete this question or it was not found.");
	    return "redirect:/questions/" + id;
	}
	
	
	
}
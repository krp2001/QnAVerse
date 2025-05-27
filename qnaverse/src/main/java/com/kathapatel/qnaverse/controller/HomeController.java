package com.kathapatel.qnaverse.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kathapatel.qnaverse.dao.QuestionDAO;
import com.kathapatel.qnaverse.model.Question;
import com.kathapatel.qnaverse.model.SearchCriteria;


/**
 * Handles requests for the home page, displaying questions with pagination,
 * searching, and filtering capabilities.
 */

@Controller
public class HomeController {

    @Autowired
    private QuestionDAO questionDAO;
    
    /**
     * Displays the home page or search results page.
     * Handles various request parameters for searching, filtering, sorting, and pagination.
     */

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortDate", defaultValue = "newest") String sortDate,
            @RequestParam(name = "sortPopularity", required = false) String sortPopularity,
            @RequestParam(name = "minVotes", required = false) Integer minVotes,
            @RequestParam(name = "maxVotes", required = false) Integer maxVotes,
            @RequestParam(name = "hasAnswers", required = false) Boolean hasAnswers,
            @RequestParam(name = "userEmailParam", required = false) String userEmailSearchParam,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
          
            Model model, Principal principal) {
    	
        // Use a SearchCriteria object to bundle all the parameters neatly
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
        } else if ("most-answers".equals(sortPopularity)) {
            criteria.setSortField("answers");
            criteria.setSortDirection("desc");
        } else if ("least-answers".equals(sortPopularity)) {
            criteria.setSortField("answers");
            criteria.setSortDirection("asc");
        } else {
            // Default: newest first
            criteria.setSortField("postedAt");
            criteria.setSortDirection("desc");
        }
        
        // Apply additional filters from request parameters
        criteria.setMinVotes(minVotes);
        criteria.setMaxVotes(maxVotes);
        criteria.setHasAnswers(hasAnswers);
        criteria.setUserEmail(userEmailSearchParam);
        criteria.setFromDate(fromDate);
        criteria.setToDate(toDate);
        
        // Calculate pagination
        int offset = (page - 1) * size;
        
        // Determine if this is a search or regular home page view
        boolean isSearch = query != null && !query.isEmpty() || 
                          !"newest".equals(sortDate) || 
                          sortPopularity != null ||
                          minVotes != null ||
                          maxVotes != null ||
                          hasAnswers != null ||
                          userEmailSearchParam != null;
                          
        // Fetch the appropriate list of questions and the total count based on whether it's a search
        List<Question> questions;
        long totalQuestions;
        
        if (isSearch) {
            // Perform a search/filter query using the criteria
            questions = questionDAO.searchWithCriteria(criteria, offset, size);
            // Get the total count matching the criteria for pagination
            totalQuestions = questionDAO.countWithCriteria(criteria);
        } else {
            // Just get the default paginated list of questions
            questions = questionDAO.getPaginatedQuestions(offset, size);
            // Get the total count of all questions
            totalQuestions = questionDAO.countQuestions();
        }
        
        // Calculate total pages needed for pagination controls
        int totalPages = (int) Math.ceil((double) totalQuestions / size);
        if (totalPages == 0) totalPages = 1;   // Avoid totalPages being 0 if there are no questions

        
        // Add all the necessary data to the Spring Model to be used in the view
        model.addAttribute("questions", questions); // The list of questions for the current page
        model.addAttribute("currentPage", page); // The current page number
        model.addAttribute("totalPages", totalPages); // Total number of pages
        model.addAttribute("totalQuestions", totalQuestions); // Total questions matching criteria
        model.addAttribute("pageSize", size); // Items per page
        model.addAttribute("searchQuery", query);
        model.addAttribute("sortDate", sortDate);
        model.addAttribute("sortPopularity", sortPopularity);
        model.addAttribute("minVotes", minVotes);
        model.addAttribute("maxVotes", maxVotes);
        model.addAttribute("hasAnswers", hasAnswers);
        model.addAttribute("userEmailParam", userEmailSearchParam);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("isSearch", isSearch); // Flag to indicate if search results are being shown
        
        // Add user info if authenticated
        if (principal != null) {
            model.addAttribute("userEmail", principal.getName());
        }
        
        return "home";
    }
}

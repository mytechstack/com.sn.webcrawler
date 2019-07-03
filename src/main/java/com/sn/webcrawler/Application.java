package com.sn.webcrawler;

import com.sn.webcrawler.service.SimpleSearchService;

/**
 * Main application for crawling through list of urls and performing search operation
 * @author snayak
 *
 */
public class Application {

	// Search terms for searching through page documents
	public final static String[] SEARCH_TERMS = { "Sign Up", "Business", "Blogs", "Gordon Ramsay", "Dream", "bigger", "Creative", "Cloud" };

	// Max capacity of queue for handling search requests
	public final static int QUEUE_BOUND = 20;
	
	// Number of consumers for processing messages
	public final static int N_CONSUMERS = Runtime.getRuntime().availableProcessors();

	// Search results output containing search term, occurrence count and url
	public final static String SEARCH_RESULTS_OUTPUT_FILE = "results.txt";

	public static void main(String[] args) {
		
		// Create new search service for crawling through the list of urls 
		SimpleSearchService searchService = new SimpleSearchService();
		
		// Start search service for starting producers and consumers to process list of urls
		searchService.startSearch();

	}


}

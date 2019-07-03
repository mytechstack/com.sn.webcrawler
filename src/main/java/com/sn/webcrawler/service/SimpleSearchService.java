package com.sn.webcrawler.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.sn.webcrawler.Application;
import com.sn.webcrawler.concurrency.Message;
import com.sn.webcrawler.concurrency.SearchResultsConsumer;
import com.sn.webcrawler.concurrency.SearchResultsProducer;
import com.sn.webcrawler.util.SearchUtil;

/**
 * A simple search service for searching through the list of urls in a concurrent fashion
 * @author snayak
 *
 */
public class SimpleSearchService {

	/**
	 * Start searching through the list of urls
	 * Uses shared blocking queue between the producers and consumers for handling concurrent processing of search
	 * 
	 */
	public void startSearch() {
		
		final AtomicInteger activeProducers = new AtomicInteger();
		final BlockingQueue<Message> queue = new LinkedBlockingQueue<>(Application.QUEUE_BOUND);
		List<String> searchUrls = SearchUtil.getListOfSearchUrls();
		
		if (null != searchUrls) {
			
			int NPRODUCERS = searchUrls.size();
			
			startProducers(activeProducers, queue, searchUrls, NPRODUCERS);
			startConsumers(activeProducers, queue);
		}

	}
	
	/**
	 * Start producers for processing list of urls
	 * 
	 * @param activeProducers number of active producers
	 * @param queue shared bounded queue
	 * @param urls list of urls to producers
	 * @param numberOfProducers number of producers
	 */
	private void startProducers(final AtomicInteger activeProducers, final BlockingQueue<Message> queue, List<String> urls, int numberOfProducers) {
		// clear search results file before starting producers
		clearSearchResultsFile();
		
		// Start producers searching through list of urls
		for (int i = 1; i < numberOfProducers; i++) {
			activeProducers.incrementAndGet();
			new Thread(new SearchResultsProducer(queue, urls.get(i))).start();
		}
	}

	/**
	 * Clear contents of result file before writing
	 */
	private void clearSearchResultsFile() {
		File file = new File(Application.SEARCH_RESULTS_OUTPUT_FILE);
		final PrintWriter writer;
		try {
			writer = new PrintWriter(file);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Start consumers for consuming messages produced by produces
	 * @param activeProducers number of active produces
	 * @param queue shared queue
	 */
	private static void startConsumers(final AtomicInteger activeProducers, final BlockingQueue<Message> queue) {
		// Start consumers for consuming messages from producers and writing the search results
		for (int j = 0; j < Application.N_CONSUMERS; j++) {
			new Thread(new SearchResultsConsumer(queue, activeProducers)).start();
		}
	}

}

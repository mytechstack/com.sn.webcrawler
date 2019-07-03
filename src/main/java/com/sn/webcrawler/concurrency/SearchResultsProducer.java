package com.sn.webcrawler.concurrency;

import java.util.concurrent.BlockingQueue;
import org.jsoup.nodes.Document;

import com.sn.webcrawler.util.SearchUtil;

/**
 * Produces search results by fetching document from a url and performing search operation on the document
 * Uses producer/consumer pattern for concurrently crawling through urls
 * Blocking queue is used to share data between producer and consumer 
 * 
 * @author snayak
 *
 */
public class SearchResultsProducer implements Runnable {

	private final BlockingQueue<Message> queue;

	private final String url;

	public SearchResultsProducer(BlockingQueue<Message> queue, String url) {
		this.queue = queue;
		this.url = url;
	}

	@Override
	public void run() {
		try {
			// Fetch page from url
			Document doc = SearchUtil.pageFetcher(url);
			if (null != doc) {
				String documentString = doc.toString();
				// Search page document for search terms
				String searchResult = SearchUtil.searchDocument(documentString, url);
				Message message = new Message(searchResult);
				System.out.println("Producing " + message.getContent());
				// Add message to the queue
				queue.put(message);
			}else {
				queue.put(new Message(""));
			}
		} catch (InterruptedException e) {
				System.out.println("error wen prod");
		}
	}

}
package com.sn.webcrawler.concurrency;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.sn.webcrawler.util.SearchUtil;

/**
 * Consumes search results produced by the produces and writes results to results.txt file
 * @author snayak
 *
 */
public class SearchResultsConsumer implements Runnable {

	// shared queue for reading message from producer
	private final BlockingQueue<Message> queue;
	
	// active number of producers 
	private AtomicInteger activeProducers = new AtomicInteger();

	public SearchResultsConsumer(BlockingQueue<Message> queue) {
		this.queue = queue;
	}

	public SearchResultsConsumer(BlockingQueue<Message> queue, AtomicInteger activeProducers) {
		this.queue = queue;
		this.activeProducers = activeProducers;
	}

	@Override
	public void run() {
		try {
			for (;;) {
				// read queue element
				final Message queueElement = queue.poll(1, TimeUnit.SECONDS);
				if (queueElement != null) {
					// check for queue contents
					if(queueElement.getContent()!="") {
						// write search results 
						SearchUtil.writeSearchResult(queueElement.getContent());
					}
					activeProducers.decrementAndGet();
				// exit consumer if there are no more active producers or queue is empty
				} else if (activeProducers.get() == 0 && queue.peek() == null) 
					return;
			}
		} catch (InterruptedException ex) {
			System.err.println("Consumer terminated early: " + ex);
		} catch (IOException ioException) {
			System.err.println(" Error occurred when writing search results to file " + ioException);
		}
	}

}
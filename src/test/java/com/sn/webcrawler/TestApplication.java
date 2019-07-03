package com.sn.webcrawler;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.nodes.Document;
import org.junit.Test;

import com.sn.webcrawler.concurrency.Message;
import com.sn.webcrawler.concurrency.SearchResultsConsumer;
import com.sn.webcrawler.concurrency.SearchResultsProducer;
import com.sn.webcrawler.util.SearchUtil;

import junit.framework.TestCase;

public class TestApplication extends TestCase {

	SearchUtil searchUtil;

	@Test
	public void test_getListofUrls() {
		List<String> results = SearchUtil.getListOfSearchUrls();
		assertNotNull(results);
		assertTrue(results.size() != 0);
		assertEquals(results.get(0),"facebook.com/");
	}

	@Test
	public void test_getDocument() {
		Document doc = SearchUtil.pageFetcher("facebook.com/");
		assertNotNull(doc);
		assertNotNull(doc.body());
	}

	@Test
	public void test_ProducerConsumer() throws InterruptedException {
		String url="facebook.com/";
		final AtomicInteger activeProducers = new AtomicInteger();
		BlockingQueue<Message> queue = new LinkedBlockingQueue<>(20);
		new Thread(new SearchResultsProducer(queue , url)).start();;
		activeProducers.incrementAndGet();
		Thread.sleep(1000);
		assertEquals(queue.size(), 1);
		new Thread(new SearchResultsConsumer(queue,activeProducers)).start();
		Thread.sleep(1000);
		assertEquals(queue.size(), 0);
	}

	@Test
	public void test_search() {
		String result = SearchUtil.searchDocument("cloud business xyz business blogs", "facebook.com/");
		System.out.println(result);
		assertNotNull(result);
		assertTrue(result.contains("Search term Business occurred 2 times in facebook.com"));
		assertTrue(result.contains("Search term Blogs occurred 1 times in facebook.com/"));
	}

}

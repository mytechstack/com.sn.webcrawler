package com.sn.webcrawler.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.sn.webcrawler.Application;

/**
 * A utility class for handling search requests from producers and consumers
 * @author snayak
 *
 */
public class SearchUtil {

	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final HttpRequestFactory HTTP_REQUEST_FACTORY = HTTP_TRANSPORT.createRequestFactory();
	static final String URLS = "https://s3.amazonaws.com/fieldlens-public/urls.txt";
	
	/**
	 * Fetch list of urls to search
	 * @return
	 */
	public static List<String> getListOfSearchUrls() {
		final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();

		try {
			final GenericUrl url = new GenericUrl(URLS);
			final HttpRequest request = requestFactory.buildGetRequest(url);
			final HttpResponse httpResponse = request.execute();
			final int statusCode = httpResponse.getStatusCode();
			List<String> result = new ArrayList<>();
			if (statusCode == HttpStatusCodes.STATUS_CODE_OK) {
				final InputStream responseStream = httpResponse.getContent();
				final List<String> urls = new BufferedReader(new InputStreamReader(responseStream)).lines().collect(Collectors.toList());
				result = urls.subList(1, urls.size()).stream().map(s -> s.split(",")[1].replace("\"", "")).collect(Collectors.toList());
			}
			return result;
		} catch (Exception e) {
			System.out.println("Error occured when fetching list of search urls " + e);
			e.printStackTrace();

		}
		return null;

	}

	/**
	 * Fetch page document from url 
	 * @param url
	 * @return
	 */
	public static Document pageFetcher(String url) {
		try {
			if (null != url) {
				if (!url.contains("http://") || !url.contains("https://")) {
					url = "https://" + url;
				}
				Jsoup.connect(url).timeout(500);
				Document document = Jsoup.connect(url).get();
				return document;
			}

		} catch (IOException e) {
			System.out.println("Error occured when fetching page " + e);
		}
		return null;
	}

	/**
	 * Search document for search terms
	 * @param document
	 * @param url
	 * @return
	 */
	public static String searchDocument(String document, String url) {
		List<String> searchTerms = Arrays.asList(Application.SEARCH_TERMS);
		final StringBuilder searchResults = new StringBuilder();

		searchTerms.stream().forEach(searchTerm -> {
			Matcher matcher = Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE).matcher(document);
			int count = 0;
			while (matcher.find()) {
				count++;
			}
			if (count > 0) {
				searchResults.append("Search term ").append(searchTerm).append(" occurred ").append(count).append(" times in ").append(url).append("\n");
			}
		});

		return searchResults.toString();

	}
	
	/**
	 * Write search results to a file
	 * @param searchResult
	 * @throws IOException
	 */
	public static void writeSearchResult(String searchResult) throws IOException {
		BufferedWriter output = null;
		final File file = new File(Application.SEARCH_RESULTS_OUTPUT_FILE);
		try {
			output = new BufferedWriter(new FileWriter(file, true));
			if (null != searchResult) {
				output.write(searchResult);
			}
		} catch (IOException e) {
			System.out.println("Error occurred while writing search results to file " + e);
			e.printStackTrace();
		} finally {
			output.close();
		}
	}

}

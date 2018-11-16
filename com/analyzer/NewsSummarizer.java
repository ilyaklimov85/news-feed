package com.analyzer;

import java.util.List;
import java.util.stream.Stream;

public class NewsSummarizer implements Runnable{
	private List<TimestampedNewsItem> newsItemsList; 
	private static int COUNT_OF_HEADLINES_TO_DISPLAY = 3;
	private static final long TEN_SECONDS = 10000; 
	
	public NewsSummarizer(List<TimestampedNewsItem> newsItemsList) {
		this.newsItemsList = newsItemsList;
	}

	@Override
	public void run() {
		synchronized (newsItemsList) {
			long startTime = System.currentTimeMillis() - TEN_SECONDS;
//		    remove all news items that are older than 10 seconds
			newsItemsList.removeIf(item -> item.getTimestamp() < startTime);

			int size = newsItemsList.size();
			System.out.println("Positive news count for the past 10 seconds: " + size);

			if (size > 0) {
				System.out.println("Top headlines:");
				Stream<String> stream = getSortedAndFilteredHeadlinesFromNewsItems();
				stream.forEach(line -> System.out.println(line));
			}
			System.out.println("");
		}

	}
	
	public Stream<String> getSortedAndFilteredHeadlinesFromNewsItems() {
		Stream<String> stream = newsItemsList.stream().map(item -> item.getNewsItem()).sorted((o1, o2) -> {
			if (o1 != null && o2 != null) {
				int o1priority = o1.getPriority();
				int o2priority = o2.getPriority();
				// switch order to have items with highest priorities at the beginning of the list (descending order)
				return Integer.compare(o2priority, o1priority);
			}
			return 0;
		}).limit(COUNT_OF_HEADLINES_TO_DISPLAY).map(item -> item.getHeadline().asString()).distinct();
		return stream;
	}
}

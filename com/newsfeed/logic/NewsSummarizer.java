package com.newsfeed.logic;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class NewsSummarizer implements Runnable{
	
	private List<TimestampedNewsItem> newsItemsList;
	
	public NewsSummarizer( List<TimestampedNewsItem> newsItemsList) {
		this.newsItemsList = newsItemsList;
	}

	@Override
	public void run() {
		synchronized (newsItemsList) {
			System.out.println(newsItemsList.size());
			long endTime = System.currentTimeMillis();
			long startTime = endTime - 10000;

			List<String> topUniqueHeadlines = newsItemsList.stream().filter(item -> item.getTimestamp() < startTime).map(item -> item.getNewsItem())
					.sorted((o1, o2) -> {
						if (o1 != null && o2 != null) {
							int o1priority = o1.getPriority();
							int o2priority = o2.getPriority();
							//switch order to have items with highest priorities at the beginning of the list (descending order)
							return Integer.compare(o2priority, o1priority);
						}
						return 0;
					})
					.limit(3).map(item -> item.getHeadline().asString()).distinct().collect(Collectors.toList());
			
			newsItemsList.removeIf(item -> item.getTimestamp()<startTime);
			int countOfItemsForLast10Secs = newsItemsList.size();
			
			System.out.println("At " + LocalTime.now() + " " + countOfItemsForLast10Secs + " positive news for the past 10 seconds");
			System.out.println("Top headlines:");
			topUniqueHeadlines.forEach(line -> System.out.println(line));
		}
		
	}

}

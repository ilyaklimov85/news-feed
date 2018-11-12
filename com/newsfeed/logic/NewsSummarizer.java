package com.newsfeed.logic;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class NewsSummarizer implements Runnable{
	
	private SortedSet<TimestampedNewsItem> newsItemsSet;
	
	public NewsSummarizer(SortedSet<TimestampedNewsItem> newsItemsList) {
		this.newsItemsSet = newsItemsSet;
	}

	@Override
	public void run() {
		synchronized (newsItemsSet) {
			long endTime = System.currentTimeMillis();
			long startTime = endTime - 10000;
			newsItemsSet.removeIf(item -> item.getTimestamp()<startTime);
			int size = newsItemsSet.size();
			
			
			newsItemsSet.forEach(item -> );
			
			
			
		}
		
	}

}

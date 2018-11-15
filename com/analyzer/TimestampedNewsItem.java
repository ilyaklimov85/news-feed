package com.analyzer;

import com.common.NewsItem;
/*
 * Wrapper class for a newsItem to store timestamp at which NewsAnalizer got the News Item.
 */
public class TimestampedNewsItem{

	private long timestamp;
	private NewsItem newsItem;

	public TimestampedNewsItem(long timestamp, NewsItem newsItem) {
		this.timestamp = timestamp;
		this.newsItem = newsItem;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public NewsItem getNewsItem() {
		return newsItem;
	}
}

package com.analyzer;

import com.common.NewsItem;

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

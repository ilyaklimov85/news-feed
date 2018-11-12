package com.newsfeed.logic;

public class TimestampedNewsItem implements Comparable<TimestampedNewsItem>{

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

	@Override
	public int compareTo(TimestampedNewsItem item) {
		int thispriority = this.getNewsItem().getPriority();
		int otherpriority = item.getNewsItem().getPriority();
		
		long thistimestamp = this.getTimestamp();
		long otherTimestamp = item.getTimestamp();
		
		if (item == null || thispriority < otherpriority)
			return -1;
		if (thispriority > otherpriority)
			return 1;
		if (thispriority == otherpriority) {
			if (thistimestamp < otherTimestamp)
				return -1;
			if (thistimestamp > otherTimestamp)
				return 1;
		}
		return 0;
	}
}

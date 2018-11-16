package com.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.analyzer.NewsSummarizer;
import com.analyzer.TimestampedNewsItem;
import com.common.HeadlineWord;
import com.common.NewsItem;
import com.feed.HeadlineImpl;

class NewsSummarizerTest {

	@Test
	void testGetSortedAndFilteredHeadlinesFromNewsItems() {
		
		List<TimestampedNewsItem> newsItemsList = new ArrayList<>();
		
		List<HeadlineWord> words = new ArrayList<HeadlineWord>(Arrays.asList(HeadlineWord.UP, HeadlineWord.RISE, HeadlineWord.FALL));
		HeadlineImpl headline = new HeadlineImpl(words);
		int priority = 7;
		NewsItem newsItem = new NewsItem(headline, priority);
		newsItemsList.add(new TimestampedNewsItem(1, newsItem));
		
		words = new ArrayList<HeadlineWord>(Arrays.asList(HeadlineWord.SUCCESS, HeadlineWord.RISE, HeadlineWord.GOOD, HeadlineWord.HIGH));
		headline = new HeadlineImpl(words);
		priority = 4;
		newsItem = new NewsItem(headline, priority);
		newsItemsList.add(new TimestampedNewsItem(2, newsItem));
		
		words = new ArrayList<HeadlineWord>(Arrays.asList(HeadlineWord.UP, HeadlineWord.RISE, HeadlineWord.FALL));
		headline = new HeadlineImpl(words);
		priority = 6;
		newsItem = new NewsItem(headline, priority);
		newsItemsList.add(new TimestampedNewsItem(3, newsItem));
		
		words = new ArrayList<HeadlineWord>(Arrays.asList(HeadlineWord.DOWN, HeadlineWord.RISE, HeadlineWord.GOOD));
		headline = new HeadlineImpl(words);
		priority = 9;
		newsItem = new NewsItem(headline, priority);
		newsItemsList.add(new TimestampedNewsItem(2, newsItem));
		
		NewsSummarizer summarizer = new NewsSummarizer(newsItemsList);
		
		
		Stream<String> sortedAndFilteredHeadlinesFromNewsItems = summarizer.getSortedAndFilteredHeadlinesFromNewsItems();
		
		StringBuilder builder = new StringBuilder();
		
		sortedAndFilteredHeadlinesFromNewsItems.forEach(item -> {builder.append(item).append(" ");});
		
		
		assertEquals("down rise good up rise fall ", builder.toString());
	}

}

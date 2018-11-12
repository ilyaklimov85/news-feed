package com.newsfeed.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import com.newsfeed.util.Headline;
import com.newsfeed.util.HeadlineWord;

public class NewsFeedHandler implements Runnable {
	private Socket socket;
	private SortedSet<TimestampedNewsItem> newsItemsSet;

	public NewsFeedHandler(Socket socket, SortedSet<TimestampedNewsItem> newsItemsSet) {
		this.socket = socket;
		this.newsItemsSet = newsItemsSet;
	}

	@Override
	public void run() {
		if (socket != null) {
			try (ObjectInputStream obejectInputStream = new ObjectInputStream(socket.getInputStream());) {
				NewsItem newsItem;
				while ((newsItem = (NewsItem) obejectInputStream.readObject()) != null) {
					if (newsItemsSet != null) {
						
						if (isPositiveNewsItem(newsItem))
							newsItemsSet.add(new TimestampedNewsItem(System.currentTimeMillis(), newsItem));
					}
//					System.out.println(newsItem.getHeadline().asString() + " server");
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			;
		}
	}

	private boolean isPositiveNewsItem(NewsItem newsItem) {
		int positiveCount = 0;
		boolean isPositive = false;
		Headline headline = newsItem.getHeadline();
		if (headline != null) {
			List<HeadlineWord> words = headline.getWords();
			if (!words.isEmpty()) {
				for (HeadlineWord headlineWord : words) {
					if (headlineWord.isPositive())
						positiveCount++;
				}
				isPositive = positiveCount > (int) Math.ceil(words.size() / 2);
			}
		}
		return isPositive;
	}
}

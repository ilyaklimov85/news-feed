package com.newsfeed.logic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

import com.newsfeed.util.Headline;

public class NewsFeedHandler implements Runnable {
	private Socket socket;
	private List<TimestampedNewsItem> newsItemsList;

	public NewsFeedHandler(Socket socket, List<TimestampedNewsItem> newsItemsList) {
		this.socket = socket;
		this.newsItemsList = newsItemsList;
	}

	@Override
	public void run() {
		if (socket != null) {
			try (ObjectInputStream obejectInputStream = new ObjectInputStream(socket.getInputStream());) {
				NewsItem newsItem;
				while ((newsItem = (NewsItem) obejectInputStream.readObject()) != null) {
					if (newsItemsList != null) {
						if (isPositiveNewsItem(newsItem)) {
							newsItemsList.add(new TimestampedNewsItem(System.currentTimeMillis(), newsItem));
							System.out.println(newsItem.getHeadline().asString() + " server");
						}
					}
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
				//more than half of words are positive
				if(positiveCount > 0) isPositive = Math.ceil(words.size() / positiveCount) < 2;
			}
		}
		return isPositive;
	}
}

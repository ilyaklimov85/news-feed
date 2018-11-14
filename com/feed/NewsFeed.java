package com.feed;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.common.HeadlineWord;
import com.common.NewsItem;

public class NewsFeed {

	private HeadlineGenerator headlineGenerator;
	private PriorityGenerator priorityGenerator;
	private static String SERVER = "127.0.0.1";
	private static int PORT = 1500;
	private static Logger logger = Logger.getLogger(NewsFeed.class.getName());

	public NewsFeed(HeadlineGenerator headlineGenerator, PriorityGenerator priorityGenerator) {
		this.headlineGenerator = headlineGenerator;
		this.priorityGenerator = priorityGenerator;
	}

	public NewsItem generateNewsItem() {
		return new NewsItem(headlineGenerator.generateHeadline(), priorityGenerator.generatePriority());
	}

	public static void main(String[] args) {

//		read frequency of emitted news from properties
		Properties appProperties = new Properties();
		long newsFrequensy = 0;
		try {
			appProperties.load(new FileInputStream("app.properties"));
			newsFrequensy = Long.parseLong(appProperties.getProperty("newsFrequensy"));
		} catch (IOException e1) {
			logger.log(Level.INFO, "Problem reading app.properties file");
		}

		// if newsFrequency wasn't read from properties - abort.
		if (newsFrequensy == 0)
			return;

		HeadlineGenerator headlineGenerator = createHeadlineGenerator();
		PriorityGenerator priorityGenerator = createPriorityGenerator();
		NewsFeed newsFeed = new NewsFeed(headlineGenerator, priorityGenerator);

		ScheduledExecutorService newsFeedService = Executors.newSingleThreadScheduledExecutor();
		try (Socket socket = new Socket(SERVER, PORT);
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());) {

			logger.log(Level.INFO, "News Feed Started. To stop it type \"stop\" and hit enter");

			newsFeedService.scheduleAtFixedRate(() -> {
				try {
					NewsItem newsItem = newsFeed.generateNewsItem();
					outputStream.writeObject(newsItem);
				} catch (IOException e) {
					newsFeedService.shutdown();
					logger.log(Level.INFO, "Can't send news items. Type in \"stop\" and hit  enter to stop the News Feed.");
				}
			}, 0, newsFrequensy, TimeUnit.SECONDS);

			// continue sending news items to the server until the user enters "stop" to
			// stop the client
			String input;
			while ((input = stdIn.readLine()) != null) {
				if (input.equalsIgnoreCase("stop")) {
					logger.log(Level.INFO, "News Feed stopped");
					break;
				}
			}
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			newsFeedService.shutdown();
		}
	}

	private static PriorityGenerator createPriorityGenerator() {
		PriorityGenerator priorityGenerator = () -> {
			// TODO find out if my understanding of the priorities distribution is correct
			int[] priorities = { 9, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 2,
					2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

			// get a random index from 0 to priorities.length-1
			int i = new Random().nextInt(priorities.length - 1);
			// get an item with that index
			return priorities[i];

		};
		return priorityGenerator;
	}

	private static HeadlineGenerator createHeadlineGenerator() {
		HeadlineGenerator headlineGenerator = () -> {
			// Generate a random number from 3 to 5
			Random random = new Random();
			int wordCount = 3 + random.nextInt(3);

			// create a list that will store wordCount unique items. Each item is an index
			// of a randomly picked HeadlineWord item
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			while (indexes.size() < wordCount) {
				int nextInt = random.nextInt(HeadlineWord.values().length);
				if (!indexes.contains(nextInt)) {
					indexes.add(nextInt);
				}
			}
			// create list of words that correspond to indexes
			List<HeadlineWord> words = new ArrayList<>();
			indexes.forEach(index -> words.add(HeadlineWord.values()[index]));

			HeadlineImpl headine = new HeadlineImpl(words);

			return headine;
		};
		return headlineGenerator;
	}

}

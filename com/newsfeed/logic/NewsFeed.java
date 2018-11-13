package com.newsfeed.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.newsfeed.util.HeadlineGenerator;
import com.newsfeed.util.PriorityGenerator;

public class NewsFeed {
	
	private static final int RATE = 10;
	
	private HeadlineGenerator headlineGenerator;
	private PriorityGenerator priorityGenerator;
	
	public NewsFeed(HeadlineGenerator headlineGenerator, PriorityGenerator priorityGenerator) {
		this.headlineGenerator = headlineGenerator;
		this.priorityGenerator = priorityGenerator;
	}
	
	public NewsItem generateNewsItem() {
		return new NewsItem(headlineGenerator.generateHeadline(), priorityGenerator.generatePriority());
	}

	
	public static void main(String[] args) {

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
		
		
		PriorityGenerator priorityGenerator = () -> {
			//TODO find out if my understanding of the priorities distribution is correct
			int[] priorities = {9,
			                    8,8,
			                    7,7,7,
			                    6,6,6,6,
			                    5,5,5,5,5,
			                    4,4,4,4,4,4,
			                    3,3,3,3,3,3,3,
			                    2,2,2,2,2,2,2,2,
			                    1,1,1,1,1,1,1,1,1,
			                    0,0,0,0,0,0,0,0,0,0};
			
				//get a random index from 0 to priorities.length-1
				int i = new Random().nextInt(priorities.length-1);
				//get an item with that index
				return priorities[i];

		};
		
		NewsFeed newsFeed = new NewsFeed(headlineGenerator, priorityGenerator);
		
//		TODO Think about restoring connection of server restarts
		try (Socket socket = new Socket("127.0.0.1", 1500);
//				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				InputStream consoleInput = System.in;
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(consoleInput));
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				

		) {
			
			System.out.println("News Feed Started. To stop it type \"stop\" and hit enter");
			
			ScheduledExecutorService newsFeedService = Executors.newSingleThreadScheduledExecutor();
			newsFeedService.scheduleAtFixedRate(() -> {
				try {
					NewsItem newsItem = newsFeed.generateNewsItem();
					outputStream.writeObject(newsItem);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					newsFeedService.shutdown();
				} 
			}, 0, RATE, TimeUnit.SECONDS);

			// continue sending news items to the server until the user enters "stop" to
			// stop the client
			String input;
			while ((input = stdIn.readLine()) != null) {
				if (input.equalsIgnoreCase("stop")) {
					newsFeedService.shutdown();
					System.out.println("News Feed stopped");
					break;
				}
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

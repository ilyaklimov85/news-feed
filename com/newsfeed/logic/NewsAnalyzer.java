package com.newsfeed.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NewsAnalyzer {
	private static final int RATE = 10;
	private static final int PORT = 1500;
	private static final long TEN_SECONDS = 10000; 

	public static void main(String[] args) {

		ExecutorService handlersService = Executors.newCachedThreadPool();
		ScheduledExecutorService summarizerService = Executors.newSingleThreadScheduledExecutor();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				ServerSocket serverSocket = new ServerSocket(PORT);

		) {
			System.out.println("News Analyzer Started. To stop it type \"stop\" and hit enter");
			
//			Start a thread that will be able to read "stop" from stdin and will stop the server.
			new Thread(() -> {
				String stdin;
				try {
					while ((stdin = br.readLine()) != null) {
						if (stdin.equalsIgnoreCase("stop")) {
							serverSocket.close();
							System.out.println("News Analyzer Stopped");
							break;
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					handlersService.shutdown();
					summarizerService.shutdown();
				}
			}).start();

			
			
//			Start a scheduled thread that will summarize news items that came over that past 10 seconds
			List<TimestampedNewsItem> incomingNewsItems = Collections.synchronizedList(new ArrayList<TimestampedNewsItem>());
			
			summarizerService.scheduleAtFixedRate(() -> {
				synchronized (incomingNewsItems) {

					long startTime = System.currentTimeMillis() - TEN_SECONDS;
					
					incomingNewsItems.removeIf(item -> item.getTimestamp()<startTime);
					
					System.out.println("Positive news for the last 10 seconds: " + incomingNewsItems.size());

					incomingNewsItems.stream().map(item -> item.getNewsItem()).sorted((o1, o2) -> {
								if (o1 != null && o2 != null) {
									int o1priority = o1.getPriority();
									int o2priority = o2.getPriority();
									//switch order to have items with highest priorities at the beginning of the list (descending order)
									return Integer.compare(o2priority, o1priority);
								}
								return 0;
							}).limit(3).map(item -> item.getHeadline().asString()).distinct().forEach(line -> System.out.println("Top headlines:" + line));
				}
			}, RATE, RATE, TimeUnit.SECONDS);

			
			
//			Upon receiving a connection from News Feeder, run a handler for it
			while (!summarizerService.isShutdown()) {
				Socket socket = serverSocket.accept();
				
				handlersService.execute(() -> {
					try (ObjectInputStream obejectInputStream = new ObjectInputStream(socket.getInputStream());) {
						NewsItem newsItem;
						while ((newsItem = (NewsItem) obejectInputStream.readObject()) != null) {
							if (newsItem.isPositive()) {
								incomingNewsItems.add(new TimestampedNewsItem(System.currentTimeMillis(), newsItem));
							}
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally {
			handlersService.shutdown();
			summarizerService.shutdown();
		}


	}

}

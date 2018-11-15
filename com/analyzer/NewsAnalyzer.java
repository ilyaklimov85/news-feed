package com.analyzer;

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

import com.common.NewsItem;

public class NewsAnalyzer {
	
	private static final int RATE = 10;
	private static final int PORT = 1500;
	private static final long TEN_SECONDS = 10000; 

	
	public static void main(String[] args) {

		ExecutorService handlersService = Executors.newCachedThreadPool();
		ScheduledExecutorService summarizerService = Executors.newSingleThreadScheduledExecutor();
		
		List<Socket> clientSockets = new ArrayList<>();
	
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				ServerSocket serverSocket = new ServerSocket(PORT);

		) {
			System.out.println("News Analyzer Started. To stop it type \"stop\" and hit enter");
			
			
//			Start a thread that will be able to read "stop" from stdin and will stop the server.
			Thread userInputHandlerThread = new Thread(() -> {
				String stdin;
				try {
					while ((stdin = br.readLine()) != null) {
						if (stdin.equalsIgnoreCase("stop")) {
//							close the server socket so that the accept() method throws SocketException and the main thread is unblocked. Client sockets will be closed in the finally block
							serverSocket.close();
							System.out.println("News Analyzer Stopped");
							break;
						}else {
							System.out.println("Unknown command \"" + stdin + "\". If you want to stop the News Analyzer. Type in \"stop\" and hit enter");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			userInputHandlerThread.start();

			
			
//			Start a scheduled thread that will summarize news items that came over that past 10 seconds
			List<TimestampedNewsItem> incomingNewsItems = Collections.synchronizedList(new ArrayList<TimestampedNewsItem>());
			
			summarizerService.scheduleAtFixedRate(() -> {
				synchronized (incomingNewsItems) {

					long startTime = System.currentTimeMillis() - TEN_SECONDS;
//					remove all news items that are older than 10 seconds
					incomingNewsItems.removeIf(item -> item.getTimestamp()<startTime);
										
					int size = incomingNewsItems.size();
					System.out.println("Positive news count for the past 10 seconds: " + size);
					
					if (size > 0) {
						System.out.println("Top headlines:");
						incomingNewsItems.stream().map(item -> item.getNewsItem()).sorted((o1, o2) -> {
							if (o1 != null && o2 != null) {
								int o1priority = o1.getPriority();
								int o2priority = o2.getPriority();
								// switch order to have items with highest priorities at the beginning of the list (descending order)
								return Integer.compare(o2priority, o1priority);
							}
							return 0;
						}).limit(3).map(item -> item.getHeadline().asString()).distinct()
								.forEach(line -> System.out.println(line));
					}
					System.out.println(" ");
				}
			}, RATE, RATE, TimeUnit.SECONDS);

			
//			Upon receiving a connection from News Feeder, run a handler for it
			while (true) {
				Socket socket = serverSocket.accept();
				clientSockets.add(socket);
			
				handlersService.execute(() -> {
					try (ObjectInputStream obejectInputStream = new ObjectInputStream(socket.getInputStream());) {
						NewsItem newsItem;
						while ((newsItem = (NewsItem) obejectInputStream.readObject()) != null) {
							if (newsItem.isPositive()) {
								synchronized (incomingNewsItems) {
									incomingNewsItems.add(new TimestampedNewsItem(System.currentTimeMillis(), newsItem));	
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();

					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				});
				
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			handlersService.shutdown();
			summarizerService.shutdown();
//			close all clients sockets
			clientSockets.forEach((socket) -> {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

	}

}

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
	
	private static final int SUMMARIZER_RATE = 10;
	private static final int SUMMARIZER_DELAY = 10;

	public static void main(String[] args) {
		
		ExecutorService handlersService = Executors.newCachedThreadPool();
		ScheduledExecutorService summarizerService = Executors.newSingleThreadScheduledExecutor();
		
		List<Socket> clientSockets = new ArrayList<>();
//		read port from properties
		String portString = System.getProperty("port");
		
		if (portString == null) {
			System.out.println("Please sepcify port as a VM property");
			return;
		}

		int port = Integer.parseInt(portString);
	
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				ServerSocket serverSocket = new ServerSocket(port);

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
			
			summarizerService.scheduleAtFixedRate(new NewsSummarizer(incomingNewsItems), SUMMARIZER_DELAY, SUMMARIZER_RATE, TimeUnit.SECONDS);

			
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

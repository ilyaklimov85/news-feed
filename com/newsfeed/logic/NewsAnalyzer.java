package com.newsfeed.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NewsAnalyzer {
	private static ExecutorService handlers = Executors.newCachedThreadPool();
	private static ScheduledExecutorService summarizer = Executors.newSingleThreadScheduledExecutor();
	private static final int RATE = 10;

	public static void main(String[] args) {

		try (ServerSocket serverSocket = new ServerSocket(1500);
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//				Socket clientSocket = serverSocket.accept();
//				BufferedReader bfr = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		) {
			
			SortedSet<TimestampedNewsItem> set = Collections.synchronizedSortedSet(new TreeSet<TimestampedNewsItem>());

			summarizer.scheduleAtFixedRate(new NewsSummarizer(set), 0, RATE, TimeUnit.SECONDS);

			while (true) {
				Socket socket = serverSocket.accept();
				handlers.execute(new NewsFeedHandler(socket, set));
			}

			// continue handling incoming news items from clients until the user terminates
			// by typing "exit"
//			String stdin;
//			while((stdin = br.readLine())!=null) {
//				if(stdin.equalsIgnoreCase("exit")) break;
//			}
//
//			String line;
//			while ((line = bfr.readLine()) != null) {
//				System.out.println(line + " server");
//			}
//
//			System.out.println("server stop");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			handlers.shutdown();
			summarizer.shutdown();
		}

	}

}

package com.newsfeed.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.newsfeed.util.HeadlineGenerator;
import com.newsfeed.util.PriorityGenerator;

public class NewsFeed {
	
	static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	private static final int RATE = 10;
	
	public static void main(String[] args) {
		
		try(Socket socket = new Socket("127.0.0.1", 1500);
//				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				InputStream consoleInput = System.in;
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(consoleInput));
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				
				){
			
			//can be easily changed to accept generator classes through xml configuration, or parameters
			HeadlineGenerator headlineGenerator = new HeadlineGeneratorImpl();
			PriorityGenerator priorityGenerator = new PriorityGeneratorImpl();
			
			Runnable task = new Runnable() {
				
				@Override
				public void run() {
					NewsItem newsItem = new NewsItem(headlineGenerator, priorityGenerator);
					try {
						outputStream.writeObject(newsItem);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					pw.println(newsItem.getPriority() + " " + newsItem.getHeadline() + " " + newsItem.isPositive());
				}
			};
			
			service.scheduleAtFixedRate(task, 0, RATE, TimeUnit.SECONDS);
			
			//continue sending news items to the server until the user enters "exit" to stop the client
			String input;
			while((input= stdIn.readLine()) != null) {
				if(input.equals("exit")) break;
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			service.shutdown();
			System.out.println("client exit");
		}
	}

}

package com.newsfeed.logic;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.newsfeed.util.Headline;
import com.newsfeed.util.HeadlineGenerator;
import com.newsfeed.util.PriorityGenerator;

public class NewsItem implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4394771038495675245L;
	private Headline headline;
	private int priority;
	
	public NewsItem(HeadlineGenerator headlineGenerator, PriorityGenerator priorityGenerator) {
		//TODO Think about fallback values
		headline = headlineGenerator != null? headlineGenerator.generateHeadline(): null;
		priority = priorityGenerator != null? priorityGenerator.generatePriority(): Integer.MAX_VALUE;
	}
	

	public Headline getHeadline() {
		return headline;
	}


	public int getPriority() {
		return priority;
	}

}

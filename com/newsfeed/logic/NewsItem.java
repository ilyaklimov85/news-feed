package com.newsfeed.logic;
import java.io.Serializable;

import com.newsfeed.util.Headline;

public class NewsItem implements Serializable{
	
	private static final long serialVersionUID = -4394771038495675245L;
	private Headline headline;
	private int priority;
	
	public NewsItem(Headline headline, int priority) {
		this.headline = headline;
		this.priority = priority;
	}
	

	public Headline getHeadline() {
		return headline;
	}


	public int getPriority() {
		return priority;
	}

}

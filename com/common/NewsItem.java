package com.common;
import java.io.Serializable;


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
	
	public boolean isPositive() {
		boolean isPositive = false;
	
		if (headline == null)
			return false;

		long positiveWordsCount = headline.getWords().stream().filter((word) -> word.isPositive()).count();
		
		if (positiveWordsCount > 0)
			isPositive = Math.ceil(headline.getWords().size() / positiveWordsCount) < 2;
		
		return isPositive;
	}

}

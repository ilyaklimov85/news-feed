package com.newsfeed.util;

public enum HeadlineWord{
	UP("up", true), 
	DOWN("down", false), 
	RISE("rise", true), 
	FALL("fall", false), 
	GOOD("good", true), 
	BAD("bad", false), 
	SUCCESS("success", true), 
	FAILURE("failure", false), 
	HIGH("high", true), 
	LOW("low", false), 
	UBER("uber", true), 
	UNTER("unter", false);
	
	private boolean positive;
	private String name;
	
	private HeadlineWord (String name, boolean positive){
		this.positive = positive;
		this.name = name;
	}
	
	public boolean isPositive() {
		return positive;
	}
	
	public String getName() {
		return name;
	}
}

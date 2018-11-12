package com.newsfeed.util;

import java.util.List;

import com.newsfeed.logic.HeadlineWord;

public interface Headline {
	
	List<HeadlineWord> getWords();
	
	String asString();

}

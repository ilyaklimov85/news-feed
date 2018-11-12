package com.newsfeed.util;

import java.util.List;

public interface Headline {
	
	List<HeadlineWord> getWords();
	
	String asString();

}

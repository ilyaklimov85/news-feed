package com.common;

import java.util.List;

public interface Headline {
	
	List<HeadlineWord> getWords();
	
	String asString();
	
	boolean isPositive();

}

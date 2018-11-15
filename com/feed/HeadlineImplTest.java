package com.feed;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.common.HeadlineWord;

class HeadlineImplTest {

	@Test
	void testIsPositive() {
		HeadlineImpl headline;
		
		headline = new HeadlineImpl(new ArrayList<HeadlineWord>(Arrays.asList(HeadlineWord.UP, HeadlineWord.RISE, HeadlineWord.FALL)));
		assertTrue(headline.isPositive());
		
		headline = new HeadlineImpl(new ArrayList<HeadlineWord>(Arrays.asList(HeadlineWord.UP, HeadlineWord.DOWN, HeadlineWord.FALL)));
		assertFalse(headline.isPositive());
		
		headline = new HeadlineImpl(new ArrayList<HeadlineWord>(Arrays.asList(HeadlineWord.UP, HeadlineWord.DOWN, HeadlineWord.FALL, HeadlineWord.SUCCESS)));
		assertFalse(headline.isPositive());
		
		headline = new HeadlineImpl(new ArrayList<HeadlineWord>(Arrays.asList(HeadlineWord.UP, HeadlineWord.HIGH, HeadlineWord.FALL, HeadlineWord.SUCCESS)));
		assertTrue(headline.isPositive());
		
		headline = new HeadlineImpl(new ArrayList<HeadlineWord>(Arrays.asList(HeadlineWord.UP, HeadlineWord.HIGH, HeadlineWord.FALL, HeadlineWord.SUCCESS, HeadlineWord.BAD)));
		assertTrue(headline.isPositive());
		
		headline = new HeadlineImpl(new ArrayList<HeadlineWord>(Arrays.asList(HeadlineWord.UP, HeadlineWord.LOW, HeadlineWord.FALL, HeadlineWord.SUCCESS, HeadlineWord.BAD)));
		assertFalse(headline.isPositive());
				
	}

}

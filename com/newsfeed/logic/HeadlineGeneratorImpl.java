package com.newsfeed.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.newsfeed.util.Headline;
import com.newsfeed.util.HeadlineGenerator;
import com.newsfeed.util.HeadlineWord;

public class HeadlineGeneratorImpl implements HeadlineGenerator {
	private static int minWords = 3;

	@Override
	public Headline generateHeadline() {
		// Generate a random number from 3 to 5
		Random random = new Random();
		int wordCount = minWords + random.nextInt(3);

		// create a list that will store wordCount unique items. Each item is an index
		// of a randomly picked HeadlineWord item
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		while (indexes.size() < wordCount) {
			int nextInt = random.nextInt(HeadlineWord.values().length);
			if (!indexes.contains(nextInt)) {
				indexes.add(nextInt);
			}
		}
		// create list of words that correspond to indexes
		List<HeadlineWord> words = new ArrayList<>();
		indexes.forEach(index -> words.add(HeadlineWord.values()[index]));

		HeadlineImpl headine = new HeadlineImpl(words);

		return headine;
	}

}

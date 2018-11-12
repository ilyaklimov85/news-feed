package com.newsfeed.logic;

import java.util.Random;

import com.newsfeed.util.PriorityGenerator;

public class PriorityGeneratorImpl implements PriorityGenerator{
	
	//TODO find out if my understanding of the priorities distribution is correct
	private static int[] priorities = {0,
	                                   1,1,
	                                   2,2,2,
	                                   3,3,3,3,
	                                   4,4,4,4,4,
	                                   5,5,5,5,5,5,
	                                   6,6,6,6,6,6,6,
	                                   7,7,7,7,7,7,7,7,
	                                   8,8,8,8,8,8,8,8,8,
	                                   9,9,9,9,9,9,9,9,9,9};
	
	private Random randomNumberGenerator = new Random();

	@Override
	public int generatePriority() {
		//get a random index from 0 to priorities.length-1
		int i = randomNumberGenerator.nextInt(priorities.length-1);
		//get an item with that index
		return priorities[i];
	}

}

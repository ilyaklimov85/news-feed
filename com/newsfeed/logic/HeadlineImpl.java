package com.newsfeed.logic;

import java.io.Serializable;
import java.util.List;

import com.newsfeed.util.Headline;
import com.newsfeed.util.HeadlineWord;

public class HeadlineImpl implements Headline, Serializable {

	private static final long serialVersionUID = -8814881748384220812L;
	private List<HeadlineWord> words;
	private String stringRepresentation = null;

	HeadlineImpl(List<HeadlineWord> words) {
		this.words = words;
	}


	public String asString() {
		if (stringRepresentation == null) {
			StringBuilder sb = new StringBuilder("");
			if (!words.isEmpty()) {
				for (HeadlineWord headlineWord : words) {
					sb.append(headlineWord.getName()).append(" ");
				}
			}
			stringRepresentation = sb.toString().trim();
		}
		return stringRepresentation;
	}


	@Override
	public List<HeadlineWord> getWords() {
		return words;
	}
}

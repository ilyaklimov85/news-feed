package com.feed;

import java.io.Serializable;
import java.util.List;

import com.common.Headline;
import com.common.HeadlineWord;

public class HeadlineImpl implements Headline, Serializable {

	private static final long serialVersionUID = -8814881748384220812L;
	private List<HeadlineWord> words;

	HeadlineImpl(List<HeadlineWord> words) {
		this.words = words;
	}


	public String asString() {

		StringBuilder sb = new StringBuilder("");

		words.stream().forEach((word) -> {
			sb.append(word.getName()).append(" ");
		});
		return sb.toString().trim();

	}


	@Override
	public List<HeadlineWord> getWords() {
		return words;
	}
}

package com.tcd.cranfield.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.CharArraySet;

import com.tcd.cranfield.CranfieldApp;

/**
 * This class loads stopwords from a given file
 * It performs a whitespace split to get the words into a CharArraySet
 * @author ranglana
 *
 */
public class AnalyzerUtil {

	public static CharArraySet getStopwords(String fileName) throws IOException, URISyntaxException {
		String text = new String(Files.readAllBytes(Paths.get(fileName)));
		final List<String> stopWords = Arrays.asList(text.split(" "));
		final CharArraySet stopSet = new CharArraySet(stopWords, false);
		return CharArraySet.unmodifiableSet(stopSet);
	}

}

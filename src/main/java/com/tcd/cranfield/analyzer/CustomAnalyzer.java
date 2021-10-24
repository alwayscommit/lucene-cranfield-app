package com.tcd.cranfield.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * CustomAnalyzer is similar to EnglishAnalyzer
 * LengthFilter has been added
 * @author ranglana
 *
 */
public class CustomAnalyzer extends Analyzer {

	private CharArraySet stopwordSet;

	public CustomAnalyzer(CharArraySet stopwordSet) {
		this.stopwordSet = stopwordSet;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		StandardTokenizer src = new StandardTokenizer();
		TokenStream result = new EnglishPossessiveFilter(src);
		result = new StopFilter(result, stopwordSet);
		result = new PorterStemFilter(result);
		result = new LengthFilter(result, 2, 18);
		result = new LowerCaseFilter(result);
		return new TokenStreamComponents(src, result);
	}
}

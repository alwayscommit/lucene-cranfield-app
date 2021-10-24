package com.tcd.cranfield.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.ListSelectionEvent;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.queryparser.xml.builders.TermQueryBuilder;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;

import com.tcd.cranfield.model.CranfieldQuery;

/**
 * This class is responsible for setting up the search configuration as well as perform the search
 * It uses MultiFieldQueryParser to query on multiple fields
 * @author ranglana
 *
 */
public class LuceneSearcher {

	private IndexSearcher indexSearcher;

	public LuceneSearcher(Path indexPath, Similarity similarity) throws IOException {
		System.out.println("Searching documents...");
		IndexReader indexReader = DirectoryReader.open(FSDirectory.open(indexPath));
		indexSearcher = new IndexSearcher(indexReader);
		indexSearcher.setSimilarity(similarity);
	}

	public ScoreDoc[] search(CranfieldQuery cranfieldQuery, Analyzer analyzer) throws IOException, ParseException {
		HashMap<String, Float> boosts = new HashMap<String, Float>();
		boosts.put("title", 2f);
		boosts.put("body", 4.7f);
		
		MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[] { "title", "author", "body" }, analyzer, boosts);
		//default
		parser.setDefaultOperator(Operator.OR);
		Query query = parser.parse(QueryParser.escape(cranfieldQuery.getQuery()));
		
		ScoreDoc[] scoreDocs = indexSearcher.search(query, 1400).scoreDocs;
		return scoreDocs;
	}

	public Document getDocument(Integer docId, ScoreDoc[] scoredDocs) throws IOException {
		return indexSearcher.doc(scoredDocs[docId].doc);
	}
}

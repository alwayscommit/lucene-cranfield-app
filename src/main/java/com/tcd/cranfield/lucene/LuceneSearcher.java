package com.tcd.cranfield.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;

import com.tcd.cranfield.model.CranfieldQuery;

public class LuceneSearcher {
	
	private IndexSearcher indexSearcher;
	
	public LuceneSearcher(Path indexPath) throws IOException {
		IndexReader indexReader = DirectoryReader.open(FSDirectory.open(indexPath));
		indexSearcher = new IndexSearcher(indexReader);
	}

	public ScoreDoc[] search(CranfieldQuery cranfieldQuery, Analyzer analyzer) throws IOException, ParseException {
		MultiFieldQueryParser parser = new MultiFieldQueryParser(
				new String[] { "docId", "title", "author", "bibliography", "body" }, analyzer);
		Query query = parser.parse(QueryParser.escape(cranfieldQuery.getQuery()));
		ScoreDoc[] scoreDocs = indexSearcher.search(query, 1400).scoreDocs;
		return scoreDocs;
	}
	
	public Document getDocument(Integer docId, ScoreDoc[] scoredDocs) throws IOException {
		return indexSearcher.doc(scoredDocs[docId].doc);
	}
}

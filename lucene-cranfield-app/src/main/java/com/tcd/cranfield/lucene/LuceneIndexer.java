package com.tcd.cranfield.lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneIndexer {

	public Analyzer indexOnVectorSpace(List<Document> cranfieldDocList) {
		try {
			Analyzer analyzer = new WhitespaceAnalyzer();
			Directory directory = FSDirectory.open(Paths.get("indexed-docs/vector-space"));
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			config.setSimilarity(new ClassicSimilarity());
			IndexWriter iwriter = new IndexWriter(directory, config);
			iwriter.addDocuments(cranfieldDocList);
			iwriter.close();
			directory.close();
			return analyzer;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}

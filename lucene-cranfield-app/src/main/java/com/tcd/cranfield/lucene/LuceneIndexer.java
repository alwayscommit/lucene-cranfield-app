package com.tcd.cranfield.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneIndexer {

	public Path index(List<Document> cranfieldDocList, IndexWriterConfig config) {
		try {
			Path indexPath = Paths.get("indexed-docs/"+config.getAnalyzer().getClass().getSimpleName()+"_"+config.getSimilarity());
			Directory directory = FSDirectory.open(indexPath);
			IndexWriter iwriter = new IndexWriter(directory, config);
			iwriter.addDocuments(cranfieldDocList);
			iwriter.close();
			directory.close();
			return indexPath;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}

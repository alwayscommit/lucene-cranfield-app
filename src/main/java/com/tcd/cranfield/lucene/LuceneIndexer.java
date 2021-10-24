package com.tcd.cranfield.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * This class is responsible for indexing and writing it to the respective files
 * @author ranglana
 *
 */
public class LuceneIndexer {
	
	public static final String INDEXED_DOC_LOCATION = "indexed-docs/";

	public Path index(List<Document> cranfieldDocList, IndexWriterConfig config) {
		try {
			System.out.println("Indexing data using " + config.getAnalyzer().getClass().getSimpleName() + " and " + config.getSimilarity());
			Path indexPath = Paths.get(INDEXED_DOC_LOCATION+config.getAnalyzer().getClass().getSimpleName()+"_"+config.getSimilarity());
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

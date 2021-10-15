package com.tcd.cranfield;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

import com.tcd.cranfield.lucene.LuceneIndexer;
import com.tcd.cranfield.lucene.LuceneSearcher;
import com.tcd.cranfield.model.CranfieldQuery;
import com.tcd.cranfield.util.CranfieldDocumentParser;
import com.tcd.cranfield.util.CranfieldQueryParser;

public class CranfieldApp {
	
	private static File cranfieldDataFile;
	private static File cranfieldQueryFile;
	private static String outputDirectory = "target/";

	public static void main(String[] args) {
		if(args.length<2) {
			System.out.println("Following Arguments are mandatory\n1. Cranfield Data \n2. Cranfield Query");
			return;
		}
		cranfieldDataFile = new File(args[0]);
		cranfieldQueryFile = new File(args[1]);
		
		//run for different analyzers and similarities
		try {
			run(getIndexWriterConfig(new WhitespaceAnalyzer(), new ClassicSimilarity()));
//			run(getIndexWriterConfig(new WhitespaceAnalyzer(), new BM25Similarity()));
//			run(getIndexWriterConfig(new WhitespaceAnalyzer(), new LMDirichletSimilarity()));
//			run(getIndexWriterConfig(new WhitespaceAnalyzer(), new TfIdfSimilarity()));
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void run(IndexWriterConfig config) throws ParseException, IOException {
		// parse documents
		CranfieldDocumentParser documentParser = new CranfieldDocumentParser();
		List<Document> cranfieldDocList = documentParser.parseCranfieldData(cranfieldDataFile);

		// index documents
		LuceneIndexer luceneIndexer = new LuceneIndexer();
		Path indexDataPath = luceneIndexer.index(cranfieldDocList, config);

		Analyzer analyzer = config.getAnalyzer();
		
		// parse queries
		CranfieldQueryParser queryParser = new CranfieldQueryParser();
		List<CranfieldQuery> cranfieldQueryList = queryParser.parseQuery(cranfieldQueryFile);

		//search
		LuceneSearcher luceneSearcher = new LuceneSearcher(indexDataPath);
		List<ScoreDoc> totalScoreDocList = new ArrayList<ScoreDoc>();
		Path path = Paths.get(outputDirectory+analyzer.getClass().getSimpleName() + "_" + config.getSimilarity().getClass().getSimpleName() + "_output.txt");
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			
			// loop over 225 search queries
			for (CranfieldQuery query : cranfieldQueryList) {
				
				// for each query, we get 1400 results
				ScoreDoc[] scoredDocs = luceneSearcher.search(query, analyzer);
				totalScoreDocList.addAll(Arrays.asList(scoredDocs));
				
				// output search results to a file
				for (int docIndex = 0; docIndex < scoredDocs.length; docIndex++) {
					Document document = luceneSearcher.getDocument(docIndex, scoredDocs);
					// output file format - query id, 0, docid, 0, score, yuyutu
					writer.append(query.getQueryId() + " 0 " + document.get("docId") + " " + docIndex + " " + scoredDocs[docIndex].score + " YUYUTU");
					writer.newLine();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	private static IndexWriterConfig getIndexWriterConfig(Analyzer analyzer, Similarity similarity) {
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		config.setSimilarity(similarity);
		return config;
	}

}

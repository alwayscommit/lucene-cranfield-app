package com.tcd.cranfield;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.DoubleSupplier;
import java.util.stream.DoubleStream;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.DFISimilarity;
import org.apache.lucene.search.similarities.DFRSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.MultiSimilarity;
import org.apache.lucene.search.similarities.Similarity;

import com.tcd.cranfield.analyzer.CustomAnalyzer;
import com.tcd.cranfield.lucene.LuceneIndexer;
import com.tcd.cranfield.lucene.LuceneSearcher;
import com.tcd.cranfield.model.CranfieldQuery;
import com.tcd.cranfield.util.AnalyzerUtil;
import com.tcd.cranfield.util.CranfieldDocumentParser;
import com.tcd.cranfield.util.CranfieldQueryParser;

/**
 * CranfieldApp is an implementation that uses the Cranfield Paradigm to test the performance of Lucene Search engine with different configurations
 * @author ranglana
 */
public class CranfieldApp {

	private static File cranfieldDataFile;
	private static File cranfieldQueryFile;
	private static List<Document> cranfieldDocList;
	private static List<CranfieldQuery> cranfieldQueryList;
	private static String outputFileDirectory;
	private static final String STOPWORDS_FILE = "/home/ranglana/lucene-cranfield-app/src/main/resources/stopwords.txt";

	public static void main(String[] args) throws IOException {
		if (invalidArguments(args)) {
			System.out.println("Following Arguments are mandatory\n1. Cranfield Data \n2. Cranfield Queries");
			return;
		}

		readArguments(args);

		// parse the documents
		CranfieldDocumentParser documentParser = new CranfieldDocumentParser();
		cranfieldDocList = documentParser.parseCranfieldData(cranfieldDataFile);

		// parse queries
		CranfieldQueryParser queryParser = new CranfieldQueryParser();
		cranfieldQueryList = queryParser.parseQuery(cranfieldQueryFile);

		// to clear old results and create a fresh output directory
		outputFileDirectory = createOutputDirectory();

		// run for different analyzers and similarities
		try {
			run(getIndexWriterConfig(new WhitespaceAnalyzer(), new ClassicSimilarity()), "WhitespaceAnalyzer_ClassicSimilarity");
			run(getIndexWriterConfig(new WhitespaceAnalyzer(), new BM25Similarity()), "WhitespaceAnalyzer_BM25Similarity");
			run(getIndexWriterConfig(new WhitespaceAnalyzer(), new BM25Similarity(2f, 0.8f)), "WhitespaceAnalyzer_BM25Similarity_Parameter");
			
			run(getIndexWriterConfig(new SimpleAnalyzer(), new ClassicSimilarity()), "SimpleAnalyzer_ClassicSimilarity");
			run(getIndexWriterConfig(new SimpleAnalyzer(), new BM25Similarity()), "SimpleAnalyzer_BM25Similarity");
			run(getIndexWriterConfig(new SimpleAnalyzer(), new BM25Similarity(2f, 0.8f)), "SimpleAnalyzer_BM25Similarity_Parameter");
			
			run(getIndexWriterConfig(new StandardAnalyzer(), new ClassicSimilarity()), "StandardAnalyzer_ClassicSimilarity");
			run(getIndexWriterConfig(new StandardAnalyzer(), new BM25Similarity()), "StandardAnalyzer_BM25Similarity");
			run(getIndexWriterConfig(new StandardAnalyzer(), new BM25Similarity(2f, 0.8f)), "StandardAnalyzer_BM25Similarity_Parameter");
			
			run(getIndexWriterConfig(new EnglishAnalyzer(), new ClassicSimilarity()), "EnglishAnalyzer_ClassicSimilarity");
			run(getIndexWriterConfig(new EnglishAnalyzer(), new BM25Similarity()), "EnglishAnalyzer_BM25Similarity");
			run(getIndexWriterConfig(new EnglishAnalyzer(), new BM25Similarity(2f, 0.8f)), "EnglishAnalyzer_BM25Similarity_Parameter");
			
			// english stop words picked from https://www.ranks.nl/stopwords, adding more stopwords decreases the mAP score
			CharArraySet stopwordSet = AnalyzerUtil.getStopwords(STOPWORDS_FILE);
			run(getIndexWriterConfig(new EnglishAnalyzer(stopwordSet), new ClassicSimilarity()), "EnglishAnalyzer_Stopwords_ClassicSimilarity");
			run(getIndexWriterConfig(new EnglishAnalyzer(stopwordSet), new BM25Similarity()), "EnglishAnalyzer_Stopwords_BM25Similarity");
			run(getIndexWriterConfig(new EnglishAnalyzer(stopwordSet), new BM25Similarity(2f, 0.8f)), "EnglishAnalyzer_Stopwords_BM25Similarity_Parameter");
			
			//Tried different similarities but none of them scored as good as BM25Similarity
			run(getIndexWriterConfig(new EnglishAnalyzer(stopwordSet), new LMJelinekMercerSimilarity(0.1f)), "english-stop-j0");
			run(getIndexWriterConfig(new EnglishAnalyzer(stopwordSet), new LMDirichletSimilarity()), "english-stop-dirichlet");
			run(getIndexWriterConfig(new EnglishAnalyzer(stopwordSet), new BooleanSimilarity()), "english-stop-boolean");

			// custom analyzer
			run(getIndexWriterConfig(new CustomAnalyzer(stopwordSet), new BM25Similarity(2f, 0.8f)), "CustomAnalyzer_BM25Similarity_Parameter");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Output results have been saved in " + outputFileDirectory);
	}

	private static boolean invalidArguments(String[] args) {
		if (args.length < 2) {
			return true;
		} else {
			return false;
		}
	}

	private static void readArguments(String[] args) {
		cranfieldDataFile = new File(args[0]);
		System.out.println("Cranfield Data : " + cranfieldDataFile);
		cranfieldQueryFile = new File(args[1]);
		System.out.println("Cranfield Queries : " + cranfieldQueryFile);
	}

	private static String createOutputDirectory() throws IOException {
		File folder = new File("output");
		if (folder.exists()) {
			FileUtils.deleteDirectory(folder);
		}
		folder.mkdir();
		return folder.getAbsolutePath();
	}

	public static void run(IndexWriterConfig config, String outputFileName) throws ParseException, IOException {
		// index documents
		LuceneIndexer luceneIndexer = new LuceneIndexer();
		Path indexDataPath = luceneIndexer.index(cranfieldDocList, config);

		// search
		LuceneSearcher luceneSearcher = new LuceneSearcher(indexDataPath, config.getSimilarity());
		List<ScoreDoc> totalScoreDocList = new ArrayList<ScoreDoc>();

		try (BufferedWriter writer = Files.newBufferedWriter(getOutputPath(outputFileName), StandardCharsets.UTF_8)) {

			// loop over 225 search queries
			for (CranfieldQuery query : cranfieldQueryList) {

				// for each query, we get 1400 results
				ScoreDoc[] scoredDocs = luceneSearcher.search(query, config.getAnalyzer());
				totalScoreDocList.addAll(Arrays.asList(scoredDocs));

				// output search results to a file
				for (int docIndex = 0; docIndex < scoredDocs.length; docIndex++) {
					Document document = luceneSearcher.getDocument(docIndex, scoredDocs);
					// output file format - query id, 0, docid, 0, score, yuyutu
					writer.append(query.getQueryId() + " 0 " + document.get("docId") + " " + docIndex + " " + scoredDocs[docIndex].score + " " + outputFileName);
					writer.newLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Path getOutputPath(String outputFileName) {
		return Paths.get("output\\" + outputFileName + ".txt");
	}

	private static IndexWriterConfig getIndexWriterConfig(Analyzer analyzer, Similarity similarity) {
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		config.setSimilarity(similarity);
		return config;
	}

}

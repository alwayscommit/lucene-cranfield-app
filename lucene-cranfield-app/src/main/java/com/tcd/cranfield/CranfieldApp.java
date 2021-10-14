package com.tcd.cranfield;

import java.io.File;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;

import com.tcd.cranfield.lucene.LuceneIndexer;
import com.tcd.cranfield.model.CranfieldQuery;
import com.tcd.cranfield.util.CranfieldDocumentParser;
import com.tcd.cranfield.util.CranfieldQueryParser;

public class CranfieldApp {

	public static void main(String[] args) {
		File cranfieldDataFile = new File(args[0]);
		File cranfieldQueryFile = new File(args[1]);
		
		//parse documents
		CranfieldDocumentParser documentParser = new CranfieldDocumentParser();
		List<Document> cranfieldDocList = documentParser.parseCranfieldData(cranfieldDataFile);
		
		//index documents
		LuceneIndexer luceneIndexer = new LuceneIndexer();
		Analyzer analyzer = luceneIndexer.indexOnVectorSpace(cranfieldDocList);
		
		//parse queries
		CranfieldQueryParser queryParser = new CranfieldQueryParser();
		List<CranfieldQuery> cranfieldQueryList = queryParser.parseQuery(cranfieldQueryFile);
		System.out.println(cranfieldQueryList.size());
		//search using parsed queries
	}

}

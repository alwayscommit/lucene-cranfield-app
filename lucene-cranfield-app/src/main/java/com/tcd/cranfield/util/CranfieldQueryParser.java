package com.tcd.cranfield.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

import com.tcd.cranfield.model.CranfieldQuery;

public class CranfieldQueryParser {

	private static final String DOC_ID_PATTERN = ".I ";
	private static final String DOC_BODY_PATTERN = ".W";
	private static final String BLANK_SPACE = " ";

	public List<CranfieldQuery> parseQuery(File cranfieldQueryFile) {
		List<CranfieldQuery> queryList = new ArrayList<CranfieldQuery>();

		String line = "";
		Integer docId = 0;
		try (Scanner scanner = new Scanner(cranfieldQueryFile)) {
			// entire file loop
			while (scanner.hasNext()) {
				if (StringUtils.isEmpty(line)) {
					line = scanner.nextLine();
				}
				// intra-document looping
				if (line.startsWith(DOC_ID_PATTERN)) {
					// create a new cranfield document
					CranfieldQuery cranfieldQuery = new CranfieldQuery();
					//as the docIds in the document are incorrect
					cranfieldQuery.setDocId((++docId).toString());
					// returns next next if it's a new section
					setBody(cranfieldQuery, scanner);
					queryList.add(cranfieldQuery);
				}

			}
			scanner.close();
			return queryList;
		} catch (IOException e) {
			System.out.println("Exception occurred while parsing cranfield data :: " + e);
		}
		return null;
	}

	private String setBody(CranfieldQuery cranfieldQuery, Scanner scanner) {
		String body = "";
		String line = scanner.nextLine();
		if (StringUtils.isNotEmpty(line)) {
			if (line.equals(DOC_BODY_PATTERN)) {
				while (scanner.hasNext()) {
					String nextLine = scanner.nextLine();
					if (nextLine.startsWith(DOC_ID_PATTERN)) {
						cranfieldQuery.setQuery(body);
						return nextLine;
					} else {
						body = body + BLANK_SPACE + nextLine;
					}
				}
			}
			cranfieldQuery.setQuery(body);
		}
		return null;
	}

	private void setDocumentId(CranfieldQuery cranfieldQuery, String line) {
		String docId = line.split(BLANK_SPACE)[1];
		// Field.Store.YES ensures that this field will be stored and retrievable with
		// the results
		cranfieldQuery.setDocId(docId);
	}

}

package com.lucene.cranfield.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import com.lucene.cranfield.model.CranfieldDocument;

public class CranfieldDocumentParser {

	private static final String DOC_ID_PATTERN = ".I ";
	private static final String DOC_TITLE_PATTERN = ".T";
	private static final String DOC_AUTHOR_PATTERN = ".A";
	private static final String DOC_BIBLIOGRAPHY_PATTERN = ".B";
	private static final String DOC_BODY_PATTERN = ".W";
	private static final String BLANK_SPACE = " ";
	private static final String STARTS_WITH_ANY_HEADER = ".";

	public List<CranfieldDocument> parseCranfieldData(String path) {
		path = "D:\\- Trinity\\Information Retrieval and Web Search\\Assignment\\submission\\cranfield data\\cran.all.1400";
//		path = "D:\\- Trinity\\Information Retrieval and Web Search\\Assignment\\submission\\lucene-cranfield-app\\lucene-cranfield-app\\src\\main\\resources\\test.txt";
		List<CranfieldDocument> cranfieldDocList = new ArrayList<CranfieldDocument>();

		String line = "";
		try (Scanner scanner = new Scanner(new File(path))) {
			// entire file loop
			while (scanner.hasNext()) {
				if(StringUtils.isEmpty(line)) {
					line = scanner.nextLine();
				}
				// intra-document looping
				if (line.startsWith(DOC_ID_PATTERN)) {
					// create a new cranfield document
					CranfieldDocument cranfieldDoc = new CranfieldDocument();

					setDocumentId(cranfieldDoc, line);
					// returns next next if it's a new section
					String authorLine = setTitle(cranfieldDoc, scanner);
					String bibliographyLine = setAuthor(cranfieldDoc, scanner, authorLine);
					String bodyLine = setBibliography(cranfieldDoc, scanner, bibliographyLine);
					line = setBody(cranfieldDoc, scanner, bodyLine);
					cranfieldDocList.add(cranfieldDoc);
				}

			}
			scanner.close();
			System.out.println(cranfieldDocList.size());
		} catch (IOException e) {
			System.out.println("Exception occurred while parsing cranfield data :: " + e);
		}
		return null;
	}

	private String setBody(CranfieldDocument cranfieldDoc, Scanner scanner, String bodyLine) {
		String body = "";
		if (bodyLine.equals(DOC_BODY_PATTERN)) {
			while (scanner.hasNext()) {
				String nextLine = scanner.nextLine();
				//Because .A, .B, do occur in the body as bullet points so to avoid that, we ensure that the next
				//element is a new doc id
				if (nextLine.startsWith(DOC_ID_PATTERN)) {
					cranfieldDoc.setBody(new TextField("body", body, Field.Store.YES));
					return nextLine;
				} else {
					body = body + BLANK_SPACE + nextLine;
				}
			}
			// last body
			cranfieldDoc.setBody(new TextField("body", body, Field.Store.YES));
		}
		return null;
	}

	private String setBibliography(CranfieldDocument cranfieldDoc, Scanner scanner, String bibliographyLine) {
		String bibliography = "";
		if (bibliographyLine.equals(DOC_BIBLIOGRAPHY_PATTERN)) {
			while (scanner.hasNext()) {
				String nextLine = scanner.nextLine();
				if (nextLine.startsWith(STARTS_WITH_ANY_HEADER)) {
					cranfieldDoc.setBibliography(new StringField("bibliography", bibliography, Field.Store.YES));
					return nextLine;
				} else {
					bibliography = bibliography + BLANK_SPACE + nextLine;
				}
			}
		}
		return null;
	}

	private String setAuthor(CranfieldDocument cranfieldDoc, Scanner scanner, String authorLine) {
		String author = "";
		if (authorLine.equals(DOC_AUTHOR_PATTERN)) {
			while (scanner.hasNext()) {
				String nextLine = scanner.nextLine();
				if (nextLine.startsWith(STARTS_WITH_ANY_HEADER)) {
					cranfieldDoc.setAuthor(new TextField("author", author, Field.Store.YES));
					return nextLine;
				} else {
					author = author + BLANK_SPACE + nextLine;
				}
			}
		}
		return null;
	}

	private String setTitle(CranfieldDocument cranfieldDoc, Scanner scanner) {
		String title = "";
		String line = scanner.nextLine();
		if (StringUtils.isNotEmpty(line)) {
			if (line.equals(DOC_TITLE_PATTERN)) {
				while (scanner.hasNext()) {
					String nextLine = scanner.nextLine();
					if (nextLine.startsWith(STARTS_WITH_ANY_HEADER)) {
						cranfieldDoc.setTitle(new TextField("title", title, Field.Store.YES));
						return nextLine;
					} else {
						title = title + BLANK_SPACE + nextLine;
					}
				}
			}
		}
		return null;
	}

	private void setDocumentId(CranfieldDocument cranfieldDoc, String line) {
		String docId = line.split(" ")[1];
		// Field.Store.YES ensures that this field will be stored and retrievable with
		// the results
		cranfieldDoc.setId(new StringField("docId", docId, Field.Store.YES));
	}

}

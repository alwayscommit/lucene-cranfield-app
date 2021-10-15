package com.tcd.cranfield.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class CranfieldDocumentParser {

	private static final String ID_PATTERN = ".I ";
	private static final String TITLE_PATTERN = ".T";
	private static final String AUTHOR_PATTERN = ".A";
	private static final String BIBLIOGRAPHY_PATTERN = ".B";
	private static final String BODY_PATTERN = ".W";
	private static final String BLANK_SPACE = " ";
	private static final String STARTS_WITH_ANY_HEADER = ".";

	public List<Document> parseCranfieldData(File cranfieldData) {
//		path = "D:\\- Trinity\\Information Retrieval and Web Search\\Assignment\\submission\\cranfield data\\cran.all.1400";
		List<Document> cranfieldDocList = new ArrayList<Document>();

		String line = "";
		try (Scanner scanner = new Scanner(cranfieldData)) {
			// entire file loop
			while (scanner.hasNext()) {
				if(StringUtils.isEmpty(line)) {
					line = scanner.nextLine();
				}
				// intra-document looping
				if (line.startsWith(ID_PATTERN)) {
					// create a new cranfield document
					Document cranfieldDoc = new Document();

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
			return cranfieldDocList;
		} catch (IOException e) {
			System.out.println("Exception occurred while parsing cranfield data :: " + e);
		}
		return cranfieldDocList;
	}

	private String setBody(Document cranfieldDoc, Scanner scanner, String bodyLine) {
		String body = "";
		if (bodyLine.equals(BODY_PATTERN)) {
			while (scanner.hasNext()) {
				String nextLine = scanner.nextLine();
				//Because .A, .B, do occur in the body as bullet points so to avoid that, we ensure that the next
				//element is a new doc id
				if (nextLine.startsWith(ID_PATTERN)) {
					cranfieldDoc.add(new TextField("body", body, Field.Store.YES));
					return nextLine;
				} else {
					body = body + BLANK_SPACE + nextLine;
				}
			}
			// last body
			cranfieldDoc.add(new TextField("body", body, Field.Store.YES));
		}
		return null;
	}

	private String setBibliography(Document cranfieldDoc, Scanner scanner, String bibliographyLine) {
		String bibliography = "";
		if (bibliographyLine.equals(BIBLIOGRAPHY_PATTERN)) {
			while (scanner.hasNext()) {
				String nextLine = scanner.nextLine();
				if (nextLine.startsWith(STARTS_WITH_ANY_HEADER)) {
					cranfieldDoc.add(new StringField("bibliography", bibliography, Field.Store.YES));
					return nextLine;
				} else {
					bibliography = bibliography + BLANK_SPACE + nextLine;
				}
			}
		}
		return null;
	}

	private String setAuthor(Document cranfieldDoc, Scanner scanner, String authorLine) {
		String author = "";
		if (authorLine.equals(AUTHOR_PATTERN)) {
			while (scanner.hasNext()) {
				String nextLine = scanner.nextLine();
				if (nextLine.startsWith(STARTS_WITH_ANY_HEADER)) {
					cranfieldDoc.add(new TextField("author", author, Field.Store.YES));
					return nextLine;
				} else {
					author = author + BLANK_SPACE + nextLine;
				}
			}
		}
		return null;
	}

	private String setTitle(Document cranfieldDoc, Scanner scanner) {
		String title = "";
		String line = scanner.nextLine();
		if (StringUtils.isNotEmpty(line)) {
			if (line.equals(TITLE_PATTERN)) {
				while (scanner.hasNext()) {
					String nextLine = scanner.nextLine();
					if (nextLine.startsWith(STARTS_WITH_ANY_HEADER)) {
						cranfieldDoc.add(new TextField("title", title, Field.Store.YES));
						return nextLine;
					} else {
						title = title + BLANK_SPACE + nextLine;
					}
				}
			}
		}
		return null;
	}

	private void setDocumentId(Document cranfieldDoc, String line) {
		String docId = line.split(BLANK_SPACE)[1];
		// Field.Store.YES ensures that this field will be stored and retrievable with
		// the results
		cranfieldDoc.add(new StringField("docId", docId, Field.Store.YES));
	}

}

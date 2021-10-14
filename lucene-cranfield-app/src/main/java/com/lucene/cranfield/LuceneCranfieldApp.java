package com.lucene.cranfield;

import com.lucene.cranfield.service.CranfieldDocumentParser;

public class LuceneCranfieldApp {

	public static void main(String[] args) {
		
		//parse documents
		CranfieldDocumentParser parser = new CranfieldDocumentParser();
		parser.parseCranfieldData("hi");
	}

}

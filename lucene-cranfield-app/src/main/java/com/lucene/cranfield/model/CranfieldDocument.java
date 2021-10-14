package com.lucene.cranfield.model;

import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class CranfieldDocument {

	// StringField cannot be tokenized, it is used for ids, filenames, urls, etc
	private StringField id;
	// TextField can be tokenized, it is used for general text like body paragraph.
	private TextField title;
	private TextField body;
	private StringField bibliography;
	private TextField author;

	public StringField getId() {
		return id;
	}

	public void setId(StringField id) {
		this.id = id;
	}

	public TextField getTitle() {
		return title;
	}

	public void setTitle(TextField title) {
		this.title = title;
	}
	
	public TextField getBody() {
		return body;
	}

	public void setBody(TextField body) {
		this.body = body;
	}

	public StringField getBibliography() {
		return bibliography;
	}

	public void setBibliography(StringField bibliography) {
		this.bibliography = bibliography;
	}

	public TextField getAuthor() {
		return author;
	}

	public void setAuthor(TextField author) {
		this.author = author;
	}

}

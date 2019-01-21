package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value=Include.NON_NULL, content=Include.NON_EMPTY)
public class UserType {

//	private List<Book> books;	

	private List<Map<String, Book>> books = new ArrayList<>();

	public List<Map<String, Book>> getBooks() {
		return books;
	}

	
}

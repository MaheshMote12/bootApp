package com.example.demo;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Book;
import com.example.demo.model.CollectionList;
import com.example.demo.model.UserType;
import com.example.demo.util.MultipartUtil;

@SpringBootApplication
@Controller
public class ExcelApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExcelApplication.class, args);
	}
	
	@Autowired
	private MultipartUtil util;

	@GetMapping("/")
	public String in() {
		return "index";
	}
	
	@ResponseBody
	@PostMapping("/import")
	public UserType ex(@RequestParam("file") MultipartFile file ) {
		
		return util.process(file);
		
		
		
	}
	
	@ResponseBody
	@GetMapping("/li")
	public CollectionList l() {
		
		CollectionList l = new CollectionList();
		
		Book book = new Book();
		book.setCategory("d");
		book.setCategory_order("5");
		book.setId("d");
		book.setSubCategory("d");
		book.setSubCategory_order("df");
		
		
		Book book2 = new Book();
		book2.setCategory("d");
		book2.setCategory_order("5");
		book2.setId("d");
		book2.setSubCategory("d");
		book2.setSubCategory_order("df");
		
		
		HashMap<String,Book> map = new HashMap<>();
		map.put("AF", book);
		map.put("PF", book2);
		
		UserType type = new UserType();
//		type.setBooks(map);
		
		l.setCollectionList(type);
		
		return l;
		
	}
	
}


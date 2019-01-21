package com.example.demo.model;

import lombok.Data;

@Data
public class Book {

	private String id;
	private String category;
	private String subCategory;
	private String category_order;
	private  String subCategory_order;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	public String getCategory_order() {
		return category_order;
	}
	public void setCategory_order(String category_order) {
		this.category_order = category_order;
	}
	public String getSubCategory_order() {
		return subCategory_order;
	}
	public void setSubCategory_order(String subCategory_order) {
		this.subCategory_order = subCategory_order;
	}
	
	
	
	
}

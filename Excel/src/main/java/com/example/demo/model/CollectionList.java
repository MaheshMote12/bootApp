package com.example.demo.model;

import java.util.List;

import lombok.Data;

@Data
public class CollectionList {
	
	private UserType collectionList;

	public UserType getCollectionList() {
		return collectionList;
	}

	public void setCollectionList(UserType collectionList) {
		this.collectionList = collectionList;
	}
	
	
	
}

package com.example.demo.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Book;
import com.example.demo.model.CollectionList;
import com.example.demo.model.UserType;

@Component
public class MultipartUtil {

	
	public UserType process(MultipartFile file) {
		
		
		CollectionList list = null;
		
		UserType ua = new UserType();
		
		if(file!=null) {
			list = new CollectionList();
			
			try {
				InputStream is = file.getInputStream();
				
					Workbook workbook;
					workbook = WorkbookFactory.create(is);
					Sheet sheet = workbook.getSheet("Categories and Sub-Categories"); 
					
					if(sheet == null) {
						System.out.println("Sheet Is Null");
						return null;
					}
					
					Iterator<Row> rows = sheet.iterator();
					DataFormatter formatter = new DataFormatter();
					while (rows.hasNext()) {
						Row row = rows.next();
						if (row.getRowNum()==0) continue;

						System.out.println(row.getCell(10).getStringCellValue());
						
//						String[] utype = row.getCell(10).getStringCellValue().split(",");
//						String[] bookIds = row.getCell(17).getStringCellValue().split(",");

						
						String bookidAsString = row.getCell(17) == null ? " " : row.getCell(17).getStringCellValue();
						String[] bookIds = bookidAsString.split(",");
						
						String userType = row.getCell(10) == null ? " " :row.getCell(10).getStringCellValue();
						String[] utype = userType.split(",");
						
						
						String category_order = formatter.formatCellValue(row.getCell(12));
						String category = formatter.formatCellValue(row.getCell(13));
						String subcategory_order = formatter.formatCellValue(row.getCell(15));
						String subcategory = formatter.formatCellValue(row.getCell(16));
						
						
						
						Stream.of(utype).forEach(str ->{
							
							
							Map<String, Book> books = createBooks(str, bookIds, category_order,category, subcategory_order, subcategory);
							
							ua.getBooks().add(books);
							
							System.out.println(books);
							
						});
						
						
						
						
					}
					
					
					
					
				} catch (EncryptedDocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		}
		
		return ua;
	}

	private Map<String, Book> createBooks(String str, String[] bookIds, String category_order, String category,
			String subcategory_order, String subcategory) {

		Map<String, Book> map = new HashMap<>();
		
		Stream.of(bookIds).forEach(id ->{
			
			
			
			Book book = new Book();
			book.setCategory(subcategory);
			book.setCategory_order(subcategory_order);
			book.setId(id);
			book.setSubCategory(subcategory);
			book.setSubCategory_order(subcategory_order);
			
			map.put(str, book);
			
		});
		
		return map;
	}

 	
}

package com.week1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to read file and parse it to get all transactions and count of items in-memory
 * @author saurabh_jogalekar
 *
 */
public class DataFileReader {

	double[][] dataSet = null;
	FileReader file_reader = null;
	String file_name = "";
	Map< List<String>, Integer> frequency_count ; 
	List<List<String>> transaction_table; //data structure to store transactions in-memory
	long[] label = null;
	Map<String, Integer> one_itemset ; //data structure to store frequency of each item
	
	
	public long[] getLabel() {
		return label;
	}

	public DataFileReader(String fileName) throws Exception {	
		this.file_name = fileName;
		this.file_reader = new FileReader(new File(this.file_name));
		this.transaction_table = new ArrayList<>();
		frequency_count = new HashMap<>();
		one_itemset = new HashMap<>();
		file_reader.close();
	}
	
	public void generateDataSet() throws Exception {
		file_reader = new FileReader(new File(file_name));
		BufferedReader buffered_reader = new BufferedReader(file_reader);
		String line = "";
		while(( line =buffered_reader.readLine()) != null){
			
			
			String[] splitLine = line.split(";");
			List<String> perTransaction = new ArrayList<>(Arrays.asList(splitLine));
			transaction_table.add(perTransaction);
			for (String string : splitLine) {
				ArrayList temp = new ArrayList<>(Arrays.asList(string));
				if(frequency_count.get(temp)!=null) {
					frequency_count.put(temp, frequency_count.get(temp)+1);	
					one_itemset.put(string, one_itemset.get(string)+1);
				}
				else {
					frequency_count.put(temp, 1);
					one_itemset.put(string, 1);
				}
			}
		}
		buffered_reader.close();
		file_reader.close();
	}

	public Map<List<String>, Integer> getFrequency_count() {
		return frequency_count;
	}
	
	
	public List<List<String>> getTransactionTable() {
		return transaction_table;
	}
	
	public Map<String, Integer> getOne_itemset() {
		return one_itemset;
	}
}

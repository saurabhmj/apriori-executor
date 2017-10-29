package com.week1;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author saurabh_jogalekar
 * This class will implement the apriori algorithm on specified input and output file
 *
 */
public class AprioriExecutor {

	private static final int MINIMUM_SUPPORT = 771; //10% of dataset
	private static final String INPUT_PATH = "resources\\Dataset.txt";
	private static final String OUTPUT_PATH = "resources\\patterns.txt";
	private Map<List<String>, Integer> frequency_count = new HashMap<>();
	private List<List<String>> reference_list;
	private List<List<String>> transactionTable;
	private BufferedWriter buffered_writer;

	/**
	 * Constructor for initializing output path of apriori
	 * @param output_path 
	 * @throws IOException - if path not found
	 */
	public AprioriExecutor(String output_path) throws IOException   {
		buffered_writer = new BufferedWriter(new FileWriter(output_path));
	}


	/**
	 *Prunes the itemsets by the minimmum support, and writes frequent itemsets to output file
	 * @return - list of items to be removed from the map of transactions
	 * @throws IOException
	 */
	private  ArrayList<List<String>> pruneItemsets() throws IOException {
		ArrayList<List<String>> toRemove = new ArrayList<>();
		ArrayList<List<String>> sortedList = new ArrayList<>(this.frequency_count.keySet());
		Collections.sort(sortedList, new FrequencyComparator(this.frequency_count));
		for (List<String> entry : sortedList) {
			if(this.frequency_count.get(entry) > MINIMUM_SUPPORT) {
				String prepKeys = "";
				for (String string : entry) {

					prepKeys = prepKeys.concat(string);

					prepKeys = prepKeys.concat(";");
				}
				prepKeys = prepKeys.substring(0, prepKeys.length() -1 );
				String prep = this.frequency_count.get(entry) + ":" + prepKeys;
				this.buffered_writer.append(prep);
				this.buffered_writer.newLine();
			}

			else {
				toRemove.add(entry);
			}
		}
		return toRemove;
	}


	/**
	 * Runs apriori recursively  to find frequent itemsets.
	 * @param k_candidates - k-frequent candidates 
	 * @param k - value of k, used for printing to console
	 * @throws IOException
	 */
	private void runApriori(Map<List<String>, Integer> k_candidates, int k) throws IOException {
		Map<List<String>, Integer> k_plus_one_candidates = generateCandidates(k_candidates);
		k_plus_one_candidates = findCountInTransactions(k_plus_one_candidates);
		List keys = new ArrayList<>(k_plus_one_candidates.keySet());
		Collections.sort(keys, new FrequencyComparator(k_plus_one_candidates));

		System.out.println("Size of "+(k+1)+" frequent candidates before elimination : "+ k_plus_one_candidates.size());
		k_plus_one_candidates = eliminateCandidates(k_plus_one_candidates);
		System.out.println("Size of "+(k+1)+" candidates after elimination : "+ k_plus_one_candidates.size());
		System.out.println("--------------------------------------------");
		if(k<4 && k_plus_one_candidates.size()>0) {
			writeToFile(k_plus_one_candidates);
			runApriori(k_plus_one_candidates, k+1);
		}
		else {
			buffered_writer.close();
			return;

		}

	}

	private void writeToFile(Map<List<String>, Integer> k_plus_one_candidates) throws IOException {
		ArrayList<List<String>> sortedList = new ArrayList<>(k_plus_one_candidates.keySet());
		Collections.sort(sortedList, new FrequencyComparator(k_plus_one_candidates));
		for (List<String> entry : sortedList) {

			String prepKeys = "";
			for (String string : entry) {

				prepKeys = prepKeys.concat(string);

				prepKeys = prepKeys.concat(";");
			}
			prepKeys = prepKeys.substring(0, prepKeys.length() -1 );
			String prep = k_plus_one_candidates.get(entry) + ":" + prepKeys;
			buffered_writer.append(prep);
			buffered_writer.newLine();
		}


	}

	/**
	 * Generates candidate items
	 * @param k_plus_one_candidates - data structure for storing candidates
	 * @return
	 */
	private Map<List<String>, Integer> findCountInTransactions(Map<List<String>, Integer> k_plus_one_candidates) {

		for (List<String> list : transactionTable) {
			for (List<String> k_plus_one_list : k_plus_one_candidates.keySet()) {
				if(list.containsAll(k_plus_one_list)) {
					k_plus_one_candidates.put(k_plus_one_list, k_plus_one_candidates.get(k_plus_one_list)+1);
				}
			}
		}

		return k_plus_one_candidates;

	}

	/**
	 * Method to remove non-frequent itemsets
	 * @param k_plus_one_candidates - map to remove candidates
	 * @return - map without non-frequent itemsets
	 */
	private Map<List<String>, Integer> eliminateCandidates(Map<List<String>, Integer> k_plus_one_candidates) {

		ArrayList<List<String>> toRemove = new ArrayList<>();

		for (Entry<List<String>, Integer> entry : k_plus_one_candidates.entrySet()) {
			if(entry.getValue() <= MINIMUM_SUPPORT )
				toRemove.add(entry.getKey());
		}

		for (List<String> list : toRemove) {
			k_plus_one_candidates.remove(list);
		}

		return k_plus_one_candidates;
	}

	/**
	 * Generates candidate items
	 * @param k_plus_one_candidates - data structure for storing candidates
	 * @return
	 */
	private Map<List<String>, Integer> generateCandidates(Map<List<String>, Integer> k_candidates) {
		Map<List<String>, Integer> k_plus_1_candidates = new HashMap<>();
		ArrayList<List<String>> reference_list = new ArrayList<>(k_candidates.keySet());
		for(int i=0;i<reference_list.size();i++) {
			List<String> ith_list = reference_list.get(i);
			for(int j=i+1;j<reference_list.size();j++) {

				List<String> jth_list = reference_list.get(j);

				for (String string : jth_list) {
					List<String> toAdd = new ArrayList<>();
					toAdd.addAll(ith_list);
					if(!ith_list.contains(string)) {
						toAdd.add(string);
						Collections.sort(toAdd);
						k_plus_1_candidates.put(toAdd,0);
					}
				}
			}
		}
		return k_plus_1_candidates;
	}

	private void sortTransactions(Map<String, Integer> map) {
		for (Object object : transactionTable) {
			Collections.sort(((List) object), new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					if(map.get(o1) < map.get(o2))
						return 1;
					if(map.get(o1) > map.get(o2))
						return -1;

					return 0;
				}
			});
		}

	}

	private  void removeObjects(ArrayList<List<String>> toRemove) {
		for (List list : toRemove) {
			reference_list.remove(list);
			frequency_count.remove(list);
			for (Object object : transactionTable) {
				((List) object).remove(list);
			}
		}

	}
	
	
	public static void main(String[] args) throws Exception {

		AprioriExecutor apriori_executor = new AprioriExecutor(OUTPUT_PATH);
		DataFileReader  data_filereader = new DataFileReader(INPUT_PATH);
		data_filereader.generateDataSet();
		apriori_executor.frequency_count = data_filereader.getFrequency_count();

		apriori_executor.reference_list= new ArrayList<>(apriori_executor.frequency_count.keySet());

		Collections.sort(apriori_executor.reference_list, new FrequencyComparator(apriori_executor.frequency_count));

		ArrayList<List<String>> toRemove = apriori_executor.pruneItemsets();


		apriori_executor.transactionTable = data_filereader.getTransactionTable();
		apriori_executor.removeObjects(toRemove);

		apriori_executor.sortTransactions(data_filereader.getOne_itemset());
		apriori_executor.runApriori(apriori_executor.frequency_count, 1);
		apriori_executor.buffered_writer.close();
	}


}

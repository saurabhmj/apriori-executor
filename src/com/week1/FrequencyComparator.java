package com.week1;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author saurabh_jogalekar
 *
 *Comparator implementation to sort a list based on count from input map
 */
public class FrequencyComparator implements Comparator<List> {

	Map<List<String>, Integer> transactions = new HashMap<List<String>, Integer>();
	
	public Map<List<String>, Integer> getTransactions() {
		return transactions;
	}
	
	public void setTransactions(Map<List<String>, Integer> transactions) {
		this.transactions = transactions;
	}
	/**
	 * @param transactions - map to compare frequency against
	 */
	public FrequencyComparator(Map<List<String>, Integer> transactions) {
		
		this.transactions = transactions;
		// TODO Auto-generated constructor stub
	}
	@Override
	public int compare(List o1, List o2) {
		if(transactions.get(o1) < transactions.get(o2))
			return 1;
		if(transactions.get(o1) > transactions.get(o2))
			return -1;
		
		return 0;

	}

}

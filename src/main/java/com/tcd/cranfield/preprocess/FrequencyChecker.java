package com.tcd.cranfield.preprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ranglana
 * This class was used to calculate the frequency of each word to figure out stopwords.
 */
public class FrequencyChecker {

	public static void main(String[] args) throws FileNotFoundException {

		File cranfieldDataFile = new File(
				"D:\\- Trinity\\Information Retrieval and Web Search\\Assignment\\submission\\cranfield data\\cran.all.1400");
		Scanner scanner = new Scanner(cranfieldDataFile);

		Map<String, Integer> frequencyMap = new LinkedHashMap<String, Integer>();

		while (scanner.hasNext()) {
			String line = scanner.next();
			String[] words = line.split(" ");

			for (int i = 0; i < words.length; i++) {
				int count = frequencyMap.containsKey(words[i]) ? frequencyMap.get(words[i]) : 0;
				frequencyMap.put(words[i], count + 1);
			}
		}

		scanner.close();

		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		frequencyMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

		System.out.println(sortedMap);
		
		Set<String> stopWordsSet = sortedMap.entrySet().stream().filter(map -> map.getValue()>500)
				.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue())).keySet();

		System.out.println(stopWordsSet);
	}
}

package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages
 * in which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {

	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the
	 * associated value is an array list of all occurrences of the keyword in
	 * documents. The array list is maintained in DESCENDING order of frequencies.
	 */
	HashMap<String, ArrayList<Occurrence>> keywordsIndex;

	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;

	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String, ArrayList<Occurrence>>(1000, 2.0f);
		noiseWords = new HashSet<String>(100, 2.0f);
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword
	 * occurrences in the document. Uses the getKeyWord method to separate keywords
	 * from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an
	 *         Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String, Occurrence> loadKeywordsFromDocument(String docFile) throws FileNotFoundException {
		// System.out.println("In loadKeywordsFromDocument");

		/** COMPLETE THIS METHOD **/
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code

		Scanner sc = new Scanner(new File(docFile));

		HashMap<String, Occurrence> doc = new HashMap<String, Occurrence>();

		while (sc.hasNext()) {
			String curr = getKeyword(sc.next());

			// System.out.println("Current Keyword: " + curr);

			if (curr != null) {
				if (doc.containsKey(curr))
					doc.get(curr).frequency++;
				else
					doc.put(curr, new Occurrence(docFile, 1));
			}
		}

		// System.out.println(doc);
		return doc;
	}

	/**
	 * Merges the keywords for a single document into the master keywordsIndex hash
	 * table. For each keyword, its Occurrence in the current document must be
	 * inserted in the correct place (according to descending order of frequency) in
	 * the same keyword's Occurrence list in the master hash table. This is done by
	 * calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String, Occurrence> kws) {
		// System.out.println("In mergeKeywords");

		/** COMPLETE THIS METHOD **/
		for (String key : kws.keySet()) {
			ArrayList<Occurrence> list;
			if (keywordsIndex.containsKey(key))
				list = keywordsIndex.get(key);
			else
				list = new ArrayList<Occurrence>();

			list.add(kws.get(key));
			insertLastOccurrence(list);
			keywordsIndex.put(key, list);
		}

	}

	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of
	 * any trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!' NO
	 * OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be
	 * stripped So "word!!" will become "word", and "word?!?!" will also become
	 * "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		// System.out.println("In getKeyword");

		/** COMPLETE THIS METHOD **/
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code

		String term = word;

		while (term.length() > 0 && Character.isLetter(term.charAt(term.length() - 1)) == false) {

			term = term.substring(0, term.length() - 1);
		}
		int k = 0;

		while (k < term.length()) {
			if (Character.isLetter(term.charAt(k)) != true)
				return null;
			k++;
		}

		term = term.toLowerCase();

		if (noiseWords.contains(term) || term.equals("")) { // Added "" comparison
			return null;
		}
		return noiseWords.contains(term) ? null : term;
	}

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in
	 * the list, based on ordering occurrences on descending frequencies. The
	 * elements 0..n-2 in the list are already in the correct order. Insertion is
	 * done by first finding the correct spot using binary search, then inserting at
	 * that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary
	 *         search process, null if the size of the input list is 1. This
	 *         returned array list is only used to test your code - it is not used
	 *         elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// System.out.println("In insertLastOccurrence");

		/** COMPLETE THIS METHOD **/
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code

		if (occs.size() < 2)
			return null;

		int add = occs.get(occs.size() - 1).frequency;
		ArrayList<Integer> ret = new ArrayList<Integer>();
		int l = 0, r = occs.size() - 2, m = 0;
		while (l <= r) {
			m = (l + r) / 2;
			ret.add(m);

			if (occs.get(m).frequency < add)
				r = m - 1;
			else if (occs.get(m).frequency > add) {
				l = m + 1;
				if (r <= m) // added
					m = m + 1; // added
			} else
				break;
		}

		ret.add(m);

		occs.add(ret.get(ret.size() - 1), occs.remove(occs.size() - 1));

		if (ret.get(ret.size() - 1) == ret.get(ret.size() - 2)) {
			ret.remove(ret.size() - 1);
		}
		return ret;

	}

	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all
	 * keywords, each of which is associated with an array list of Occurrence
	 * objects, arranged in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile       Name of file that has a list of all the document file
	 *                       names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise
	 *                       word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input
	 *                               files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}

		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String, Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2
	 * occurs in that document. Result set is arranged in descending order of
	 * document frequencies.
	 * 
	 * Note that a matching document will only appear once in the result.
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. That is,
	 * if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same
	 * frequency f1, then doc1 will take precedence over doc2 in the result.
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all,
	 * result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in
	 *         descending order of frequencies. The result size is limited to 5
	 *         documents. If there are no matches, returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		System.out.println("In top5Search");

		/** COMPLETE THIS METHOD **/
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code

		ArrayList<String> ret = new ArrayList<String>();
		ArrayList<Occurrence> word1 = (keywordsIndex.containsKey(kw1) == true) ? keywordsIndex.get(kw1) : null;
		ArrayList<Occurrence> word2 = (keywordsIndex.containsKey(kw2) == true) ? keywordsIndex.get(kw2) : null;
		ArrayList<Occurrence> merged = new ArrayList<Occurrence>();

		System.out.println("Word1: " + word1);
		System.out.println("Word2: " + word2);

		if (word1 == null && word2 == null) {
			return null;
		}
		if (word1 != null && word2 == null) {
			merged.addAll(word1);
		}
		if (word2 != null && word1 == null) {
			merged.addAll(word2);
		} else {
			merged.addAll(word1);
			merged.addAll(word2);
		}

		int k = 0;
		System.out.println("In last group");
		// Insertion Sort
		if (word1 != null && word2 != null) {

			while (k < merged.size() - 1) {
				System.out.println("In Loop");
				int y = 1;
				while (y < merged.size() - k) { // Loops through the loop and swaps positions when necessary

					if (merged.get(y - 1).frequency < merged.get(y).frequency) {

						Occurrence t = merged.get(y);
						merged.set(y, merged.get(y - 1));
						merged.set(y - 1, t);

					}
					y++;
				}
				/*
				 * System.out.println("Sorting"); Set<Occurrence> sort = new
				 * LinkedHashSet<Occurrence>(); sort.addAll(merged); merged.clear();
				 * merged.addAll(sort);
				 */

				k++;
			}
		}

		System.out.println("Exited Loop: " + merged);

		// Dedupe HERE:

		for (int l = 0; l < merged.size() - 1; l++) {
			for (int y = l + 1; y < merged.size() - 1; y++) {

				if (merged.get(l).document == merged.get(y).document) {
					merged.remove(y);
					System.out.println("removed duplicate");
				}
			}

		}

		k = 0;
		while (k < merged.size() && k < 5) {
			ret.add(merged.get(k).document);
			k++;
		}

		return ret;
	}

}

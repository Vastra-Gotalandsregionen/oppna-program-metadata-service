package se.vgregion.metaservice.LemmatisationService;

import se.vgregion.metaservice.LemmatisationService.model.Trie;
import se.vgregion.metaservice.LemmatisationService.model.TrieEntry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author sture.svensson
 */
public class Dictionary {

    private Trie root;
    private Map<String, List<TrieEntry>> map = null;
    private int lemmas = 0;
    private int words = 0;

    /**
     * Create a Dictionary from a file
     * The file should be newline separated for each word (lemma)
     * and ","-separated for each variation of that word, the lemma
     * should come first
     * @param file the file
     */
    public Dictionary(File file) throws IOException {
        root = new Trie();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.replaceAll("[{}]", "");
            String[] arr = line.split(",");
            if (arr[0].equals("")) {
                continue;
            }
            byte[] bArray = line.toLowerCase().getBytes();

            for (int i = 0; i < arr.length; i++) {
                String norm = arr[i].toLowerCase();
                TrieEntry te = new TrieEntry();
                te.setbArray(bArray);
                root.insert(norm, te);
                words++;
            }
            lemmas++;
        }
    }

    /**
     * Create a dictionary from a List of strings
     * every string should be a ","-separated list
     * of all the varaitions of the word, the lemma
     * should come first
     * @param wordList the wordlist
     */
    public Dictionary(List<String> wordList) {
        root = new Trie();

        for (String line : wordList) {
            line = line.replaceAll("[{}]", "");
            String[] arr = line.split(",");
            if (arr[0].equals("")) {
                continue;
            }
            byte[] bArray = line.toLowerCase().getBytes();
            for (int i = 0; i < arr.length; i++) {
                String norm = arr[i].toLowerCase();
                TrieEntry te = new TrieEntry();
                te.setbArray(bArray);
                root.insert(norm, te);
                words++;
            }
            lemmas++;
        }
    }

    /**
     * Get the words that are a variation of the query, if there are multiple
     * lemmas with the same word the outer list will have multiple entries.
     * The lemma is the first entry in every list
     * @param query the word to look up
     * @return
     */
    public List<List<String>> getWords(String query) {
        List<List<String>> ret = new LinkedList<List<String>>();
        for (TrieEntry entry : root.getTrieFor(query)) {
            List<String> wordList = new LinkedList<String>();
            String[] arr = new String(entry.getbArray()).split(",");
            for (int i = 0; i < arr.length; i++) {
                wordList.add(arr[i]);
            }
            ret.add(wordList);
        }
        return ret;
    }

    /**
     * Get the number of lemmas in the dictionary
     * @return nr of lemmas
     */
    public int getNrOfLemmas() {
        return lemmas;
    }

    /**
     * Get the total number of words in the dictionary
     * @return total number of words in the dictionary
     */
    public int getNrOfWords() {
        return words;
    }

    /**
     * @deprecated
     * Build a dictionary using a hashmap instead, used for testing/comparison
     * @param file
     * @param useHashmap
     * @throws IOException
     */
    public Dictionary(File file, Boolean useHashmap) throws IOException {
        root = new Trie();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
        String line;
        map = new HashMap<String, List<TrieEntry>>();
        while ((line = br.readLine()) != null) {
            line = line.replaceAll("[{}]", "");
            String[] arr = line.split(",");
            if (arr[0].equals("")) {
                continue;
            }
            byte[] bArray = line.toLowerCase().getBytes();

            for (int i = 0; i < arr.length; i++) {
                String norm = arr[i].toLowerCase();
                TrieEntry te = new TrieEntry();
                te.setbArray(bArray);
                if (map.get(norm) == null) {
                    List<TrieEntry> list = new Vector<TrieEntry>(1, 1);
                    list.add(te);
                    map.put(norm, list);
                } else {
                    map.get(norm).add(te);
                }
                words++;
            }
            lemmas++;
        }
    }

    /**
     * @deprecated
     * Use this function when the dictionary was built with a hashmap instead of a trie
     * used for testing/comparison
     * @param word
     * @param useHashMap
     * @return
     */
    public List<List<String>> getWords(String query, Boolean useHashMap) {
        List<List<String>> ret = new LinkedList<List<String>>();

        List<TrieEntry> list = map.get(query);
        if (list != null) {
            for (TrieEntry entry : list) {
                String[] arr = new String(entry.getbArray()).split(",");
                List<String> wordList = new LinkedList<String>();
                for (int i = 0; i < arr.length; i++) {
                    wordList.add(arr[i]);
                }
                ret.add(wordList);
            }
        }
        return ret;
    }
}

package se.vgregion.metaservice.LemmatisationService.model;

import se.vgregion.metaservice.LemmatisationService.model.TrieEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * @author sture.svensson
 */
public class Trie {

    private int position;
    private Object entries = null;
    private Object at = null;
    private Character onlyChildCharacter = null;

    /**
     * Create a root trie
     */
    public Trie() {
        this.position = 0;
    }

    /**
     * Create a child trie
     * @param previous the parent
     */
    public Trie(Trie previous) {
        position = previous.getPosition() + 1;
    }

    /**
     * Returns this tries distance from the root
     * @return position the distance/position
     */
    private int getPosition() {
        return position;
    }

    /**
     * Insert a word in this trie
     * @param norm the name of the word (should be normalized)
     * @param te the entry to insert
     */
    public void insert(String norm, TrieEntry te) {

        // If there are any more characters to insert
        if (position + 1 <= norm.length()) {

            //Get the character
            char current = norm.charAt(position);

            //If the character already is inserted
            if (containsEntry(current)) {
                //check if we should go on
                if (norm.length() > position + 1) {
                    getEntry(current).insert(norm, te);
                    // or if the word should be placed here
                } else {
                    getEntry(current).addAt(te);
                }

                // The character needs to be inserted first
            } else {
                Trie child = new Trie(this);
                child.insert(norm, te);
                addEntry(current, child);
                //Check if the word should be inserted there
                if (norm.length() == (position + 1)) {
                    getEntry(current).addAt(te);
                }
            }
        }
    }

    /**
     * Get a list of tries that matches the query
     * @param query
     * @return
     */
    public List<TrieEntry> getTrieFor(String query) {

        //Fetch the character
        char current = query.charAt(position);

        // The word was not found
        if (!containsEntry(current)) {
            return Collections.EMPTY_LIST;

            // We only return words that have the correct length
        } else if (query.length() == position + 1) {
            if (!containsEntry(current)) {
                return Collections.EMPTY_LIST;
            } else {
                ArrayList<TrieEntry> result = new ArrayList<TrieEntry>();
                //It is possible to find the word at multiple places
                return getEntry(current).getAt();
            }
        }
        // go to next character
        return getEntry(current).getTrieFor(query);
    }

    /**
     * Check if the trie contains an entry for the character
     * @param ch the character
     * @return true if the trie contains the character
     */
    private boolean containsEntry(Character ch) {
        if (entries == null) {
            return false;
        } else if (entries instanceof Trie) {
            return ch.equals(onlyChildCharacter);
        }
        return ((HashMap<Character, Trie>) entries).containsKey(ch);
    }

    /**
     * Get the trie for a specified entry, if the entry does not exist
     * null is returned, check with containsEntry first
     * @param ch the character
     * @return the trie or null if it does not exist
     */
    private Trie getEntry(Character ch) {
        if (entries == null) {
            return null;
        } else if (entries instanceof Trie) {
            return (Trie) entries;
        }
        return ((HashMap<Character, Trie>) entries).get(ch);
    }

    /**
     * Add an entry for a character, if there is more than one character
     * on the trie a map will be created internally
     * @param character the character to be used as key
     * @param entry the entry to use as value
     */
    private void addEntry(Character character, Trie entry) {
        if (entries == null) {
            this.onlyChildCharacter = character;
            entries = entry;
        } else if (entries instanceof Trie) {
            Trie tmp = (Trie) entries;
            entries = new HashMap<Character, Trie>(2);
            ((HashMap<Character, Trie>) entries).put(character, entry);
            ((HashMap<Character, Trie>) entries).put(onlyChildCharacter, tmp);
            onlyChildCharacter = null;
        } else {
            ((HashMap<Character, Trie>) entries).put(character, entry);
        }
    }

    /**
     * Get the list of Entries at the Trie
     * @return the list
     */
    public List<TrieEntry> getAt() {
        if (at == null) {
            at = new Vector<TrieEntry>(1, 1);
        } else if (at instanceof TrieEntry) {
            List<TrieEntry> ret = new Vector<TrieEntry>(1, 1);
            ret.add((TrieEntry) at);
            return ret;
        }
        return (Vector) at;
    }

    /**
     * Add an entry to the trie, if needed a vector is created
     * @param entry the entry to add
     */
    public void addAt(TrieEntry entry) {
        if (at == null) {
            at = entry;
        } else if (at instanceof TrieEntry) {
            TrieEntry tmp = (TrieEntry) at;
            at = new Vector<TrieEntry>(2, 1);
            ((Vector<TrieEntry>) at).add(entry);
            ((Vector<TrieEntry>) at).add(tmp);
        } else {
            ((Vector<TrieEntry>) at).add(entry);
        }
    }
}

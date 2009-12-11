/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.vgregion.metaservice.LemmatisationService;

import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.vgregion.metaservice.LemmatisationService.model.Dictionary;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author sture.svensson
 */
public class DictionaryTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        URL url = ClassLoader.getSystemResource("saldo.txt");
        File file = new File(url.getFile());
        Dictionary dict = null;
        long start = new Date().getTime();
        try {
            dict = new Dictionary(file);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        long time = new Date().getTime() - start;

        /*
        List<String> list = new ArrayList<String>();
        list.add("apa,apan");
        list.add("gris,grisen");
        list.add("g-ris,grisen");
        Dictionary dict = new Dictionary(list);
         */

        System.out.println("Initialized a dictionary with " + dict.getNrOfLemmas() + " lemmas and " + dict.getNrOfWords() + " words in total" +
                ", building took " + time + " milliseconds");


        List<List<String>> entryList = dict.getWords("gris");
        time = new Date().getTime() - start;
        System.out.println("Results (query took " + time + " milliseconds):");
        for (List<String> e : entryList) {
            System.out.println("---------");
            for (String s : e) {
                System.out.println(s);
            }
        }

    }
}

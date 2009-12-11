/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.vgregion.metaservice.LemmatisationService;

import se.vgregion.metaservice.LemmatisationService.Dictionary;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author sture.svensson
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        File file = new File("saldo_1.0.txt");
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
                ", building took " + time +" milliseconds");

        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("\n>");
            String input = in.nextLine();

            start = new Date().getTime();
            List<List<String>> entryList = dict.getWords(input);
            time = new Date().getTime() - start;
            System.out.println("Results (query took "+time+" milliseconds):");
            for (List<String> e : entryList) {
                System.out.println("---------");
                for (String s : e) {
                    System.out.println(s);
                }
            }
        }
    }
}

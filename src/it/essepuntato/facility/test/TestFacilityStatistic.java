package it.essepuntato.facility.test;

import it.essepuntato.facility.statistic.StatisticFacility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Silvio Peroni
 */
public class TestFacilityStatistic {
    public static void main (String[] args) {
        ArrayList<String> prova = new ArrayList<String>();
        
        prova.add("1");prova.add("2");prova.add("3");prova.add("4");prova.add("5");
        prova.add("6");prova.add("7");prova.add("8");prova.add("9");prova.add("10");
        
        List<ArrayList<String>> result = 
                (List<ArrayList<String>>) StatisticFacility.combinationWithoutRepetition(prova, 8);
        
        Iterator<ArrayList<String>> ite = result.iterator();
        for (int i = 1; ite.hasNext(); i++) {
            ArrayList<String> combination = ite.next();
            Iterator<String> cur = combination.iterator();
            System.out.print(i + ": ");
            while (cur.hasNext()) {
                System.out.print(cur.next());
                if (cur.hasNext()) { System.out.print(","); }
            }
            System.out.println("");
        }
    }
}

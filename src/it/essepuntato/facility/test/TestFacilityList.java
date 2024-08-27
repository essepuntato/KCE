package it.essepuntato.facility.test;

import it.essepuntato.facility.list.ListFacility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Silvio Peroni
 */
public class TestFacilityList {
    public static void main(String[] args) {
        ArrayList<String> prova = new ArrayList<String>();
        
        prova.add("Pippo");

        ArrayList<String> nuova = (ArrayList<String>) ListFacility.copy(prova);
        Iterator<String> ite = nuova.iterator();
        while (ite.hasNext()) {
            String cur = ite.next();
            System.out.println(cur);
        }
    }
}

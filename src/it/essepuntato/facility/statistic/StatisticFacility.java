package it.essepuntato.facility.statistic;

import it.essepuntato.facility.list.ListFacility;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silvio Peroni
 */
public class StatisticFacility {
    public static List combinationWithoutRepetition(List l, int k) {
        ArrayList<List> result = new ArrayList<List>();
        
        if (l.size() <= k) {
            result.add(l);
        }
        else {
            result = (ArrayList<List>) 
                    StatisticFacility.combinationWithoutRepetition(result, l, new ArrayList(), k);
        }
        
        return result;
    }
    
    private static List combinationWithoutRepetition(List result, List l, List previous, int k) {
        for (int c = 0; c < l.size() - k + 1; c++) {
            
            List newL = ListFacility.copy(l);
            for (int i = 0; i <= c; i++) {
                newL.remove(0);
            }
            
            //I add myself to the previous
            List newPrevious = ListFacility.copy(previous);
            
            //Base case
            if (k == 1) {
                List combination = ListFacility.copy(newPrevious);
                combination.add(l.get(c));
                result.add(combination);
            }
            else {
                newPrevious.add(l.get(c));
            }
            
            //Recursive step
            if (!newL.isEmpty() && k != 1) {
                StatisticFacility.combinationWithoutRepetition(
                        result, newL, newPrevious, k - 1);
            }
        }
        
        return result;
    }
}

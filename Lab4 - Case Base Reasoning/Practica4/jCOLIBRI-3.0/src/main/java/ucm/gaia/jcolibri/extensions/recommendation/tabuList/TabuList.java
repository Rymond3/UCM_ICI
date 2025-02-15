/**
 * TabuList.java
 * jCOLIBRI2 framework. 
 * @author Juan A. Recio-Garc�a.
 * GAIA - Group for Artificial Intelligence Applications
 * http://gaia.fdi.ucm.es
 * 21/11/2007
 */
package ucm.gaia.jcolibri.extensions.recommendation.tabuList;


import ucm.gaia.jcolibri.cbrcore.CBRCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a list of tabu items. 
 * Tabu items already were presented to the user so the must not presented again.
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with Derek Bridge.
 * @version 1.0
 *
 */
public class TabuList
{
    private static ArrayList<CBRCase> tabu = new ArrayList<CBRCase>();
    
    /**
     * Removes cases from the tabu list. 
     * @param cases to remove
     * @return updated tabu list.
     */
    public static List<CBRCase> removeTabuList(List<CBRCase> cases)
    {
	ArrayList<CBRCase> newList =  new ArrayList<CBRCase>(cases);
	newList.removeAll(tabu);
	return newList;
    }
    
    /**
     * Adds cases to the tabu list.
     * @param tabuCases to add
     */
    public static void updateTabuList(List<CBRCase> tabuCases)
    {
	tabu.addAll(tabuCases);
    }
}

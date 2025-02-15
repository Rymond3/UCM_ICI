package ucm.gaia.jcolibri.extensions.recommendation.casesDisplay;

import ucm.gaia.jcolibri.cbrcore.CBRCase;
import ucm.gaia.jcolibri.cbrcore.CBRQuery;
import ucm.gaia.jcolibri.util.CopyUtils;

/**
 * Object that encapsulates the user answer when cases are shown.<br>
 * This object keeps an internal integer with posible values:
 * <ul>
 *    <li>QUIT
 *    <li>REFINE QUERY
 *    <li>BUY
 * </ul>
 * It also contains the chosen case from the list.<br>
 * If the answer is BUY, the selected case is the final result.<br>
 * If the answer is REFINE QUERY, the selected case can be used in Navigation 
 * by Proposing to elicit the query.<br>
 * The subclass CriticalUserChoice is an extension that also contains
 * the critiques to the chosen case.
 * 
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with Derek Bridge.
 * @version 1.0
 */
public class UserChoice
{
    /** QUIT constant */
    public static final int QUIT = -1;
    /** REFINE_QUERY constant */
    public static final int REFINE_QUERY = -2;
    /** BUY constant */
    public static final int BUY = -3;
    
    
    /** Internal value that stores the choice */
    int choice = -1;
    
    /** Internal value to store the selected case */
    CBRCase selectedCase;
    
    /**
     * Constructor
     * @param choice is the user's choice
     */
    public UserChoice(int choice, CBRCase selectedCase)
    {
	this.choice = choice;
	if(selectedCase != null)
	    this.selectedCase = CopyUtils.copyCBRCase(selectedCase);
	else
	    selectedCase = null;
    }
    
    /**
     * Returns the user choice
     */
    public int getChoice()
    {
	return choice;
    }
    
    /**
     * Returns true if the choice is QUIT
     */
    public boolean isQuit()
    {
	return choice == QUIT;
    }
    
    /**
     * Returns true if the choice is REFINE_QUERY
     */
    public boolean isRefineQuery()
    {
	return choice == REFINE_QUERY;
    }
    
    /**
     * Returns true if the choice is a case
     */
    public boolean isBuy()
    {
	return choice == BUY;
    }
    
    /**
     * Returns the critiqued case as a CBRQuery object.
     */
    public CBRQuery getSelectedCaseAsQuery()
    {
	return this.selectedCase;
    }
    
    /**
     * Returns the critiqued case.
     */
    public CBRCase getSelectedCase()
    {
	return this.selectedCase;
    }
}

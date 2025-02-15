/**
 * Equal.java
 * jCOLIBRI2 framework. 
 * @author Juan A. Recio-Garc�a.
 * GAIA - Group for Artificial Intelligence Applications
 * http://gaia.fdi.ucm.es
 * 28/10/2007
 */
package ucm.gaia.jcolibri.method.retrieve.FilterBasedRetrieval.predicates;

import ucm.gaia.jcolibri.exception.NoApplicableFilterPredicateException;

/**
 * Predicate that compares if two objects are equal.
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with Derek Bridge.
 * @version 1.0
 * @see FilterBasedRetrievalMethod
 * @see FilterConfig
 */
public class Equal implements FilterPredicate
{
    public boolean compute(Object caseObject, Object queryObject) throws NoApplicableFilterPredicateException {
		if((caseObject == null)&&(queryObject==null))
			return true;
		else if(caseObject == null)
			return false;
		else if(queryObject == null)
			return true;
		else
			return caseObject.equals(queryObject);
    }

}

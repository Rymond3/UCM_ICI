/**
 * FilterPredicate.java
 * jCOLIBRI2 framework. 
 * @author Juan A. Recio-Garc�a.
 * GAIA - Group for Artificial Intelligence Applications
 * http://gaia.fdi.ucm.es
 * 28/10/2007
 */
package ucm.gaia.jcolibri.method.retrieve.FilterBasedRetrieval.predicates;

import ucm.gaia.jcolibri.exception.NoApplicableFilterPredicateException;

/**
 * Interface for Predicates
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with Derek Bridge.
 * @version 1.0
 *
 */
public interface FilterPredicate
{
    /**
     * Computes the predicate
     * @param caseObject is the object of the case being compared
     * @param queryObject is the object of the query being compared
     * @return the result of the comparation
     * @throws NoApplicableFilterPredicateException if the predicate cannot be applied to those objects.
     */
    public boolean compute(Object caseObject, Object queryObject) throws NoApplicableFilterPredicateException;
}

/**
 * GreedySelection.java
 * jCOLIBRI2 framework. 
 * @author Juan A. Recio-Garc�a.
 * GAIA - Group for Artificial Intelligence Applications
 * http://gaia.fdi.ucm.es
 * 05/11/2007
 */
package ucm.gaia.jcolibri.method.retrieve.selection.diversity;

import ucm.gaia.jcolibri.cbrcore.CBRCase;
import ucm.gaia.jcolibri.cbrcore.CBRQuery;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import ucm.gaia.jcolibri.method.retrieve.RetrievalResult;
import ucm.gaia.jcolibri.method.retrieve.selection.SelectCases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This method incrementally builds a retrieval set, R. During each step the
 * remaining cases are ordered according to their quality with the highest 
 * quality case added to R. 
 * <br>
 * The quality metric combines diversity and similarity. The quality of a case c 
 * is proportional to the similarity between c and the query, and to the diversity
 * of c relative to those cases so far selected in R.
 * <br>
 * This algorithm is very expensive. It should be applied to small case bases. 
 * <p>See:
 * <p>
 * B. Smyth and P. McClave. Similarity vs. diversity. In ICCBR '01: Proceedings 
 * of the 4th International Conference on Case-Based Reasoning, pages 347-361, 
 * London, UK, 2001. Springer-Verlag.
 * 
 * 
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with Derek Bridge.
 * @version 1.0
 *
 */
public class GreedySelection {

    /******************************************************************************/
    /**                           STATIC METHODS                                 **/
    /******************************************************************************/   
    
    
    /**
     * Executes the greedy selection algorithm
     * @param cases to select from
     * @param query to compare
     * @param simConfig is the knn similarity configuration. Its k determines the number of returned cases 
     * @return k cases (k is defined in simConfig). 
     */
    public static List<CBRCase> greedySelection(Collection<RetrievalResult> cases, CBRQuery query, NNConfig simConfig, int k)  {

    	Collection<CBRCase> C = SelectCases.selectAll(cases);
		List<CBRCase> R = new ArrayList<>();

		for(int i=0; i<k; i++) {
	    	CBRCase best = getMoreQuality(query, C, R, simConfig);
	    	R.add(best);
	    	C.remove(best);
		}

		return R;
    }
    
    /**
     * Returns the case with more quality.
     * @param query to compare
     * @param cases to compare
     * @param R is the set of previous selected cases
     * @param simConfig is the knn similarity config
     * @return the case with more quality
     */
    public static CBRCase getMoreQuality(CBRQuery query, Collection<CBRCase> cases, Collection<CBRCase> R, NNConfig simConfig) {

        // TODO - Testearlo bien

		List<RetrievalResult> nn = NNScoringMethod.evaluateSimilarity(cases, query, simConfig);
		double maxQuality = -Double.MAX_VALUE;

		CBRCase best = null;

		for(RetrievalResult rr: nn)
		{
			double quality = rr.getEval() * relDiversity(rr.get_case(), R, simConfig);
			if(quality > maxQuality)
			{
			maxQuality = quality;
			best = rr.get_case();
			}
		}
		return best;
    }
    
    public static double relDiversity(CBRCase c, Collection<CBRCase> R, NNConfig simConfig)
    {
	if(R.isEmpty())
	    return 1;
	double sum = 0;
	for(CBRCase _case : R)
	{
	    List<CBRCase> aux = new ArrayList<CBRCase>();
	    aux.add(_case);
	    CBRQuery query = new CBRQuery();
	    query.setDescription(c.getDescription());
	    double sim = NNScoringMethod.evaluateSimilarity(aux, query, simConfig).iterator().next().getEval();
	    sum += sim;
	}
	sum = sum/(double)R.size();
	
	return sum;
    }
}

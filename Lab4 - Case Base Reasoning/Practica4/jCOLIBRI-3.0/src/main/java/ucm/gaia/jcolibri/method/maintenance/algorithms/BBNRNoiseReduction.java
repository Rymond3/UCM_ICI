package ucm.gaia.jcolibri.method.maintenance.algorithms;

import ucm.gaia.jcolibri.cbrcore.CBRCase;
import ucm.gaia.jcolibri.exception.InitializingException;
import ucm.gaia.jcolibri.extensions.classification.ClassificationSolution;
import ucm.gaia.jcolibri.method.maintenance.AbstractCaseBaseEditMethod;
import ucm.gaia.jcolibri.method.maintenance.CaseResult;
import ucm.gaia.jcolibri.method.maintenance.CompetenceModel;
import ucm.gaia.jcolibri.method.maintenance.solvesFunctions.CBESolvesFunction;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import ucm.gaia.jcolibri.method.retrieve.RetrievalResult;
import ucm.gaia.jcolibri.method.retrieve.selection.SelectCases;
import ucm.gaia.jcolibri.method.reuse.classification.KNNClassificationConfig;
import ucm.gaia.jcolibri.method.reuse.classification.KNNClassificationMethod;
import ucm.gaia.jcolibri.method.revise.classification.BasicClassificationOracle;
import ucm.gaia.jcolibri.method.revise.classification.ClassificationOracle;
import ucm.gaia.jcolibri.util.ProgressController;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Provides the ability to run the BBNR case base editing algorithm 
 * on a case base to eliminate noise.
 * 
 * @author Lisa Cummins
 * @author Derek Bridge
 * 18/05/07
 */
public class BBNRNoiseReduction extends AbstractCaseBaseEditMethod {

	/**
	 * Simulates the BBNR editing algorithm, returning the cases
	 * that would be deleted by the algorithm.
	 * @param cases The group of cases on which to perform editing.
	 * @param simConfig The similarity configuration for these cases.
	 * @return the list of cases that would be deleted by the 
	 * BBNR algorithm.
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<CBRCase> retrieveCasesToDelete(Collection<CBRCase> cases, KNNClassificationConfig simConfig)
	{	/*
		 * Blame-based Noise Reduction (BBNR) Algorithm
		 * T, Training Set
		 * For each c in T
		 * CSet(c) = Coverage Set of c
		 * LSet(c) = Liability Set of c
		 * End-For
		 *	
		 * TSet = T sorted in descending order of LSet(c) size
		 * c = first case in TSet
		 *	
		 * While |LSet(c)| >0
		 *		TSet = TSet - {c}
		 *		misClassifiedFlag = false
		 *		For each x in CSet(c)
		 *			If x cannot be correctly classified by TSet
		 *				misClassifiedFlag = true
		 *				break
		 *			End-If
		 *		End-For
		 *		If misClassifiedFlag = true
		 *			TSet = TSet + {c}
		 *		End-If
		 *		c = next case in TSet
		 * End-While
		 * 
		 * Return TSet
		 */
	    
		ProgressController.init(this.getClass(), "Blame-based Noise Reduction (BBNR)", ProgressController.UNKNOWN_STEPS);
		Collection<CBRCase> localCases = new LinkedList<CBRCase>();
		for(CBRCase c: cases)
		{	localCases.add(c);
		}
	
		CompetenceModel sc = new CompetenceModel();
		sc.computeCompetenceModel(new CBESolvesFunction(), simConfig, localCases);
		
		List<CaseResult> caseLiabilitySetSizes = new LinkedList<>();
		
		for(CBRCase c:localCases)
		{	Collection<CBRCase> currLiabilitySet = null;
			try 
			{	currLiabilitySet = sc.getLiabilitySet(c);
			} catch (InitializingException e)
			{	e.printStackTrace();
			}
			int liabilitySetSize = 0;

			if(currLiabilitySet != null) 
			{	liabilitySetSize = currLiabilitySet.size();
			}
		
			caseLiabilitySetSizes.add(new CaseResult(c, liabilitySetSize));
			ProgressController.step(this.getClass());
		}
		
		caseLiabilitySetSizes = CaseResult.sortResults(false, caseLiabilitySetSizes);

		LinkedList<CBRCase> allCasesToBeRemoved = new LinkedList<CBRCase>();
		
	    	for(ListIterator<CaseResult> liabIter = caseLiabilitySetSizes.listIterator(); liabIter.hasNext(); )
        	{	CaseResult highestLiability = liabIter.next();
        		if(highestLiability.getResult() <= 0)
        		{	break;    
        		}

			CBRCase removed = highestLiability.getCase();
        		localCases.remove(removed);
        			
        		Collection<CBRCase> covSet = null;
        		try 
        		{	covSet = sc.getCoverageSet(removed);
        		} catch (InitializingException e) 
        		{	e.printStackTrace();
        		}
        		
        		boolean caseMisclassified = false;
        		for(CBRCase query: covSet)
        		{	Collection<RetrievalResult> knn = NNScoringMethod.evaluateSimilarity(localCases, query, simConfig);
        			knn = SelectCases.selectTopKRR(knn, simConfig.getK());
        			try
        			{	KNNClassificationMethod classifier = ((KNNClassificationConfig)simConfig).getClassificationMethod();
        				ClassificationSolution predictedSolution = classifier.getPredictedSolution(knn);
        				ClassificationOracle oracle = new BasicClassificationOracle();
               			
        				if(!oracle.isCorrectPrediction(predictedSolution, query))
        				{	caseMisclassified = true;
        					break;
        				}	
        			} catch(ClassCastException cce)
        			{	org.apache.commons.logging.LogFactory.getLog(BBNRNoiseReduction.class).error(cce);
        				System.exit(0);
        			}
        		}
        		if(caseMisclassified)
        		{	localCases.add(removed);
        		}
        		else
        		{	allCasesToBeRemoved.add(removed);
        		}
			ProgressController.step(this.getClass());
        	}	
		ProgressController.finish(this.getClass());
		return allCasesToBeRemoved;
	}
}
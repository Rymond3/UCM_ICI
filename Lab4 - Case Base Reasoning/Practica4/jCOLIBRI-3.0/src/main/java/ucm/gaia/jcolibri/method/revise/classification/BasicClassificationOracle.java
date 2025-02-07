package ucm.gaia.jcolibri.method.revise.classification;


import ucm.gaia.jcolibri.cbrcore.CBRCase;
import ucm.gaia.jcolibri.cbrcore.CBRCaseBase;
import ucm.gaia.jcolibri.cbrcore.CBRQuery;
import ucm.gaia.jcolibri.extensions.classification.ClassificationSolution;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import ucm.gaia.jcolibri.method.retrieve.RetrievalResult;
import ucm.gaia.jcolibri.method.retrieve.selection.SelectCases;
import ucm.gaia.jcolibri.method.reuse.classification.KNNClassificationConfig;
import ucm.gaia.jcolibri.method.reuse.classification.KNNClassificationMethod;

import java.util.Collection;
import java.util.List;

/**
 * This class represents a decision-maker to decide if a query has been 
 * correctly classified or not and to assign a cost to incorrectly 
 * classified queries.
 * 
 * @author Derek Bridge
 * @author Lisa Cummins
 * 16/05/07
 */
public class BasicClassificationOracle implements ClassificationOracle {
    
    /**
     * Checks if the predicted solution is the correct solution
     * for the given test case.
     * @param predictedSolution the predicted solution.
     * @param testCase the test case (query and correct solution).
     * @return true if the predicted solution is the correct solution
     * for the given test case, false if not. 
     */
    public boolean isCorrectPrediction(ClassificationSolution predictedSolution, CBRCase testCase)
    {	
	    ClassificationSolution correctSolution = (ClassificationSolution)testCase.getSolution();
    	return isCorrectPrediction(predictedSolution, correctSolution);
    }
    
    /**
     * Checks if the predicted solution and the correct solution
     * are the same.
     * @param predictedSolution the predicted solution.
     * @param correctSolution the correct solution.
     * @return true if the predicted solution and the correct 
     * solution are the same, false if not. 
     */
    public boolean isCorrectPrediction(ClassificationSolution predictedSolution, ClassificationSolution correctSolution)
    {	
    	return predictedSolution.getClassification().equals(correctSolution.getClassification());
    }
    
    /**
     * Checks if the query is correctly classified by the given case-base and 
     * similarity configuration.
     * @param query the query to be tested.
     * @param caseBase the case base to use to find the predicted solution.
     * @param knnConfig the similarity configuration.
     * @return true if the query is correctly classified by the given case-base and 
     * similarity configuration, fasle otherwise.
     */
    public boolean isCorrectPrediction(CBRQuery query, CBRCaseBase caseBase, KNNClassificationConfig knnConfig)
    {
        Collection<CBRCase> cases = caseBase.getCases();
	    Collection<RetrievalResult> knn = NNScoringMethod.evaluateSimilarity(cases, query, knnConfig);
	knn = SelectCases.selectTopKRR(knn, knnConfig.getK());
	KNNClassificationMethod classifier = knnConfig.getClassificationMethod();
	ClassificationSolution predictedSolution = classifier.getPredictedSolution(knn);
	return isCorrectPrediction(predictedSolution, (CBRCase)query);
    }

    /**
	 * Calculates the cost of the given solution as a prediction for the 
	 * solution of the given case. The cost returned is 0 for a correct
	 * prediction and 1 for a wrong prediction.
     * @param predictedSolution the predicted solution.
     * @param testCase the test case (query and correct solution).
     * @return the cost of the prediction made (0 if the prediction
     * is correct, 1 otherwise).
     */
    public double getPredictionCost(ClassificationSolution predictedSolution, CBRCase testCase)
    {	
	ClassificationSolution correctSolution = (ClassificationSolution)testCase.getSolution();
		return getPredictionCost(predictedSolution, correctSolution);
    }

    /**
	 * Calculates the cost of the given test solution while bein compared
	 * to the given correct solution. The cost returned is 0 for a correct
	 * prediction and 1 for a wrong prediction.
     * @param predictedSolution the predicted solution.
     * @param correctSolution the correct solution.
     * @return Returns the cost of the prediction made (0 if the prediction
     * is correct, 1 otherwise).
     */
    public double getPredictionCost(ClassificationSolution predictedSolution, ClassificationSolution correctSolution)
    {	
	return isCorrectPrediction(predictedSolution, correctSolution) ? 0 : 1;
    }
    
    /**
     * Calculates the cost of the prediction made by the given case-base and
     * similarity configuration for the solution of the query. The cost 
     * returned is 0 for a correct prediction and 1 for a wrong prediction.
     * @param query the query to be tested.
     * @param caseBase the case base to use to find the predicted solution.
     * @param knnConfig the similarity configuration.
     * @return the cost of the prediction made (0 if the prediction
     * is correct, 1 otherwise).
     */
    public double getPredictionCost(CBRQuery query, CBRCaseBase caseBase, KNNClassificationConfig knnConfig)
    {	Collection<CBRCase> cases = caseBase.getCases();
	Collection<RetrievalResult> knn = NNScoringMethod.evaluateSimilarity(cases, query, knnConfig);
	knn = SelectCases.selectTopKRR(knn, knnConfig.getK());
	KNNClassificationMethod classifier = knnConfig.getClassificationMethod();
	ClassificationSolution predictedSolution = classifier.getPredictedSolution(knn);
	return getPredictionCost(predictedSolution, (CBRCase)query);
    }
}
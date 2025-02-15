package ucm.gaia.jcolibri.extensions.maintenance_evaluation.evaluators;

import org.apache.logging.log4j.LogManager;
import ucm.gaia.jcolibri.casebase.CachedLinealCaseBase;
import ucm.gaia.jcolibri.cbrcore.CBRCase;
import ucm.gaia.jcolibri.cbrcore.CBRCaseBase;
import ucm.gaia.jcolibri.extensions.maintenance_evaluation.MaintenanceEvaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * This evaluation divides the case base into several random folds 
 * (indicated by the user). 
 * For each fold, their cases are used as queries and the remaining folds are 
 * used together as case base. Maintenance is performed on the case-base before
 * running the queries.
 * This process is performed several times.
 * 
 * @author Lisa Cummins.
 * @author Juan A. Recio Garc�a - GAIA http://gaia.fdi.ucm.es
 */
public class MaintenanceNFoldEvaluator extends MaintenanceEvaluator
{
    /**
     * Executes the N-Fold evaluation.
     * @param numFolds the number of randomly generated folds.
     * @param repetitions the number of repetitions
     */
    public void NFoldEvaluation(int numFolds, int repetitions)
    {   try
        {   //Get the time
            long t = (new Date()).getTime();
            int numberOfCycles = 0;

            // Run the precycle to load the case base
            LogManager.getLogger().info("Running precycle()");
            CBRCaseBase caseBase = app.preCycle();

            if (!(caseBase instanceof CachedLinealCaseBase))
        	LogManager.getLogger().warn("Evaluation should be executed using a cached case base");
            
            List<CBRCase> cases = new ArrayList<CBRCase>(caseBase.getCases());
            
            //For each repetition
            for(int r=0; r<repetitions; r++)
            {	//Create the folds
            	ArrayList<ArrayList<CBRCase>> folds = createFolds(cases, numFolds);
                
                //For each fold
                for(int f=0; f<numFolds; f++)
                {   ArrayList<CBRCase> querySet = new ArrayList<CBRCase>();
                    prepareCases(cases, querySet, f, caseBase, folds);
                
                    //Run cycle for each case in querySet (current fold)
                    for(CBRCase c: querySet)
                    {	LogManager.getLogger().info(
                	    "Running cycle() " + numberOfCycles);
        		app.cycle(c);
                        numberOfCycles++;
                    }          
                } 
            }

            //Revert case base to original state
            caseBase.forgetCases(cases);
            caseBase.learnCases(cases);
            
            //Run the poscycle to finish the application
            LogManager.getLogger().info("Running postcycle()");
            app.postCycle();

            //Complete the evaluation result
            report.setTotalTime(t);
            report.setNumberOfCycles(numberOfCycles);
            
    	} catch (Exception e) 
    	{	LogManager.getLogger().error(e);
	}

    }

    /**
     * Prepares the cases for evaluation by setting up test and training sets	 
     * @param originalCases Complete original set of cases
     * @param querySet Where queries are to be stored
     * @param fold The fold number
     * @param caseBase The case base
     */
    protected void prepareCases(Collection<CBRCase> originalCases, List<CBRCase> querySet, 
	int fold, CBRCaseBase caseBase, ArrayList<ArrayList<CBRCase>> folds)
    {	ArrayList<CBRCase> caseBaseSet = new ArrayList<CBRCase>();
            	
    	//Obtain the query and casebase sets
    	getFolds(fold, querySet, caseBaseSet, folds);
            
    	//Clear the caseBase
    	caseBase.forgetCases(originalCases);
            
    	//Set the cases that acts as casebase in this cycle
    	caseBase.learnCases(caseBaseSet);
    	
	if(this.simConfig != null && this.editMethod != null)
	{	// Perform maintenance on this case base
		editCaseBase(caseBase);
	}
    }

    /**
     * Divides the given cases into the given number of folds.
     * @param cases the original cases.
     * @param numFolds the number of folds.
     */
    protected ArrayList<ArrayList<CBRCase>> createFolds(Collection<CBRCase> cases, int numFolds)
    {   ArrayList<ArrayList<CBRCase>> folds = new ArrayList<ArrayList<CBRCase>>();
        int foldsize = cases.size() / numFolds;
        ArrayList<CBRCase> copy = new ArrayList<CBRCase>(cases);
        
        for(int f=0; f<numFolds; f++)
        {   ArrayList<CBRCase> fold = new ArrayList<CBRCase>();
            for(int i=0; (i<foldsize)&&(copy.size()>0); i++)
            {   int random = (int) (Math.random() * copy.size());
                CBRCase _case = copy.get( random );
                copy.remove(random);
                fold.add(_case);
            }
            folds.add(fold);
        }
        return folds;
    }

    /**
     * Clears the current query and case base sets and populates the query set with fold
     * f and the case base set with the cases not contained in fold f.
     * @param f the fold to use.
     * @param querySet the set of queries.
     * @param caseBaseSet the set of cases.
     */
    public static void getFolds(int f, List<CBRCase> querySet, List<CBRCase> caseBaseSet, ArrayList<ArrayList<CBRCase>> folds)
    {   querySet.clear();
        caseBaseSet.clear();
        
        querySet.addAll(folds.get(f));
        
        for(int i=0; i<folds.size(); i++)
            if(i!=f)
                caseBaseSet.addAll(folds.get(i));
    }
}
package ucm.gaia.jcolibri.evaluation.evaluators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import ucm.gaia.jcolibri.casebase.CachedLinealCaseBase;
import ucm.gaia.jcolibri.cbraplications.StandardCBRApplication;
import ucm.gaia.jcolibri.cbrcore.CBRCase;
import ucm.gaia.jcolibri.cbrcore.CBRCaseBase;
import ucm.gaia.jcolibri.evaluation.EvaluationReport;
import ucm.gaia.jcolibri.evaluation.Evaluator;
import ucm.gaia.jcolibri.exception.ExecutionException;
import ucm.gaia.jcolibri.util.FileIO;
import ucm.gaia.jcolibri.util.ProgressController;

/**
 * This method splits the case base in two sets: one used for testing where each
 * case is used as query, and another that acts as normal case base.
 * <br>
 * This method is different of the other evaluators beacuse the split is stored in a file that can be used in following evaluations.
 * This way, the same set is used as queries for each evaluation.
 * <br>
 * The generateSplit() method does the initial random split and saves the query set in a file. 
 * Later, the  HoldOutfromFile() method uses that file to load the queries set and perform the evaluation.
 * 
 * @author Juan A. Recio García & Lisa Cummins
 * @version 2.0
 */

public class SameSplitEvaluator extends Evaluator {

	protected StandardCBRApplication app;

	public void init(StandardCBRApplication cbrApp) {

		report = new EvaluationReport();
		app = cbrApp;
		try {
			app.configure();
		} catch (ExecutionException e) {
			LogManager.getLogger().error(e);
		}
	}

	public void generateSplit(int testPercent,  String filename)
	{
		//Run the precycle to load the case base
		LogManager.getLogger().info("Running precycle()");
		CBRCaseBase caseBase = null;
		try {
			caseBase = app.preCycle();
		} catch (ExecutionException e) {
			LogManager.getLogger().error(e);
		}

		if (!(caseBase instanceof CachedLinealCaseBase))
			LogManager.getLogger().warn(
							"Evaluation should be executed using a cached case base");

		ArrayList<CBRCase> originalCases = new ArrayList<CBRCase>(
				caseBase.getCases());
				
		ArrayList<CBRCase> querySet = new ArrayList<CBRCase>();
		ArrayList<CBRCase> caseBaseSet = new ArrayList<CBRCase>();
			
		// Split the case base
		splitCaseBase(originalCases, querySet, caseBaseSet, testPercent);
		
		save(querySet, filename);
	}
	
	protected void save(Collection<CBRCase> queries, String filename)
	{
		try {
			BufferedWriter br = null;
			br = new BufferedWriter(new FileWriter(filename));
			if (br == null)
				throw new Exception("Error opening file for writing: "+ filename);
			
			for (CBRCase _case : queries) {
				br.write(_case.getID().toString());
				br.newLine();
			}
			br.close();
		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
			

	}
	
	public void HoldOutfromFile(String file) {
		try {
			// Obtain the time
			long t = (new Date()).getTime();
			int numberOfCycles = 0;

			// Run the precycle to load the case base
			LogManager.getLogger().info("Running precycle()");
			CBRCaseBase caseBase = app.preCycle();

			if (!(caseBase instanceof CachedLinealCaseBase))
				LogManager.getLogger().warn(
								"Evaluation should be executed using a cached case base");

			ArrayList<CBRCase> originalCases = new ArrayList<CBRCase>(caseBase.getCases());
			ArrayList<CBRCase> querySet = new ArrayList<CBRCase>();
			ArrayList<CBRCase> caseBaseSet = new ArrayList<CBRCase>();
			
			// Split the case base
			splitCaseBaseFromFile(originalCases, querySet, caseBaseSet, file);
			
			int totalSteps = querySet.size();
			ProgressController.init(getClass(),
					"Same Split - Hold Out Evaluation", totalSteps);


			// Clear the caseBase
			caseBase.forgetCases(originalCases);

			// Set the cases that acts as case base in this repetition
			caseBase.learnCases(caseBaseSet);

			// Run cycle for each case in querySet
			for (CBRCase c : querySet) {
				// Run the cycle
				LogManager.getLogger().info(
						"Running cycle() " + numberOfCycles);
				app.cycle(c);

				ProgressController.step(getClass());
				numberOfCycles++;
			}

			ProgressController.finish(getClass());

			// Revert case base to original state
			caseBase.forgetCases(originalCases);
			caseBase.learnCases(originalCases);

			// Run the poscycle to finish the application
			LogManager.getLogger().info("Running postcycle()");
			app.postCycle();

			t = (new Date()).getTime() - t;

			// Obtain and complete the evaluation result
			report.setTotalTime(t);
			report.setNumberOfCycles(numberOfCycles);

		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}

	}

	/**
	 * Splits the case base in two sets: queries and case base
	 * 
	 * @param wholeCaseBase
	 *            Complete original case base
	 * @param querySet
	 *            Output param where queries are stored
	 * @param casebaseSet
	 *            Output param where case base is stored
	 * @param testPercent
	 *            Percentage of cases used as queries
	 */
	protected void splitCaseBase(Collection<CBRCase> wholeCaseBase,
			List<CBRCase> querySet, List<CBRCase> casebaseSet, int testPercent) {
		querySet.clear();
		casebaseSet.clear();

		int querySetSize = (wholeCaseBase.size() * testPercent) / 100;
		casebaseSet.addAll(wholeCaseBase);

		for (int i = 0; i < querySetSize; i++) {
			int random = (int) (Math.random() * casebaseSet.size());
			CBRCase _case = casebaseSet.get(random);
			casebaseSet.remove(random);
			querySet.add(_case);
		}
	}
	
	protected void splitCaseBaseFromFile(Collection<CBRCase> wholeCaseBase,
			List<CBRCase> querySet, List<CBRCase> casebaseSet, String filename)
	{
		querySet.clear();
		casebaseSet.clear();
		
		casebaseSet.addAll(wholeCaseBase);
		
		try {
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(FileIO.findFile(filename).getFile()));
			if (br == null)
				throw new Exception("Error opening file: " + filename);

			String line = "";
			while ((line = br.readLine()) != null) {
				CBRCase c = null;
				int pos=0;
				boolean found = false;
				for(Iterator<CBRCase> iter = casebaseSet.iterator(); iter.hasNext() && (!found); )
				{
					c = iter.next();
					if(c.getID().toString().equals(line))
						found = true;
					else
						pos++;
				}
				if(c==null)
				{
					LogManager.getLogger().error("Case "+line+" not found into case base");
					continue;
				}
				
				casebaseSet.remove(pos);
				querySet.add(c);
			}
			br.close();
		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
	}

}
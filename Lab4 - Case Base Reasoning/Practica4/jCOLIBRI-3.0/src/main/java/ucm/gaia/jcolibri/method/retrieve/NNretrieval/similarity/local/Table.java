package ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.logging.LogFactory;
import ucm.gaia.jcolibri.exception.NoApplicableSimilarityFunctionException;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;
import ucm.gaia.jcolibri.util.FileIO;


/**
 * Similarity function that uses a table to obtain the similarity between two values. 
 * Allowed values are Strings or Enums. The table is read from a text file with the following format:
 * <ul>
 * <li>1st line: coma separated n categories
 * <li>following n lines: n double values separated by comma. 
 * </ul> 
 */
public class Table implements LocalSimilarityFunction {

    double matrix[][] = null;

    ArrayList<String> categories = new ArrayList<String>();

    public Table(String filename)
    {
	try
	{
	    InputStream is = FileIO.openFile(filename);
	    BufferedReader br = null;
	    br = new BufferedReader(new InputStreamReader(is));
	    if (br == null)
	    {
		org.apache.commons.logging.LogFactory.getLog(this.getClass()).error("Cannot find file: " + filename);
		return;
	    }
	    String line = br.readLine();
	    StringTokenizer st = new StringTokenizer(line, ",");
	    while (st.hasMoreTokens())
		categories.add(st.nextToken());
	    int size = categories.size();

	    matrix = new double[size][size];

	    int i = 0;
	    while ((line = br.readLine()) != null)
	    {
		st = new StringTokenizer(line, ",");
		int j = 0;
		while (st.hasMoreTokens())
		    matrix[i][j++] = Double.parseDouble(st.nextToken());
		i++;
	    }

	} catch (Exception e)
	{
	    org.apache.commons.logging.LogFactory.getLog(this.getClass()).error(e);

	}
    }

    /**
     * Applies the similarity function.
     * 
     * @param caseObject is a String or Enum
     * @param queryObject is a String or Enum
     * @return result of apply the similarity function.
     */
    public double compute(Object caseObject, Object queryObject) throws NoApplicableSimilarityFunctionException
    {
	if ((caseObject == null) || (queryObject == null))
		return 0;
	if (! ((caseObject instanceof String)||(caseObject instanceof Enum)))
		throw new NoApplicableSimilarityFunctionException(this.getClass(), caseObject.getClass());
	if (! ((queryObject instanceof String)||(queryObject instanceof Enum)))
		throw new NoApplicableSimilarityFunctionException(this.getClass(), queryObject.getClass());

	String caseS;
	String queryS;
	if(caseObject instanceof String)
	{    
	    caseS = (String) caseObject;
	    queryS = (String) queryObject;
	}else
	{
	    caseS = ((Enum)caseObject).toString();
	    queryS = ((Enum)queryObject).toString();
	}
	if (matrix == null)
	{
	    LogFactory.getLog(this.getClass()).error("Similarity table empty");
	    return 0;
	}

	int pos1 = categories.indexOf(caseS);
	if (pos1 == -1)
	{
	    LogFactory.getLog(this.getClass()).error(caseS + " not found in table");
	    return 0;
	}
	int pos2 = categories.indexOf(queryS);
	if (pos2 == -1)
	{
	    LogFactory.getLog(this.getClass()).error(queryS + " not found in table");
	    return 0;
	}
	return matrix[pos1][pos2];

    }

    /** Applicable to String or Enum */
    public boolean isApplicable(Object o1, Object o2)
    {
	if((o1==null)&&(o2==null))
		return true;
	else if(o1==null)
		return (o2 instanceof String)||(o2 instanceof Enum);
	else if(o2==null)
		return (o1 instanceof String)||(o1 instanceof Enum);
	else
		return ((o1 instanceof String)&&(o2 instanceof String)) ||
			((o1 instanceof Enum)&&(o2 instanceof Enum));
    }
}
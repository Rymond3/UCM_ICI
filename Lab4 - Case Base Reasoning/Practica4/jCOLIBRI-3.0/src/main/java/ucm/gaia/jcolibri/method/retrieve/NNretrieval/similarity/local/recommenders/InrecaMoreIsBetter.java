package ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.recommenders;


import ucm.gaia.jcolibri.exception.NoApplicableSimilarityFunctionException;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 * This function returns the similarity of two numbers (or Enums) following 
 * the INRECA - More is Better formulae
 * 
 * sim(c.a,q.a)= if(c.a > q.a) then 1 else  jump * (1- (q.a - c.a) / q.a))
 * 
 * jump must be defined by the designer.
 */
public class InrecaMoreIsBetter implements LocalSimilarityFunction {

	double jump;

	/**
	 * Constructor.
	 */
	public InrecaMoreIsBetter(double jumpSimilarity) {
	    this.jump = jumpSimilarity;
	}

	/**
	 * Applies the similarity function.
	 * 
	 * @param caseObject is a Number
	 * @param queryObject is a Number
	 * @return result of apply the similarity function.
	 */
	public double compute(Object caseObject, Object queryObject) throws NoApplicableSimilarityFunctionException {
		if ((caseObject == null) || (queryObject == null))
			return 0;
		if (! ((caseObject instanceof Number)||(caseObject instanceof Enum)))
			throw new NoApplicableSimilarityFunctionException(this.getClass(), caseObject.getClass());
		if (! ((queryObject instanceof Number)||(queryObject instanceof Enum)))
			throw new NoApplicableSimilarityFunctionException(this.getClass(), queryObject.getClass());

		double caseValue;
		double queryValue;

		if(caseObject instanceof Number)
		{
		    Number n1  = (Number) caseObject;
		    Number n2  = (Number) queryObject;
		    caseValue  = n1.doubleValue();
		    queryValue = n2.doubleValue();
		}
		else
		{
		    Enum enum1 = (Enum)caseObject;
		    Enum enum2 = (Enum)queryObject;
		    caseValue  = enum1.ordinal();
		    queryValue = enum2.ordinal();
		}
		
		if(caseValue >= queryValue)
		    return 1;
		
		else return jump * (1- ((queryValue - caseValue) / queryValue) );
		
	}
	
	/** Applicable to any Number subinstance */
	public boolean isApplicable(Object o1, Object o2)
	{
		if((o1==null)&&(o2==null))
			return true;
		else if(o1==null)
			return (o2 instanceof Number)||(o2 instanceof Enum);
		else if(o2==null)
			return (o1 instanceof Number)||(o1 instanceof Enum);
		else
			return ((o1 instanceof Number)&&(o2 instanceof Number)) ||
				((o1 instanceof Enum)&&(o2 instanceof Enum));
	}

}

/**
 * CopyUtils.java
 * jCOLIBRI2 framework. 
 * @author Juan A. Recio-Garc�a.
 * GAIA - Group for Artificial Intelligence Applications
 * http://gaia.fdi.ucm.es
 * 05/01/2007
 */
package ucm.gaia.jcolibri.util;

import org.apache.logging.log4j.LogManager;
import ucm.gaia.jcolibri.cbrcore.Attribute;
import ucm.gaia.jcolibri.cbrcore.CBRCase;
import ucm.gaia.jcolibri.cbrcore.CaseComponent;

/**
 * Utility functions to copy cases or case components.
 * @author Juan A. Recio-Garcia
 */
public class CopyUtils {

	/**
	 * Returns a deep copy of a CBRCase.
	 */
	public static CBRCase copyCBRCase(CBRCase c) {

		try {
			Class _class = c.getClass();
			CBRCase copy = (CBRCase)_class.newInstance();
			
			copy.setDescription(copyCaseComponent(c.getDescription()));
			copy.setSolution(copyCaseComponent(c.getSolution()));
			copy.setJustificationOfSolution(copyCaseComponent(c.getJustificationOfSolution()));
			copy.setResult(copyCaseComponent(c.getResult()));
			
			return copy;
		} catch (Exception e) {
			LogManager.getLogger(CopyUtils.class).error("Error copying case "+c);
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * Returns a deep copy of a CaseComponent
	 */
	public static CaseComponent copyCaseComponent(CaseComponent c) {
		try {
			if(c==null)
				return null;
			Class _class = c.getClass();
			CaseComponent copy = (CaseComponent)_class.newInstance();
			Attribute[] attrs = AttributeUtils.getAttributes(_class);
			
			for(int i=0; i<attrs.length; i++)
			{
				Attribute at = attrs[i];
				Object value = at.getValue(c);
				if(value == null)
					continue;
				else if(value instanceof CaseComponent)
					at.setValue(copy, copyCaseComponent((CaseComponent)value));
				else
					at.setValue(copy, value);
			}
			
			return copy;
		} catch (Exception e) {
			LogManager.getLogger(CopyUtils.class).error("Error copying case component "+c);
			e.printStackTrace();
		} 
		return null;	
	}
}

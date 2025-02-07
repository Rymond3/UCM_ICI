/**
 * ObtainQueryFromProfile.java
 * jCOLIBRI2 framework. 
 * @author Juan A. Recio-Garc�a.
 * GAIA - Group for Artificial Intelligence Applications
 * http://gaia.fdi.ucm.es
 * 04/11/2007
 */
package ucm.gaia.jcolibri.extensions.recommendation.ContentBasedProfile;


import ucm.gaia.jcolibri.cbrcore.CBRQuery;
import ucm.gaia.jcolibri.connector.xmlutils.QuerySerializer;

/**
 * Obtains an user profile (query object) from a XML file.
 * 
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with Derek Bridge.
 * @version 1.0
 * @see jcolibri.connector.xmlutils.QuerySerializer
 */
public class ObtainQueryFromProfile
{
    /**
     * Obtains a query from a XML file
     * @param filename of the XML file
     * @return the query
     */
    public static CBRQuery obtainQueryFromProfile(String filename)
    {
	return QuerySerializer.deserializeQuery(filename);
    }
}

package org.barracudamvc.core.comp;

import org.barracudamvc.core.forms.FormMap;

/**
 * An interface to provide extended feature to a component such as : 
 * -set himself an error (Called by ErrorManager when applying errors)
 * -repopulate himself from a formMap (Called by FormUtil when repopulating)
 * 
 * @author Franck Routier (alci@mecadu.org)
 *
 */
public interface ExtendedComponent {
	
    /**
     * Called by ErrorManager when applying errors
     * @param error either a String or a List<String> describing the error
     */
	public void setError(Object error);
	
	/**
	 * Called by FormUtil when repopulating form elements
	 * @param fm the formMap which contains values
	 * @param key 
	 */
	public void repopulate(FormMap fm, String key);
	
	/**
	 * Ask the component to set or unset a mandatory display 
	 * @param bool
	 */
	public void setMandatory(boolean bool);
	
	/**
	 * Ask the component to set or unset a protected display 
	 * @param bool
	 */
	public void setProtected(boolean bool);
	
	// fro_100108_1_begin 
	/**
	 * Ask the component the main component it contains
	 * This allow to access the real component inside a 
	 * composed component.
	 */
	public BComponent getMainComponent();
	// fro_100108_1_end
	
	// fro_121709_1_begin 
	/**
	 * Give the component a chance to display a desciption (tooltip, text description, etc...)
	 */
	public BComponent setDescription(String desc);
	// 	fro_121709_1_end
}

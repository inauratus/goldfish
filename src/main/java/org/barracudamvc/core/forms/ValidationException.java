/*
 * Copyright (C) 2003  Christian Cryder [christianc@granitepeaks.com]
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: ValidationException.java 237 2010-12-09 12:23:38Z alci $
 */
package org.barracudamvc.core.forms;

import org.barracudamvc.plankton.exceptions.NestableException;

/**
 * This class defines the validation exception. Code that catches
 * these exceptions should check for subExceptions, as this class
 * can be used to group collections of ValidationExceptions.
 */
public class ValidationException extends NestableException {

	private static final long serialVersionUID = 1L;
	//protected List subExceptions = null;
    protected Object source = null;
    protected static final String sep = System.getProperty("line.separator");
    
    /**
     * The noargs public contructor for ValidationException
     */
    public ValidationException () {
        this("Generic Validation Exception");
    }

    /**
     * The public contructor for ValidationException
     *
     * @param s a String describing the exception
     */
    public ValidationException (String s) {
        this(null, s);
    }

    /**
     * The public contructor for ValidationException
     *
     * @param source the object which caused this error (usually a
     *        FormElement)
     */
    public ValidationException (Object source) {
        this(source, null);
    }

    /**
     * The public contructor for ValidationException
     *
     * @param source the object which caused this error (usually a
     *        FormElement)
     * @param s a String describing the exception
     */
    public ValidationException (Object source, String s) {
        this(source, s, null);
    }

    /**
     * The public contructor for ValidationException
     *
     * @param isource the object which caused this error (usually a
     *        FormElement)
     * @param s a String describing the exception
     * @param ibaseException the original exception to wrap within this exception
     */
    public ValidationException (Object isource, String s, Exception ibaseException) {
        super(s, ibaseException);
        source = isource;
    }

    /**
     * Get the form element which caused this error
     *
     * @return the form element which caused this error
     */
    public Object getSource() {
        return source;
    }
    
//    /**
//     * See if this particular ValidationException has
//     * sub-exceptions
//     *
//     * @return true if this particular ValidationException has
//     *         sub-exceptions
//     */
//    public boolean hasSubExceptions() {
//        return (subExceptions!=null && subExceptions.size()>0);
//    }
    
//    /**
//     * Add a sub exception
//     *
//     * @param ve a sub-exception to be added
//     */
//    public void addSubException(ValidationException ve) {
//        if (subExceptions==null) subExceptions = new ArrayList();
//        subExceptions.add(ve);
//    }

    /**
     * Get a copy of the list of sub-exceptions. May be null
     * if there are no sub-exceptions.
     *
     * @return a copy of the list of sub-exceptions
     */
//    public List getSubExceptions() {
//        return (subExceptions==null ? null : new ArrayList(subExceptions));
//    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append(this.getClass().getName()+"@"+Integer.toHexString(this.hashCode()));
        sb.append(", Msg:"+getMessage());
        if (source!=null && source instanceof ValidationException) sb.append(", Src: ValidationException[@"+Integer.toHexString(source.hashCode())+"]");
        else sb.append(", Src:"+source);
//        if (subExceptions!=null) {
//            sb.append(sep+"Sub-exceptions:");
//            Iterator it = subExceptions.iterator();
//            while (it.hasNext()) {
//                sb.append(sep+"    "+it.next());
//            }
//        }
        return sb.toString();
    }

//    /**
//     * Collapse all sub-exceptions into a single list of
//     * Validation exceptions
//     */
//    public List getExceptionList() {
//        List list = new ArrayList();
//        list.addAll(getExceptionList(this));
//        return list;
//    }
//    
//    protected List getExceptionList(ValidationException ve) {
//        List list = new ArrayList();
//        if (!ve.hasSubExceptions()) {
//            list.add(ve);
//        } else {
//            Iterator it = ve.getSubExceptions().iterator();
//            while (it.hasNext()) {
//                list.addAll(getExceptionList((ValidationException) it.next()));
//            }
//        }
//        return list;
//    }

}

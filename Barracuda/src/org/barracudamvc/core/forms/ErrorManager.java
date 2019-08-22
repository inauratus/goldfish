/*
 * Copyright (C) 2004 ATM Express, Inc [christianc@atmreports.com]
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
 * Christian Cryder, Diez B. Roggisch
 *
 * $Id: ErrorManager.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.forms;

// java imports:
import java.io.Serializable;
import java.util.*;

// 3rd-party imports:
import org.apache.log4j.*;
import org.barracudamvc.core.comp.*;
import org.barracudamvc.plankton.data.*;

/**
 * This class is used to collate all the nested exceptions on a form into something that can 
 * be easily accessed. To use it, you typically call ErrorManager.handleError(ValidationException).
 * You can also manually add errors. You can them use the ErrorManager to flag any components
 * to which it relates (in other words, by calling ErrorHandler.apply(bcomp), the error manager
 * will automatically use the component name to look up any validation exceptions which correspond
 * to a form element of the same name, and if a match occurs, will automatically adjust the component
 * by setting its css class attribute to "invalid" and setting the title attribute with the error
 * description. This makes it possible to visibly flag components which have errors - when the user 
 * mouses over they see the err).
 *
 * The way you use this class is to call
 *
 *     try {
 *         form.map();
 *         form.validate();
 *     catch (ValdationException ve) {
 *         ErrorManager.handleError(ve);
 *     }
 *
 * Now the error manager is actually populated and ready to use. For convenience sake, a copy is 
 * stored in the local object repositry, and can be accessed later simply by calling
 *
 *     ErrorManager eman = ErrorManager.getInstance();
 *
 * Note that now the ErrorManager is integrated with DefaultFormMap - when you call FormMap.validate() 
 * it automatically set up the error manager if there are errors so that you don't need to manually call
 * ErrorManager.handleError(). If you DO manually call this method, however, it shouldn't hurt anything.
 *
 * @author christianc@atmreports.com
 * @since csc_110304_1
 */
public class ErrorManager implements Serializable {

    private static final long serialVersionUID = 1;

    private static final Class CLASS = ErrorManager.class;
    protected static Logger logger = Logger.getLogger(CLASS.getName());

    //public constants
    public static final String INVALID_CSS_CLASS = "invalid";   //CSS class to apply to invalid fields

    //local repository vars
    public static final String ERROR_MANAGER = CLASS + ".ErrorManager";          //(ErrorManager)

    //local vars
    protected List<String> errors = null;          //non field-related error messages (String)
    protected Map<String, List<String>> fieldErrors = null;      //field-related error messages (String/List), keyed by field name
    protected Map<Object, Object> handledExceptions = new IdentityHashMap<Object, Object>();

    protected HashMap<String, List<ValidationException>> fieldExceptions = new HashMap<String, List<ValidationException>>();

    /**
     * Create an empty error manager
     */
    public ErrorManager() {
        this(null);
    }

    /**
     * Create a new error manager for a specific exception. Most of the time you will use the
     * handleError factory method instead of instantiating the form directly. The error
     * passed to the constructor will automatically be added to the handler.
     *
     * @param error the actual root error this form is being associated
     */
    public ErrorManager(Exception error) {
        this.errors = new ArrayList<String>();
        this.fieldErrors = new TreeMap<String, List<String>>();
        addError(error);
    }

    /**
     * Add an error to the handler (this method recursively adds any errors
     * which may be nested within this error as well, ie. in the case of
     * ValidationExceptions.
     *
     * @param error the actual error being associated
     */
    public void addError(Object error) {
        addError(null, error);
    }

    /**
     * Add an error to the handler (this method recursively adds any errors
     * which may be nested within this error as well, ie. in the case of
     * ValidationExceptions.
     *
     * @param key the name of the object to which the error is associated
     * (usually the key name of the form element or the component name)
     * A null value indicates its a general error, not associated with
     * any particular field.
     * @param error the actual error being associated
     */
    public void addError(String key, Object error) {
        if (error == null) {
            return;
        }

        // fro_2011-11-03_1 - handle the case when the exception has already been processed.
        if (handledExceptions.containsKey(error)) {
            // the purpose of this is so that if someone inadvertantly calls 
            // handleError() mutiple times for the same exception, the error 
            // will only be processed once

            return;
        }

        handledExceptions.put(error, error);

        if (error instanceof DeferredValidationException) {
            for (ValidationException exception : (DeferredValidationException) error) {
                addError(key, exception);
            }
            return;
        }

        if (error instanceof ValidationException) {
            ValidationException ve = (ValidationException) error;

            Object source = ve.getSource();

            if (source == null) {
                _addError(null, ve);
                return;
            }

            if (source instanceof FormElement) {
                FormElement fe = (FormElement) source;
                String fkey = fe.getKey();
                if (fkey != null) {
                    // by default, we're going to map the error using the 
                    // form element name
                    key = fkey;

                    // if however, the form element mapper is an instance 
                    // of PrefixFormMapper, we're also going to pre-pend 
                    // on the prefix, making the name fully qualified
                    FormMap fm = fe.getParentForm();
                    FormMapper fmapper = fm.getFormMapper();
                    if (fmapper instanceof PrefixFormMapper) {
                        String prefix = ((PrefixFormMapper) fmapper).getPrefix();
                        if (prefix != null && !key.startsWith(prefix)) {
                            key = prefix + key;
                        }
                    }
                }
                _addError(key, ve);
            } else if (source instanceof String) {
                _addError((String) source, ve);
            } else if (source instanceof List) {
                for (Object src : (List) source) {
                    if (src instanceof String) {
                        _addError((String) src, ve);
                    } else if (src instanceof FormElement) {
                        FormElement fe = (FormElement) src;
                        String fkey = fe.getKey();
                        if (fkey != null) {
                            //by default, we're going to map the error using the form element name
                            //key = fkey;

                            //if however, the form element mapper is an instance of PrefixFormMapper, we're also going to pre-pend on the prefix, making the name fully qualified
                            FormMap fm = fe.getParentForm();
                            FormMapper fmapper = fm.getFormMapper();
                            if (fmapper instanceof PrefixFormMapper) {
                                String prefix = ((PrefixFormMapper) fmapper).getPrefix();
                                if (prefix != null && !fkey.startsWith(prefix)) {
                                    fkey = prefix + fkey;
                                }
                            }
                        }
                        _addError(fkey, ve);
                    }
                }
            } else {
                _addError(source.toString(), ve);
            }
        } else {
            _addError(key, error);
        }
    }

    private void addFieldException(String key, Object error) {
        if (!(error instanceof ValidationException)) {
            return;
        }
        if (key == null) {
            return;
        }

        ValidationException exception = (ValidationException) error;

        if (exception.getSource() instanceof FormElement) {
            List<ValidationException> exceptions = fieldExceptions.get(key);
            if (exceptions == null) {
                exceptions = new LinkedList<ValidationException>();
                fieldExceptions.put(key, exceptions);
            }
            exceptions.add(exception);
        }
    }

    protected void _addError(String key, Object error) {
        String message;
        if (error instanceof Throwable) {
            message = ((Throwable) error).getMessage();
            addFieldException(key, error);
        } else if (error instanceof String) {
            message = (String) error;
        } else if (error != null) {
            message = error.toString();
        } else {
            message = "";
        }
        if (message == null || message.isEmpty()) {
            return;
        }

        if (key == null) {
            errors.add(message);
        } else {
            List<String> list = fieldErrors.get(key);
            if (list == null) {
                list = new ArrayList<String>();
                fieldErrors.put(key, list);
            }
            list.add(message);
        }
    }

    /**
     * Returns true if there are non-field-related errors
     */
    public boolean hasErrors() {
        return (!errors.isEmpty());
    }

    /**
     * Get a List of errors associated with this error manager
     */
    public List getErrors() {
        return errors;
    }

    /**
     * Returns true if there are field-related errors
     */
    public boolean hasFieldErrors() {
        return (!fieldErrors.isEmpty());
    }

    /**
     * Returns true if there are field-related errors
     */
    public boolean hasFieldError(String key) {
        return (fieldErrors.containsKey(key));
    }

    /**
     * Get any errors associated with particular fields
     *
     * @param key the key name of the field
     * @return either the actual error (String) or a List of
     *      such errors associated witht he field
     */
    public Object getFieldError(String key) {
        return fieldErrors.get(key);
    }

    /**
     * Get a Map of all errors associated with particular fields
     */
    public Map getFieldErrors() {
        return fieldErrors;
    }

    /**
     * This method applies any errors which are associated with this component. The component
     * will have its class attribute adjusted (so that the error is visibly obvious). The title
     * attribute will also be adjusted, so that a description of the error shows on mouse over.
     */
    public void apply(BComponent component) {
        String name = component.getName();
        if (name == null) {
            return;
        }
        Object error = fieldErrors.get(name);
        if (error != null) {
            // fro_070907_2_start
            if (component instanceof ExtendedComponent) {// XXX ErrorAwareComponent => ExtendedComponent
                ((ExtendedComponent) component).setError(error);
            } else {
                // fro_070907_2_end
                component.addClass(INVALID_CSS_CLASS);
                String descr;
                if (error instanceof List) {
                    Iterator it = ((List) error).iterator();
                    StringBuffer sb = new StringBuffer();
                    String sep = "";
                    while (it.hasNext()) {
                        sb.append(sep).append((String) it.next());
                        sep = "&0x13;";
                    }
                    descr = sb.toString();
                } else {
                    descr = (String) error;
                }
                component.setAttr("title", descr);
                // fro_070907_2_start
            }
            // fro_070907_2_end
        }
    }

    //--------------------------------------------------------------------------
    //      Utilities
    //--------------------------------------------------------------------------
    /**
     * Convenience method to initialize an ErrorManager from an error.
     * You can access this error manager again/later simply by calling
     * ErrorManager.getInstance(). It will be available until the request
     * completes or until you call ErrorManager.reset().
     *
     * @param   error   Error to pass to the ErrorManager
     */
    public static ErrorManager handleError(Exception error) {
        return handleError(null, error);
    }

    public static ErrorManager handleError(String key, Exception error) {
        // fro_2011-11-03_1 - move this to addError, to handle the case when 
        // DeferredException contains subexceptions that ahve already been
        // processed.
        ErrorManager em = getInstance();
        em.addError(key, error);
        return em;
    }

    /**
     * Convenience method to get the current ErrorManager (if there are any
     * errors).
     * Returns null if all is well.
     *
     * @return the current error manager (if exists)
     */
    public static ErrorManager getInstance() {
        ObjectRepository lor = ObjectRepository.getLocalRepository();
        Object possibleErrorManager = lor.getState(ERROR_MANAGER);
        if (possibleErrorManager instanceof ErrorManager) {
            return (ErrorManager) possibleErrorManager;
        } else if (possibleErrorManager == null) {
            ErrorManager errorManager = new ErrorManager();
            lor.putState(ERROR_MANAGER, errorManager);
            return errorManager;
        } else {
            throw new IllegalStateException(
                    "The Local Object Repository contained a Object of type ["
                    + possibleErrorManager.getClass().getName() + "] in slot "
                    + ERROR_MANAGER + "; the " + ErrorManager.class.getName()
                    + " requires this field.");
        }
    }

    /**
     * Resets the error manager to a clear state (no errs)
     */
    public static void reset() {
        ObjectRepository lor = ObjectRepository.getLocalRepository();
        lor.removeState(ERROR_MANAGER);
    }

    public List<ValidationException> getFieldExceptions(String field) {
        return fieldExceptions.get(field);
    }
}

/*
 * $Log: ErrorManager.java,v $
 */

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
 * $Id: FormControl.js 114 2005-12-09 15:51:51Z christianc $
 */

var calledOnSubmit = false; //csc_033105_1

//--------------------- Public functions ----------------------------
/**
 * takes a form object as the first argument and then takes an infinite amount
 * of additional arguments which are expected to be functions.  This allows for
 * the possibility of executing multiple functions (in the order provided)
 * before actual submittal of the form.  Actually, any one of the named
 * functions may submit the form themselves.  However, $bmvc_doSubmit() always
 * provides a backstop objForm.submit() in case no extra function is provided
 * or the functions that are provided don't submit the form themselves.
 *
 * @see bmvc_doSubmitAndLock(objForm)
 */
function bmvc_doSubmitAndNoLock(objForm) {
    return $bmvc_doSubmit(arguments);
}

/**
 * same as doSubmitAndNoLock, only this locks all form controls of type
 * 'submit', 'button', and 'image'.  Other types of controls can't be
 * locked (disabled) because they are data-rich and modern browsers won't send
 * data of disabled controls to the server.
 *
 * @see bmvc_doSubmitAndNoLock(objForm)
 */
function bmvc_doSubmitAndLock(objForm) {
    for (i = 0; i < objForm.elements.length; i++) {
        if (objForm.elements[i].type == 'submit' || objForm.elements[i].type == 'button' || objForm.elements[i].type == 'image') {
            objForm.elements[i].disabled = true;
        }
    }
    return $bmvc_doSubmit(arguments);
}


//--------------------- Utility functions ---------------------------
/**
 * Takes an arguments object.  Treats the first argument as the form object
 * and the rest as function to be called, with the form object as the only
 * argument.
 *
 * csc_032905_1 - reworked after consultation w/ Jake (see FormControl.js ver 21 for previous)
 */
function $bmvc_doSubmit(args) {
    if ($bmvc_properArgs(arguments) && args[0].submit) {
        //args is an arguments object and the first argument of that
        //arguments object is a form object.


//csc_033105_1_start - this will now be handled down below, making it possible to avoid the onsubmit call altogether
/*
        //first and foremost, check to see if we are dealing with a form that has
        //the onsubmit handler defined. If so, we are going to manually invoke the
        //onsubmit handler (giving form validation code a chance to operate). If
        //this handler returns false, form submission will be cancelled. This method
        //will be invoked for ANY form submission, so if you don't want "global"
        //functionality, you should not define the onsubmit handler for the form)
        var contWithSubmit = (args[0].onsubmit) ? args[0].onsubmit() : true;
        if (contWithSubmit == undefined) contWithSubmit = true;
        if (!contWithSubmit) return;
*/
//csc_033105_1_end

        //now we need to loop through other arguments which are expected to be functions,
        //but we'll double check anyway and ignore each argument that isn't a. If any one
        //of these functions returns false, that effectively stops the chain of events (we
        //won't continue with the submit process)
        for (var i=1; i < args.length; i++) {
            if (args[i].call) {
                contWithSubmit = args[i].call(null, args[0]);
                if (contWithSubmit == undefined) contWithSubmit = true; //account for methods with no return value (default = continue);
//csc_033105_1                if (!contWithSubmit) break;
                if (!contWithSubmit) return false;  //csc_033105_1
            }
        }

        //at this point, if we still need to submit the form, do so now
        contWithSubmit = $bmvc_call_onsubmit(args[0]);  //csc_033105_1
        if (!contWithSubmit) return false;              //csc_033105_1

        //if we make it this far, do the default submit
        args[0].submit();

    } else {
        alert('Improper arguments!  Must be a single arguments object with the first argument of said object being a form object');
    }
    return false;
}


//csc_033105_1
/**
 * calls a form's onsubmit function if it has it
 */
function $bmvc_call_onsubmit(form) {
    if (calledOnSubmit) return true;
    var result = (form.onsubmit) ? form.onsubmit() : true;
    if (result == undefined) result = true;
    calledOnSubmit = true;
    return result;
}

//csc_032905_1 - added (bmvc may utilize this)
/**
 * returns false (indicating the form has been submitted and there is nothing processing is complete)
 */
function $bmvc_submitted() {
    return false;
}



/**
 * checks, first, that the args argument is an arguments object and then
 * checks that the actual length of the arguments object matches the
 * expected length.
 */
function $bmvc_properArgs(args) {
    if (!args.callee) return false;
    var actual = args.length;
    var expected = args.callee.length;
    if (actual == expected) return true;
    else return false;
}

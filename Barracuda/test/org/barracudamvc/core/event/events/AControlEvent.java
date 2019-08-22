/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: AControlEvent.java 
 * Created: Nov 7, 2013 9:39:41 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.event.events;

import org.barracudamvc.core.event.HttpRequestEvent;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class AControlEvent extends HttpRequestEvent {

    @Override
    public boolean isHandled() {
        return true;
    }
}

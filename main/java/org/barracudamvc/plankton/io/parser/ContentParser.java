/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.plankton.io.parser;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ContentParser {

    public Map<String, List<Object>> parse(InputStream stream);
}

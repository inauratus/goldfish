package org.barracudamvc.core.helper.servlet;

import java.util.List;
import java.util.Map;

public interface HttpRequest {

    Map<String, List<Object>> getContentValues();
}

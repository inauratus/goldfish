package org.barracudamvc.core.http.content;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface ContentParser {

    Map<String, List<Object>> getContent(HttpServletRequest request);
}

package org.barracudamvc.taskdefs;

import java.io.File;
import java.io.IOException;

public interface SSIListener {
    void start(File directory, String name);

    void write(String ssiContents);

    void end() throws IOException;
}

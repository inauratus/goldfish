package org.barracudamvc.taskdefs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SSIListenerFileWriter implements SSIListener {
    private StringBuilder stringBuilder = new StringBuilder();
    private File ssiFile;
    List<File> ssiFiles = new ArrayList<>();
    Map<String, File> touchedDirs = new HashMap<>();

    public Map<String, File> getTouchedDirs() {
        return touchedDirs;
    }

    public List<File> getSsiFiles() {
        return ssiFiles;
    }

    @Override
    public void start(File directory, String name) {
        ssiFile = new File(directory, name + ".ssi");

    }

    @Override
    public void write(String ssiContents) {
        stringBuilder.append(ssiContents);
    }

    public boolean hasOpenFile() {
        return ssiFile != null;
    }

    @Override
    public void end() throws IOException {
        if (ssiFile == null)
            return;
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(ssiFile))) {
            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.flush();
            touchedDirs.put(ssiFile.getParent(), ssiFile.getParentFile());
            ssiFiles.add(ssiFile);
        }
        stringBuilder.setLength(0);
        ssiFile = null;
    }
}

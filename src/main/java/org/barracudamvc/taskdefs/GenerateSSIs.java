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
 * $Id: GenerateSSIs.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.taskdefs;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * The purpose of this taskdef is to parse a source file (or fileset)
 * and create ssi's from it. This is useful for the mockup process, when
 * you want to edit your mockups in a single HTML file and then have
 * pieces of it automatically written out to .ssi files.
 * <p>
 * <p>The tags are valid HTML comments that follow a format that looks
 * like this (where 'foo' can be the name of any ssi file):
 * <p>
 * <p><!-- start foo.ssi -->
 * <br>...
 * <br><!-- end foo.ssi -->
 * <p>
 * <p>When the taskdef sees these tags it will write the contents to the
 * target ssi file IFF the source file is newer than the ssi file or
 * force="true".
 * <p>
 * <p>The taskdef also allows you to specify a touchPattern (defaults to *.shtml);
 * if the taskdef writes ssi files in that directory, it will also update the
 * timestamp on all the files in that directory that match the touch pattern. This
 * is useful for causing XMLC to automatically recompile these files the next time
 * you do a full build.
 *
 * @author <a href="mailto:christianc@granitepeaks.com">Christian Cryder</a>
 */
public class GenerateSSIs extends Task {
    public static final String START_TAG_BEGINNING = "<!-- start ";
    public static final String START_TAG_END = ".ssi -->";
    protected File file = null;
    protected List<FileSet> filesets = new ArrayList();
    protected String touchPattern = "**/*.shtml";
    protected String excludePattern = null;
    protected boolean force = false;
    protected int verbosity = Project.MSG_VERBOSE;
    protected Set<File> touchedFiles = new HashSet<>();
    private File manifestDestination;

    /**
     * Sets a single source file to copy.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Specify a pattern of files to be touched if we end up rewriting
     * any .ssi files (this defaults to *.shtml")
     */
    public void setTouchPattern(String touchPattern) {
        this.touchPattern = touchPattern;
    }

    //csc_081805_1 - added

    /**
     * Specify a pattern of files to be excluded in the touch process
     * (defaults to null)
     */
    public void setExcludePattern(String excludePattern) {
        this.excludePattern = excludePattern;
    }

    /**
     * Force the regeneration of SSIs
     */
    public void setForce(boolean force) {
        this.force = force;
    }

    /**
     * Used to force listing of all names of copied files.
     */
    public void setVerbose(boolean verbose) {
        if (verbose) {
            this.verbosity = Project.MSG_INFO;
        } else {
            this.verbosity = Project.MSG_VERBOSE;
        }
    }

    /**
     * Adds a set of files (nested fileset attribute).
     */
    public void addFileset(FileSet set) {
        filesets.add(set);
    }

    public Set<File> getTouchedFiles() {
        return touchedFiles;
    }

    /**
     * Performs the copy operation.
     */
    public void execute() throws BuildException {
        // make sure we don't have an illegal set of options
        validateAttributes();
        File file = this.file;

        List<File> fileList = new ArrayList<>();
        // deal with the single file
        if (file != null) {
            if (file.exists()) {
                fileList.add(file);
            } else {
                String message = "Could not find file " + file.getAbsolutePath() + " to generate ssi files from.";
                log(message);
                throw new BuildException(message);
            }
            // deal with the filesets
        } else {
            for (int i = 0; i < filesets.size(); i++) {
                FileSet fs = filesets.get(i);
                DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
                
                File baseDir = ds.getBasedir();
                String[] srcFiles = ds.getIncludedFiles();

                for (int j = 0; j < srcFiles.length; j++) {
                    fileList.add(new File(baseDir, srcFiles[j]));
                }
            }
        }

        //now process our file list
        int writeCnt = 0;
        int touchCnt = 0;
        SSIListenerFileWriter ssiListener = new SSIListenerFileWriter();
        for (File f : fileList) {
            generateSSIs(f, ssiListener);
        }

        //now touch all the appropriate files
        String touchPattern = this.touchPattern;
        String excludePattern = this.excludePattern;
        int verbosity = this.verbosity;
        Map<String, File> touchedDirs = ssiListener.getTouchedDirs();

        if (touchPattern != null && touchedDirs.size() > 0) {
            for (File baseDir : touchedDirs.values()) {
                log("Touching " + baseDir + ", touchPattern=\"" + touchPattern + "\", excludePattern=\"" + excludePattern + "\"");
                FileSet fs = new FileSet();
                fs.setDir(baseDir);
                fs.setIncludes(touchPattern);
                if (excludePattern != null) fs.setExcludes(excludePattern);   //csc_081805_1
                DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
                String[] touchFiles = ds.getIncludedFiles();
                long millis = System.currentTimeMillis();
                for (int j = 0; j < touchFiles.length; j++) {
                    File tf = new File(baseDir, touchFiles[j]);
                    log("...Touching " + tf, verbosity);
                    tf.setLastModified(millis);
                    touchCnt++;
                }
            }
        }

        if (writeCnt > 0 || touchCnt > 0) {
            log("Created " + writeCnt + " .ssi files, touched " + touchCnt + " " + touchPattern + " files.");
        }
        if (manifestDestination != null) {
            try (FileWriter fileWriter = new FileWriter(manifestDestination)) {
                for (File ssiFile : touchedFiles) {
                    fileWriter.write(ssiFile.getAbsolutePath());
                    fileWriter.write(System.lineSeparator());
                }
                fileWriter.flush();
            } catch (IOException io) {
                log("ERROR:" + io.getMessage());
            }
        }
    }

    protected void generateSSIs(File f, SSIListener ssiListener) {
        boolean inSSI = false;
        log("Processing file: " + f, verbosity);
        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            String nextLine = null;
            String targetSSI = null;
            while (true) {
                nextLine = in.readLine();
                if (nextLine == null) {
                    if (inSSI)
                        ssiListener.end();
                    break;
                }
                if (!inSSI) {
                    //look for the start flag; if not present continue
                    int spos = nextLine.indexOf(START_TAG_BEGINNING);
                    if (spos < 0) continue;
                    int epos = nextLine.indexOf(START_TAG_END, spos);
                    if (epos < 0) continue;

                    //get the target ssi name
                    targetSSI = nextLine.substring(spos + START_TAG_BEGINNING.length(), epos);

                    File ssi = new File(f.getParent(), targetSSI + ".ssi");
                    touchedFiles.add(ssi);
                    if (force || !ssi.exists() || f.lastModified() > ssi.lastModified()) {
                        ssiListener.start(ssi.getParentFile(), targetSSI);
                        inSSI = true;
                        ssiListener.write(nextLine.substring(epos + 8));
                        ssiListener.write(System.lineSeparator());
                    }

                    //otherwise check for an ending flag and write the output
                } else {
                    //look for the end tag; if not present continue
                    String endFlag = "<!-- end " + targetSSI + ".ssi -->";
                    int spos = nextLine.indexOf(endFlag);
                    if (spos < 0) {
                        ssiListener.write(nextLine);
                        ssiListener.write(System.lineSeparator());
                    } else {
                        inSSI = false;
                        ssiListener.write(nextLine.substring(0, spos));
                        ssiListener.end();
                    }
                }
            }
            in.close();

        } catch (IOException e) {
            log("Unable to read file:" + f, Project.MSG_ERR);
            log("IOException " + e, Project.MSG_ERR);
        }
    }

    /**
     * Ensure we have a consistent and legal set of attributes, and set
     * any internal flags necessary based on different combinations
     * of attributes.
     */
    protected void validateAttributes() throws BuildException {
        if (file == null && filesets.size() == 0) {
            throw new BuildException("Specify at least one source - a file or a fileset.");
        }

        if (file != null && file.exists() && file.isDirectory()) {
            throw new BuildException("Use a fileset to generate SSIs on directories.");
        }
    }

    public void setExportManifest(File destination) throws IOException {
        manifestDestination = destination;
    }
}
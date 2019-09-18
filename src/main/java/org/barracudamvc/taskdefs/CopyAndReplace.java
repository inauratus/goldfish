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
 * $Id: CopyAndReplace.java 243 2011-06-01 12:33:36Z alci $
 */
package org.barracudamvc.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Replace;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.barracudamvc.plankton.io.StreamUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>A consolidated copy and replace task that extends the basic Ant Copy
 * taskdef.
 * <p>
 * <p>In addition, once files have been copied, a replace function is also
 * run on them. This replace will only affect the files that actually
 * get copied; it will not affect any other files in the directory.
 * <p>
 * <p>The replace occurs using a simply properties file, rather than
 * specifying replace parameters via the xml. The format of this file
 * looks something like this:
 * <p>
 * <ul>
 * <li>token="some target text" value="some replacement text"</li>
 * <li>token='some target text called "blah"' value='some replacement text named "blech"'</li>
 * <li>token=~some target text with " and '~ value=^some replacement text with ' and "^</li>
 * </ul>
 * <p>
 * <p>As you can see, the format is flexible. Tokens are identified with "token=" and
 * values are identified by "value=". The actual token/value delimiters are
 * taken to be the first character following the = sign. This could be a double
 * quote, single quote, or any other character (ie. if the text you wish to
 * replace contains both double and single quotes, you might want to use a ~
 * delimiter or something like that).
 * <p>
 * <p>Note also that this taskdef supports SSI replacement, which can be quite useful.
 * By default, the copied files are NOT processed for SSIs; if you want to use this,
 * turn is on using the 'ssi=true' attribute. SSI replacement occurs BEFORE the token
 * replacement (ensuring that the text contained in the SSI gets processed for tokens
 * as well).
 * <p>
 * <p>If SSI replacement is turned on, you can also exclude blocks of code using #exclude_start
 * and #exclude_end tags (as many as you want in the template). This allows you to drop code
 * from the mockups when you copy across. For example:
 * <p>
 * <br>    <!--#exclude_start--><p>blah blah blah<!--#exclude_end-->
 * <br>will result in this after copying:
 * <br>    <!-- -->
 * <p>
 * <p>As a final note, doing SSI replacement on a hierarchy of directories can easily cause
 * broken link references (ie. if your SSI is in the root directory, and it refers to images/foo.gif,
 * then when you suck that SSI into a file several directories deep (eg. /foo/bar/blah.html), then
 * that ssi reference really needs to get changed to ../../images/foo.gif. You can do this by specifying
 * a relative dir constant in your options.mappings file:
 * <p>
 * <br>    token=^"images/^ value=^"@REL_PATH@images/^
 *
 * @author Christian Cryder [christianc@granitepeaks.com]
 * @author Charles H. Lowery (chuck.lowery at gmail.com)
 */
public class CopyAndReplace extends Copy {

    public final static String REL_PATH_TOKEN = "@REL_PATH@";
    protected static final Pattern SSI_INCLUDE_TAG = Pattern.compile("<!--[\\s]*#include[\\s]*file[\\s]*=[\\s]*\"([^\"]*)\"[\\s]*-->");
    public static final String HTML_ERROR_MESSAGE = "<!-- An error occurred while attempting to construct this document: see build log -->";
    public static final String CIRCULAR_INCLUDE_MESSAGE = "<!-- Encountered circular include. File was not included -->";
    protected static final String ERROR_LOCATING_FILE_MESSAGE = "CopyAndReplace: File %1$-50s error locating SSI: %2$s Continuing parse...";
    public static final String START_EXCLUDE = "#exclude_start";
    public static final String END_EXCLUDE = "#exclude_end";
    protected File mappingsFile = null;
    protected boolean ssi = false;
    private File manifestDestination;

    public void setUsingPropertyFile(boolean usingPropertyFile) {
        this.usingPropertyFile = usingPropertyFile;
    }

    /**
     * Defines if the mapping file should be read in as a java Property file or
     * as the standard defined above. <b>TRUE</b> if the the file should be read
     * as a Java Property file. <b>FALSE</b> for Barracuda defined property file.
     */
    protected boolean usingPropertyFile = false;

    Set<File> includeFiles = new HashSet<>();

    public Set<File> getIncludeFiles() {
        return includeFiles;
    }

    /**
     * Sets the mappings file.
     */
    public void setMappings(File mappingsFile) {
        this.mappingsFile = mappingsFile;
    }


    /**
     * Process files copied for ssi
     *
     * @param issi set "true" to process copied files for ssi
     */
    public void setSsi(BooleanAttribute issi) {
        ssi = (issi.getValue().equals("yes") || issi.getValue().equals("true"));
    }

    public void setMappingIsPropertyFile(BooleanAttribute issi) {
        ssi = (issi.getValue().equals("yes") || issi.getValue().equals("true"));
    }

    /**
     * processSSI is the result of a code refactoring due to changes in Ant 1.5 to Ant 1.6
     * Ant 1.6 supports multiple target file, therefore processSSI might be called n-times
     *
     * @param fromFile
     * @param toFile
     */
    protected void processSSI(String fromFile, String toFile) {
        File targetFile = new File(toFile);
        File fromDir = new File(fromFile).getParentFile();
        processSSI(targetFile, fromDir);
    }

    protected void processSSI(File targetFile, File fromDir) {
        byte[] fileContent = retrieveFileContents(targetFile, fromDir);
        if (fileContent == null) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.write(fileContent);
        } catch (IOException e) {
            log("Error writing file " + targetFile + ":" + e, Project.MSG_ERR);
        }
    }

    /**
     * perform the token-value based replacement. This method was introduced due to changes
     * from Ant 1.5 to Ant 1.6. It might be called multiple times
     *
     * @param fromFile
     * @param toFile
     * @param mappings
     */
    protected void processReplace(String fromFile, String toFile, Properties mappings) {
        File targetFile = new File(toFile);

        //do the replace on each file (this is important: we only
        //want to do the replace on files we actually copied in)
        Replace replace = new Replace();
        replace.setProject(this.getProject());
        replace.setFile(targetFile);

        for (Map.Entry entry : mappings.entrySet()) {
            Replace.Replacefilter rf = replace.createReplacefilter();
            rf.setToken(entry.getKey().toString());
            rf.setValue(entry.getValue().toString());
        }

        //figure out the relative path token (so that we can reference this in the mappings file)
        String relPath = "";
        File parentDir = (targetFile != null ? targetFile.getParentFile() : null);
        while (parentDir != null && parentDir.compareTo(this.destDir) != 0) {
            relPath = relPath + "../";
            parentDir = parentDir.getParentFile();
        }
        Replace.Replacefilter rf = replace.createReplacefilter();
        rf.setToken(REL_PATH_TOKEN);
        rf.setValue(relPath);

        replace.execute();
    }

    /**
     * <p>Actually does the file (and possibly empty directory) copies.
     * This is a good method for subclasses to override.
     * <p>
     * <p>Note that all the copy functionality occurs by simply deferring
     * to the superclass implementation. The replace functionality follows:
     * <p>
     * <ul>
     * <li><p>first we make sure there is a mappings file</li>
     * <li><p>next we parse it to determine all token/value mappings</li>
     * <li><p>finally we iterate through the list of files that actually
     * got copied and we create Replace task for each of them. This
     * task contains all the various token/value mappings, and
     * gets executed for each file, effectively making all the
     * necessary text substitutions</li>
     * <ul>
     */
    @Override
    protected void doFileOperations() {
        //start by allowing the basic copy to occur
        super.doFileOperations();

        supportSsi();

        /* The mapping file can either be a property file or the original definition
         * provided by Barracuda. In either case we need the mapping file to be
         * present. If the file doesn't exist or was not provided we need to inform
         * the user and we are finished.
         */
        if (mappingsFile == null) {
            return;
        } else if (!mappingsFile.exists()) {
            log("Unable to find mappings file: " + mappingsFile + " ... files copied but no text replace occurred", Project.MSG_ERR);
            return;
        }

        Properties mappings;
        try {
            if (usingPropertyFile) {
                mappings = new Properties();
                mappings.load(new FileReader(mappingsFile));
            } else {
                mappings = processMappingFile();
            }
        } catch (IOException e) {
            log("Error reading file mapping file failed: " + mappingsFile + ":" + e, Project.MSG_ERR);
            return;
        }

        //now iterate through the file copy list
        if (fileCopyMap.size() > 0) {
            log("Replacing text in copied files", Project.MSG_INFO);

            Enumeration enumeration = fileCopyMap.keys();
            while (enumeration.hasMoreElements()) {
                //figure out the target file
                String fromFile = (String) enumeration.nextElement();

                // Ant 1.5 returns a String, Ant 1.6 returns a String[] (multiple targets)
                Object targetFile = fileCopyMap.get(fromFile);
                boolean isAnt15 = targetFile instanceof String;
                if (isAnt15) {
                    processReplace(fromFile, (String) targetFile, mappings);
                } else {
                    String[] targetArray = (String[]) targetFile;
                    for (int j = 0; j < targetArray.length; j++) {
                        processReplace(fromFile, targetArray[j], mappings);
                    }
                }
            }
        }
    }

    @Override
    public void execute() throws BuildException {
        super.execute();
        if (manifestDestination != null) {
            try (FileWriter fileWriter = new FileWriter(manifestDestination)) {
                for (File includeFile : includeFiles) {
                    fileWriter.write(includeFile.getCanonicalPath());
                    fileWriter.write("\n");
                }
                fileWriter.flush();
            } catch (IOException io) {
                log("Error:" + io.getMessage());
            }
        }
    }

    private void supportSsi() {
        if (!ssi)
            return;

        if (fileCopyMap.size() > 0) {
            log("Processing copied files for server side includes", Project.MSG_INFO);
        }

        Enumeration enumeration = fileCopyMap.keys();
        while (enumeration.hasMoreElements()) {
            String fromFile = (String) enumeration.nextElement();
            Object targetFile = fileCopyMap.get(fromFile);

            if (targetFile instanceof String)
                processSSI(fromFile, (String) targetFile);
            else
                for (String target : (String[]) targetFile)
                    processSSI(fromFile, target);
        }
    }

    /**
     * Process the original format for mapping file. This is done to allow for
     * backwards compatibility (Property files should be used where possible
     * instead of using this format).
     *
     * @return The properties gathered from the mapping file
     * @throws IOException
     */
    private Properties processMappingFile() throws IOException {
        Properties mappingProperties = new Properties();
        FileInputStream fis = new FileInputStream(mappingsFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        try {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                String token = null;
                String value = null;

                int spos = currentLine.indexOf("token=");
                if (spos < 0 || spos + 6 >= currentLine.length()) {
                    continue;
                }
                spos += 6;
                String delim = currentLine.substring(spos, spos + 1);
                spos += 1;
                int epos = currentLine.indexOf(delim, spos + 1);
                if (epos < 0 || epos + 1 >= currentLine.length()) {
                    continue;
                }
                token = currentLine.substring(spos, epos);
                if (token == null) {
                    continue;
                }

                spos = currentLine.indexOf("value=", epos + 1);
                if (spos < 0 || spos + 6 >= currentLine.length()) {
                    continue;
                }
                spos += 6;
                delim = currentLine.substring(spos, spos + 1);
                spos += 1;
                epos = currentLine.indexOf(delim, spos + 1);
                if (epos < 0) {
                    continue;
                }
                value = currentLine.substring(spos, epos);
                if (value == null) {
                    continue;
                }

                mappingProperties.setProperty(token, value);
            }
        } finally {
            br.close();
            fis.close();
            return mappingProperties;
        }
    }

    public static class BooleanAttribute extends EnumeratedAttribute {

        public String[] getValues() {
            return new String[]{"yes", "no", "true", "false"};
        }
    }

    /**
     * Attempts to read the contents of a file into a byte array. This is dangerous
     * as the file on disk could be bigger than you memory left on the build client.
     *
     * @param targetFile The file to read
     * @return the byte array of the file
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected byte[] readFileToByteArray(File targetFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(targetFile)) {
            try (BufferedInputStream in = new BufferedInputStream(fis)) {
                return StreamUtils.readIntoByteArray(in);
            }
        }
    }

    /**
     * Locate a given file based on the <b>targetFile</b>'s location and the from
     * the <b>forDir</b>'s location.
     *
     * @param targetFile      The current file
     * @param fromDir         The files source directory
     * @param includeFileName The name of the file to locate
     * @return File or <b>null</b> if not found
     */
    private File locateTargetFile(File targetFile, File fromDir, String includeFileName) {
        // Attempt to locate the file in the targetFile's directory
        File fileToLocate = new File(targetFile.getParent(), includeFileName);
        if (fileToLocate.exists()) {
            return fileToLocate;
        }
        //  Attempt to locate the file in the source directory
        fileToLocate = new File(fromDir, includeFileName);
        if (fileToLocate.exists()) {
            return fileToLocate;
        }
        // Attempt to find the file in the current running directory
        // This seems odd but it is legacy (it may be useful if you are building
        // in a temp location)
        fileToLocate = new File(includeFileName);
        if (fileToLocate.exists()) {
            return fileToLocate;
        }

        File startDir = fromDir;
        File topDir = (mappingsFile != null ? mappingsFile.getParentFile() : null);
        while ((startDir = startDir.getParentFile()) != null) {
            fileToLocate = new File(startDir, includeFileName);
            if (fileToLocate.exists()) {
                return fileToLocate;
            }
            // Note: this may need to be checked. If the top directory doesn't
            // exist and the file can't be found it will walk all the way to /
            if (topDir != null && topDir.compareTo(startDir) == 0) {
                break;
            }
        }

        return null;
    }

    /**
     * Recursively walk through each layer of files and includes parsing each layer. <p>
     * Returns the byte array containing the current layers contents and all lower
     * layer contents. <p>
     * Note: this <b>does</b> protect against circular includes.
     *
     * @param targetFile  The current layer that needs to be parsed.
     * @param fromDir     The directory where the file could have originated from
     * @param parentFiles The files that have already been parsed upstream
     * @return The contents of the current target file and all
     * lower layers. Will return an empty byte array
     * if a problem occurs.
     */
    protected String replaceIncludeTags(File targetFile, File fromDir, List<String> parentFiles) {
        targetFile = createFile(targetFile, fromDir);

        if (!targetFile.exists() || !targetFile.canRead()) {
            log("Encountered error reading file " + targetFile + " continuing parse...", Project.MSG_WARN);
            return "";
        }

        try {
            // Making sure that circular or cyclical includes are caught and handled
            int ssiInParentsListIndex = parentFiles.indexOf(targetFile.getCanonicalPath());
            if (ssiInParentsListIndex > 0) {
                return generateError(parentFiles, ssiInParentsListIndex);
            }

            parentFiles.add(targetFile.getCanonicalPath());

            // if the current file is not in cyclical include parser the file for
            // include statements.
            byte[] fileContent = readFileToByteArray(targetFile);
            StringBuilder newContents = new StringBuilder(new String(fileContent));
            while (true) {
                Matcher m = SSI_INCLUDE_TAG.matcher(newContents);
                if (!m.find()) {
                    break;
                }
                String includeFileName = m.group(1);
                int includeStartIndex = m.start();
                int includeEndIndex = m.end();

                File ssiFile = locateTargetFile(targetFile, fromDir, includeFileName);
                if (ssiFile == null) {
                    newContents.replace(includeStartIndex, includeEndIndex, HTML_ERROR_MESSAGE);
                    continue;
                } else {
                    includeFiles.add(ssiFile);
                    newContents.replace(includeStartIndex, includeEndIndex,
                           replaceIncludeTags(ssiFile, fromDir, new LinkedList<>(parentFiles)));
                }
            }
            return newContents.toString();
        } catch (IOException e) {
            log("Encountered error reading file " + targetFile + " continuing parse...\n:" + e, Project.MSG_WARN);
            return "";
        }
    }

    private File createFile(File targetFile, File fromDir) {
        File targetFile1 = targetFile;
        if (!targetFile1.exists() && fromDir != null) {
            String targetFileName = targetFile1.getName();
            targetFile1 = new File(fromDir, targetFileName);
        }
        return targetFile1;
    }


    private String generateError(List<String> parentFiles, int ssiInParentsListIndex) {
        log("Encountered circular include: rejecting include: Continuing parse...", Project.MSG_ERR);
        // Building error messages
        for (int pIdx = 0; pIdx < parentFiles.size(); pIdx++) {
            StringBuilder tabbedName = new StringBuilder();
            for (int tabIndex = 0; tabIndex < pIdx; tabIndex++) {
                tabbedName.append("   ");
            }
            tabbedName.append(parentFiles.get(pIdx));
            System.out.println(tabbedName.toString());
        }
        StringBuilder tabbedName = new StringBuilder();
        for (int tabIndex = 0; tabIndex < ssiInParentsListIndex; tabIndex++) {
            tabbedName.append("   ");
        }
        tabbedName.append(parentFiles.get(ssiInParentsListIndex));
        System.err.println(tabbedName.toString());
        // return the circular message. At this point it would be bad to
        // continue the parse as it would result in an infinite loop
        return CIRCULAR_INCLUDE_MESSAGE;
    }

    /**
     * Parses the targetFile in order to find and replace include tags and
     * exclude tags.
     *
     * @param targetFile The target file is the file to parse
     * @param fromDir    The directory to use as a fall back if the file doesn't exist
     * @return the contents of the containing the result of the parse and
     * replace.
     */
    public byte[] retrieveFileContents(File targetFile, File fromDir) {
        String tagsReplaced = replaceIncludeTags(
                createFile(targetFile, fromDir),
                fromDir,
                new LinkedList<String>()
        );
        return stripExcluded(tagsReplaced).getBytes();
    }

    private String stripExcluded(String result) {
        int spos = result.indexOf(START_EXCLUDE);
        int epos = 0;
        if (spos > -1) {
            StringBuilder sb = new StringBuilder(result);
            while (spos > -1) {
                epos = sb.indexOf(END_EXCLUDE, spos);
                if (epos < 0) {
                    break;
                }
                sb.replace(spos, epos + END_EXCLUDE.length(), " ");
                spos = sb.indexOf(START_EXCLUDE, spos);
            }
            result = sb.toString();
        }
        return result;
    }

    public void setExportManifest(File destination) throws IOException {
        manifestDestination = destination;
    }
}

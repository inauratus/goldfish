package org.barracudamvc.taskdefs;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class CopyAndReplaceTest {
    CopyAndReplace copyAndReplace;
    List<String> parentFiles;
    File fromDirectory;
    File targetFile;

    @Before
    public void setUp() {
        copyAndReplace = new CopyAndReplaceDouble();
        parentFiles = new ArrayList<>();
        fromDirectory = null ; // new File("taskdefs");
    }

    @Test
    public void givenTargetFileWithNoInclude_expectEmptyByteArray() throws URISyntaxException {
        targetFile = getTestFile("CopyAndReplace_emptyInclude.txt");
        assertThat(copyAndReplace.replaceIncludeTags(targetFile, fromDirectory, parentFiles), matches(""));
    }

    @Test
    public void givenAFileWithText_expectByteArrayToContainFileContents() throws URISyntaxException, IOException {
        targetFile = getTestFile("CopyAndReplace_nonEmptyFile.txt");
        assertThat(byteToString(copyAndReplace.readFileToByteArray(targetFile)),
                matches(
                        "this is not empty\n" +
                                ":)"
                ));
    }

    @Test
    public void givenTargetFileWithSingleIncludeForEmptySSI_expectIncludeReplacedByEmptyString() throws URISyntaxException {
        targetFile = getTestFile("CopyAndReplace_emptyIncludeSSI.txt");
        assertThat(copyAndReplace.replaceIncludeTags(targetFile, fromDirectory, parentFiles), matches(""));

    }

    @Test
    public void givenTargetFileWithSingleIncludeForNonEmptyTextSSI_expectIncludeReplacedBySSIContent() throws URISyntaxException {
        targetFile = getTestFile("CopyAndReplace_nonEmptyIncludeSSI.txt");
        assertThat(copyAndReplace.replaceIncludeTags(targetFile, fromDirectory, parentFiles),
                matches("\n    I contain text\n"));

    }

    @Test
    public void givenTargetFileWithSingleIncludeForNonEmptySSI_expectIncludeReplacedBySSI() throws URISyntaxException {
        targetFile = getTestFile("CopyAndReplace_emptySSIInclude.txt");
        assertThat(copyAndReplace.replaceIncludeTags(targetFile, fromDirectory, parentFiles), matches(
                "<!-- start CR_nonEmptySSI.ssi -->\n" +
                        "    I contain text\n" +
                        "<!-- end CR_nonEmptySSI.ssi -->")
        );

    }

    @Test
    public void givenTargetFileWithSingleIncludeForTwoEmptySSIs_expectIncludeTagsReplacedByTwoSSIs() throws URISyntaxException {
        targetFile = getTestFile("CopyAndReplace_twoEmptySSI.txt");
        assertThat(copyAndReplace.replaceIncludeTags(targetFile, fromDirectory, parentFiles), matches(
                "<!-- start CR_firstEmptySSI.ssi -->\n" +
                        "<!-- end CR_firstEmptySSI.ssi -->\n" +
                        "\n" +
                        "<!-- start CR_secondEmptySSI.ssi -->\n" +
                        "<!-- end CR_secondEmptySSI.ssi -->"));
    }

    @Test
    public void givenTargetFileWithTwoIncludesForEmptySSI_expectEachIncludeReplacedByEmptySSI() throws URISyntaxException {
        targetFile = getTestFile("CopyAndReplace_twoInclude.txt");
        assertThat(copyAndReplace.replaceIncludeTags(targetFile, fromDirectory, parentFiles), matches(
                "<!-- start CR_nonEmptySSI.ssi -->\n" +
                        "    I contain text\n" +
                        "<!-- end CR_nonEmptySSI.ssi -->\n" +
                        "\n    I contain text\n"));
    }

    @Test
    public void givenTargetFileWithSingleIncludeForSingleSSIWithInclude_expectIncludeReplacedBySSIAndSSIContentsOfSecondInclude() throws URISyntaxException {
        targetFile = getTestFile("CopyAndReplace_singleIncludeSSI.txt");
        assertThat(copyAndReplace.replaceIncludeTags(targetFile, fromDirectory, parentFiles),
                matches(
                        "<!-- start CR_SSI.ssi -->\n" +
                                "In SSI\n" +
                                "<!-- end CR_SSI.ssi -->\n" + "<!-- start CR_nonEmptySSI.ssi -->\n" +
                                "    I contain text\n" +
                                "<!-- end CR_nonEmptySSI.ssi -->\n"));
    }

    @Test
    public void givenTargetFileWithCyclicalInclude_expectErrorMessageByteArray() throws URISyntaxException {
        targetFile = getTestFile("CopyAndReplace_cyclicalIncludeSSI.txt");
        assertThat(copyAndReplace.replaceIncludeTags(targetFile, fromDirectory, parentFiles), matches("<!-- Encountered circular include. File was not included -->"));
    }

    private String byteToString(byte[] data) {
        return new String(data);
    }

    private File getTestFile(String fileName) throws URISyntaxException {
        return new File(this.getClass().getResource(fileName).toURI());
    }

    public static class CopyAndReplaceDouble extends CopyAndReplace {

        @Override
        public Project getProject() {
            return new Project() {
                @Override
                public void log(String message) {

                }

                @Override
                public void log(String message, int msgLevel) {
                }

                @Override
                public void log(Task task, String message, int msgLevel) {
                }

                @Override
                public void log(Target target, String message, int msgLevel) {
                }
            };
        }

        @Override
        public void log(String msg) {
        }

        @Override
        public void log(String msg, int msgLevel) {
        }
    }

    private Matcher<String> matches(String verification) {
        return is(
                String.format(
                        verification)
        );
    }
}
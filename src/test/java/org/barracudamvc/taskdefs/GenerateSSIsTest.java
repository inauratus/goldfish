package org.barracudamvc.taskdefs;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class GenerateSSIsTest {
    private GenerateSSIs generateSSIs;
    private SpySSIListener ssiListener;

    @Before
    public void startUp(){
        generateSSIs = new GenerateSSIDouble();
        generateSSIs.force = true;
        ssiListener = new SpySSIListener();
    }

    @Test
    public void givenFileContainingNoSSIs_expectNoSSIListenerCalls() throws URISyntaxException {
        generateSSIs.generateSSIs(getTestFile("GenerateSSIs_noSSIs.txt"), ssiListener);

        assertFalse(ssiListener.isStartCalled());
        assertFalse(ssiListener.isWriteCalled());
        assertFalse(ssiListener.isEndCalled());
    }

    @Test
    public void givenFileContainingEmptySSI_expectSSIListenerCalls() throws URISyntaxException {
        generateSSIs.generateSSIs(getTestFile("GenerateSSIs_emptySSI.txt"),ssiListener);
        assertTrue(ssiListener.isStartCalled());
        assertThat(ssiListener.getName(),is("emptySSI"));

        assertTrue(ssiListener.isWriteCalled());
        assertThat(ssiListener.getContent(),is(System.lineSeparator()));

        assertTrue(ssiListener.isEndCalled());
    }

    @Test
    public void givenFileContainingNonEmptySSI_expectSSIListenerCallsAndContent() throws URISyntaxException {
        generateSSIs.generateSSIs(getTestFile("GenerateSSIs_nonEmptySSI.txt"), ssiListener);

        assertTrue(ssiListener.isStartCalled());
        assertThat(ssiListener.getName(),is("nonEmptySSI"));

        assertTrue(ssiListener.isWriteCalled());
        assertThat(ssiListener.getContent(),is(System.lineSeparator() +"    I contain text" +"\r\n"));

        assertTrue(ssiListener.isEndCalled());
    }

    @Test
    public void givenFileContainingTwoEmptyNestedSSIs_expectSSIListenerCalls() throws URISyntaxException {
        generateSSIs.generateSSIs(getTestFile("GenerateSSIs_twoEmptyNestedSSIs.txt"),ssiListener);

        assertTrue(ssiListener.isStartCalled());
        assertTrue(ssiListener.isWriteCalled());
        assertThat(ssiListener.getName(),is("nestedEmptySSI"));
        assertTrue(ssiListener.isEndCalled());
    }

    @Test
    public void givenFileContainingTwoEmptySSIs_expectTwoSSSListenerStartCalls() throws URISyntaxException {
        generateSSIs.generateSSIs(getTestFile("GenerateSSIs_twoEmptySSIs.txt"),ssiListener);

        assertTrue(ssiListener.isStartCalled());
        assertThat(ssiListener.getStartCount(),is(2));
        assertThat(ssiListener.getName(),is("secondEmptySSI"));
        assertThat(ssiListener.getContent(), is(System.lineSeparator()));
        assertThat(ssiListener.getName(),is("firstEmptySSI"));
        assertThat(ssiListener.getContent(), is("\r\n"));

        assertTrue(ssiListener.isWriteCalled());
        assertTrue(ssiListener.isEndCalled());
    }

    @Test
    public void givenFileContainingTwoNonEmptySSIs_expectTwoStartSSIListenerCallsAndEachSSIFileToContainContent() throws URISyntaxException {
        generateSSIs.generateSSIs(getTestFile("GenerateSSIs_twoNonEmptySSIs.txt"),ssiListener);

        assertTrue(ssiListener.isStartCalled());
        assertThat(ssiListener.getStartCount(),is(2));
        assertThat(ssiListener.getName(),is("secondNonEmptySSI"));
        assertThat(ssiListener.getContent(), is("\r\nNow in the second SSI\r\nI am a message!\r\n"));
        assertThat(ssiListener.getName(),is("firstNonEmptySSI"));
        assertThat(ssiListener.getContent(), is("\r\nI am in the first SSI\r\n"));

    }

    private File getTestFile(String fileName) throws URISyntaxException {
        return new File(this.getClass().getResource(fileName).toURI());
    }

    private Object[] getSSIFileNames(Set<File> ssiFiles){
        List<String> name = new ArrayList<>();
        for (File file: ssiFiles){
            name.add(file.getName());
        }
        return name.toArray();
    }


    public static class GenerateSSIDouble extends GenerateSSIs {
        @Override
        public Project getProject() {
            return new Project(){
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
        public void log(String msg) {}

        @Override
        public void log(String msg, int msgLevel) {}
    }

    private static class SpySSIListener implements SSIListener {
        private boolean isStartCalled = false;
        private boolean isWriteCalled = false;
        private boolean isEndCalled = false;
        private List<String> fileNames = new ArrayList<>();
        private String name;
        private String content = "";
        private int startCount = 0;
        List<SSI> ssiFiles = new ArrayList<>();
        SSI oneSSIFile;

        private class SSI {
            private String name;
            private File directory;
            private String contents = "";
            private

            SSI(File directory, String name){
                this.directory = directory;
                this.name = name;
            }
            public void appendContents(String contents){
                this.contents += contents;
            }
            public String getContents(){
                return contents;
            }
        }

        @Override
        public void start(File directory, String name) {
            isStartCalled = true;
            fileNames.add(name);
            oneSSIFile = new SSI(directory, name);
            ssiFiles.add(oneSSIFile);
            startCount++;
        }

        public int getStartCount() {
            return startCount;
        }

        @Override
        public void write(String ssiContents) {
            isWriteCalled = true;
            oneSSIFile.appendContents(ssiContents);


        }
        @Override
        public void end() throws IOException {
            isEndCalled = true;

        }

        public List<SSI> getSsiFiles() {
            return ssiFiles;
        }

        public String getContent(){
            return ssiFiles.remove(ssiFiles.size() -1).getContents();

        }

        public String getName() {
            return fileNames.remove(fileNames.size() -1);
        }

        public boolean isStartCalled() {
            return isStartCalled;
        }

        public boolean isWriteCalled() {
            return isWriteCalled;
        }

        public boolean isEndCalled() {
            return isEndCalled;
        }
    }
}
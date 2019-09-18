package org.barracudamvc.taskdefs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class SSIListenerTest {
    public static final String TEST_FILE_NAME = "TestFile";
    public static final String FULL_NAME = TEST_FILE_NAME + ".ssi";
    SSIListener ssiListener;

    String fileContents;
    private Path temp;

    @Before
    public void initialize() throws IOException {
        ssiListener = new SSIListenerFileWriter();
        temp = Files.createTempDirectory("temp");

        ssiListener.start(temp.toFile(), TEST_FILE_NAME);

    }

    @After
    public void tearDown() throws Exception {

        Files.walkFileTree(temp, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if(attrs.isRegularFile()){
                    file.toFile().delete();
                }
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                dir.toFile().delete();
                return super.postVisitDirectory(dir, exc);
            }
        });

    }

    @Test
    public void givenDirectoryAndFileName_expectFileCreatedInDirectoryWithGivenName() throws IOException {
        ssiListener.end();
        File written = temp.resolve(FULL_NAME).toFile();
        assertTrue(written.getAbsolutePath(), written.exists());
    }

    @Test
    public void givenEmptyStringToWrite_expectFileToBeEmpty() throws IOException {
        ssiListener.write("");
        ssiListener.end();
        fileContents = new String(Files.readAllBytes(temp.resolve(FULL_NAME)));
        assertThat(fileContents,is(""));
    }

    @Test
    public void givenStringToWrite_expectFileToContainString() throws IOException {
        ssiListener.write("Hello World");
        ssiListener.end();
        fileContents = new String(Files.readAllBytes(temp.resolve(FULL_NAME)));
        assertThat(fileContents,is("Hello World"));
    }

    @Test
    public void givenTwoFileWrites_expectFileContentsToContainBothStringValues() throws IOException {
        ssiListener.write("hello");
        ssiListener.write("world");
        ssiListener.end();
        fileContents = new String(Files.readAllBytes(temp.resolve(FULL_NAME)));
        assertThat(fileContents,is("helloworld"));
    }

    @Test
    public void givenMultipleWrites_expectFileToContainAllStringValues() throws IOException {
        for(int i = 0; i< 5; i++){
            ssiListener.write("hello");
            ssiListener.write("\n");
        }
        ssiListener.end();
        fileContents = new String(Files.readAllBytes(temp.resolve(FULL_NAME)));
        assertThat(fileContents,is("hello\nhello\nhello\nhello\nhello\n"));
    }

    @Test
    public void givenTwoStringsWithANewLineWritten_expectFileToContainTwoLines() throws IOException {
        ssiListener.write("1");
        ssiListener.write(System.lineSeparator());
        ssiListener.write("2");
        ssiListener.write(System.lineSeparator());
        ssiListener.end();
        String[] lines = new String(Files.readAllBytes(temp.resolve(FULL_NAME))).split(System.lineSeparator());
        int lineCount = lines.length;
        assertThat(lineCount,is(2));
    }

}
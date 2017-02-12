package com.commandline;

import org.junit.*;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.fail;

public class MainClassTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private File temporaryFolder;
    private Commandline commandline;
    private File testFile1;
    private File testFile2;


    @Rule
    public final TextFromStandardInputStream systemInMock
            = emptyStandardInputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void createTemporaryFolder() {
        temporaryFolder = null;
        try {
            temporaryFolder = testFolder.newFolder("tempfolder");
            testFile1 = new File(temporaryFolder.getPath() + File.separator + "Test1.txt");
            testFile2 = new File(temporaryFolder.getPath() + File.separator + "Test2.txt");
            testFile1.createNewFile();
            testFile2.createNewFile();

            if (!temporaryFolder.exists()) {
                fail();
            }
        } catch (IOException e) {
            fail();
        }
    }

    @Before
    public void createCommandline() {
        commandline = new Commandline();
        String[] args = {"cd", temporaryFolder.getPath()};
        commandline.changeDirectory(args);
        outContent.reset();
    }


    @Test
    public void RunThenExitTest() {
        systemInMock.provideLines("exit");
        Commandline.main(null);
        assertEquals(System.getProperty("user.home") + ": ", outContent.toString());
    }

    @Test
    public void runThenLsInDirectoryThenCreateDirectoryThenChangeDirectorythenExitTest() {
        systemInMock.provideLines("ls", "mkdir temp", "cd " + temporaryFolder.getPath() + File.separator + "temp", "exit");
        commandline.run();
        assertEquals(
                temporaryFolder.getPath() + ": Files in current directory:\r\n" + "" +
                        "Test1.txt\r\n" +
                        "Test2.txt\r\n" +
                        temporaryFolder.getPath() + ": Directory: temp created\r\n" +
                        temporaryFolder.getPath() + ": " +
                        temporaryFolder.getPath() + File.separator + "temp: ",
                outContent.toString());
    }


    public String setUpDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    @Test
    public void runThenDiffThenDateThenUnknownCommandThenExit(){
        String command1 = "diff " + testFile1.getName() + " " + testFile2.getName();
        systemInMock.provideLines(command1, "date", "UNKNOWN COMMAND", "exit");
        commandline.run();
        assertEquals("" +
                temporaryFolder.getPath() + ": " +
                temporaryFolder.getPath() + ": " + setUpDate() + "\r\n" +
                temporaryFolder.getPath() + ": Command: UNKNOWN COMMAND is not defined\r\n" +
                        temporaryFolder.getPath() + ": ",
                outContent.toString());
    }

    @Test
    public void runThenDiffWithOneArgumentThenExit(){
        systemInMock.provideLines("diff " + testFile1.getName(), "exit");
        commandline.run();
        assertEquals(
                temporaryFolder.getPath() + ": "
                + "Error: not enough files in arguments\r\n" +
                        temporaryFolder.getPath() + ": "
                , outContent.toString()


        );
    }

    @Test
    public void sequenceDiagramCoverAllTransisionsTest(){
        String validDiff = "diff " + testFile1.getName() + " " + testFile2.getName();
        String invalidDiff = "diff " + testFile1.getName();
        String validMkdir = "mkdir tempfolder";
        String invalidMkdir = validMkdir;
        String validCd = "cd " + temporaryFolder.getPath()+ File.separator + "tempfolder";
        String invalidCd = "cd NOT_EXISTING_PATH";
        String lsWithoutFilter = "ls";
        String lsWithFilter = "ls Test1";
        String date = "date";
        String exit = "exit";

        systemInMock.provideLines(validDiff, invalidDiff, validMkdir, invalidMkdir, validCd, invalidCd, lsWithoutFilter, lsWithFilter, date, exit);

        commandline.run();

        String comparisonString =
                temporaryFolder.getPath() + ": " +
                temporaryFolder.getPath() + ": Error: not enough files in arguments\n" +
                temporaryFolder.getPath() + ": Directory: tempfolder created\n" +
                temporaryFolder.getPath() + ": File already exists\n" +
                temporaryFolder.getPath() + ": " +
                temporaryFolder.getPath() + File.separator + "tempfolder: Error: Illegal path name\n" +
                temporaryFolder.getPath() + File.separator + "tempfolder: Files in current directory:\n" +
                temporaryFolder.getPath() + File.separator + "tempfolder: Files of chosen type in current directory:\n" +
                temporaryFolder.getPath() + File.separator + "tempfolder: "+ setUpDate() +"\n" +
                temporaryFolder.getPath() + File.separator + "tempfolder: ";

        String resultString = outContent.toString();

        comparisonString = comparisonString.replaceAll("\r", "").replaceAll("\n", "");
        resultString = resultString.replaceAll("\r", "").replaceAll("\n", "");

        assertEquals(comparisonString, resultString);
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }
}

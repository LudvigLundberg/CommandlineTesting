package com.commandline.commands;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

public class DiffCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private File testFile1;
    private File testFile2;
    private File temporaryFolder;
    private DiffCommand diffCommand;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Before
    public void createDiffCommand(){
        diffCommand = new DiffCommand();
    }

    @Before
    public void createTemporaryFolder(){
        temporaryFolder = null;
        try {
            temporaryFolder = testFolder.newFolder("Test");
            testFile1 = new File(temporaryFolder.getPath()+ File.separator + "Test1.txt");
            testFile2 = new File(temporaryFolder.getPath()+ File.separator + "Test2.txt");
            testFile1.createNewFile();
            testFile2.createNewFile();
        }catch (IOException e){
            fail();
        }
    }

    public void createTestFile(String contentInFile1, String contentInFile2){
        try {
            PrintWriter writer1 = new PrintWriter(testFile1, "UTF-8");
            PrintWriter writer2 = new PrintWriter(testFile2, "UTF-8");
            writer1.println(contentInFile1);
            writer2.println(contentInFile2);
            writer1.close();
            writer2.close();
        }catch(IOException e){
            fail();
        }
    }

    @Test
    public void inputIsTwoTextFilesTest() {
        DiffCommand diffCommand = new DiffCommand();
        String[] args = {"Test1.txt", "Test2.txt"};
        assertTrue(diffCommand.checkIfTextFiles(args));
    }

    @Test(expected = IllegalArgumentException.class)
    public void inputIsNotTwoTextFilesTest() {
        DiffCommand diffCommand = new DiffCommand();
        String[] args = {"Test1.jpeg", "Test2.gif"};
        diffCommand.checkIfTextFiles(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void inputIsTextFileAndNotTextFileTest(){
        DiffCommand diffCommand = new DiffCommand();
        String[] args = {"Test1.txt", "Test2.jpeg"};
        diffCommand.checkIfTextFiles(args);
    }

    @Test
    public void createFilesAndSeeIfTheyExistTest(){
        DiffCommand diffCommand = new DiffCommand();
        String[] args = {"Test1.txt", "Test2.txt"};
        assertTrue(diffCommand.createFiles(args, temporaryFolder.toPath()));
    }

    @Test
    public void fileNamesDoNotExistTest(){
        DiffCommand diffCommand = new DiffCommand();
        String[] args = {"Test100.txt", "Test101.txt"};
        assertFalse(diffCommand.createFiles(args, temporaryFolder.toPath()));
    }

    @Test
    public void fileNameDoesExistAndFileNameDoesNotExistTest(){
        DiffCommand diffCommand = new DiffCommand();
        String[] args = {"Test1.txt", "Test100.txt"};
        assertFalse(diffCommand.createFiles(args, temporaryFolder.toPath()));
    }

    @Test
    public void twoFilesWithOneSimilarRowAndOneDifferentRowTest(){
        ExtendsDiffCommand extendsDiffCommand = new ExtendsDiffCommand();
        createTestFile("same thing\nnot the same thing", "same thing\nnot the same thaaang");
        extendsDiffCommand.listFileToLines(testFile1, testFile2);
        extendsDiffCommand.lookAfterDifferences();
        assertEquals("Diff on row 1: not the same thing vs not the same thaaang\r\n", outContent.toString());
    }

    @Test
    public void twoFilesThatAreNotEquallyLongTest(){
        ExtendsDiffCommand extendsDiffCommand = new ExtendsDiffCommand();
        createTestFile("same thing\nnot the same thing", "same thing\nnot the same thaaang\nthird thing");
        extendsDiffCommand.listFileToLines(testFile1, testFile2);
        extendsDiffCommand.lookAfterDifferences();
        assertEquals("Diff on row 1: not the same thaaang vs not the same thing\r\n" + "Diff on row 2: third thing vs\r\n", outContent.toString());
    }

    @Test
    public void checkForFileDifferencesTest(){
        DiffCommand diffCommand = new DiffCommand();
        createTestFile("same thing\nnot the same thing", "same thing\nnot the same thaaang\nthird thing");
        diffCommand.listFileToLines(testFile1, testFile2);
        diffCommand.lookAfterDifferences();
        assertEquals("Diff on row 1: not the same thaaang vs not the same thing\r\n"
                + "Diff on row 2: third thing vs\r\n", outContent.toString());
    }

    @Test
    public void checkTwoDifferentFilesWithTheSameContentTest(){
        DiffCommand diffCommand = new DiffCommand();
        createTestFile("same thing\nnot the same thing", "same thing\nnot the same thing");
        diffCommand.listFileToLines(testFile1, testFile2);
        diffCommand.lookAfterDifferences();
        assertEquals("", outContent.toString());
    }

    @Test
    public void notOneSimilarRowInFilesTest(){
        DiffCommand diffCommand = new DiffCommand();
        createTestFile("same thing\nnot the same thing","other words\nare in this file" );
        diffCommand.listFileToLines(testFile1, testFile2);
        diffCommand.lookAfterDifferences();
        assertEquals("Diff on row 0: same thing vs other words\r\n"
                + "Diff on row 1: not the same thing vs are in this file\r\n", outContent.toString());
    }

    @Test
    public void sameTextFileComparedToItselfTest(){
        DiffCommand diffCommand = new DiffCommand();
        createTestFile("test\ntest", "test2\ntest2");
        diffCommand.listFileToLines(testFile1, testFile1);
        diffCommand.lookAfterDifferences();
        assertEquals("", outContent.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileNameContainsIllegalCharactersGreaterThanTest(){
        DiffCommand diffCommand = new DiffCommand();
        String str = temporaryFolder.getPath();
        Path path = Paths.get(str);
        String[] args = {"Test.txt", "Te>st.txt"};
        diffCommand.createFiles(args, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileNameContainsIllegalCharactersSmallerThanTest(){
        DiffCommand diffCommand = new DiffCommand();
        String str = temporaryFolder.getPath();
        Path path = Paths.get(str);
        String[] args = {"<est.txt", "Test.txt"};
        diffCommand.createFiles(args, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileNameContainsIllegalCharactersQuotationMarkTest(){
        DiffCommand diffCommand = new DiffCommand();
        String str = temporaryFolder.getPath();
        Path path = Paths.get(str);
        String[] args = {"Test\"txt", "Test.txt"};
        diffCommand.createFiles(args, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileNameContainsIllegalCharactersSlashTest(){
        DiffCommand diffCommand = new DiffCommand();
        String str = temporaryFolder.getPath();
        Path path = Paths.get(str);
        String[] args = {"Test/.txt", "Test.txt"};
        diffCommand.createFiles(args, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileNameContainsIllegalCharactersBackSlashTest(){
        DiffCommand diffCommand = new DiffCommand();
        String str = temporaryFolder.getPath();
        Path path = Paths.get(str);
        String[] args = {"Test.txt", "Test\\.txt"};
        diffCommand.createFiles(args, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileNameContainsIllegalCharactersColonTest(){
        DiffCommand diffCommand = new DiffCommand();
        String str = temporaryFolder.getPath();
        Path path = Paths.get(str);
        String[] args = {"T:st.txt", "Test.txt"};
        diffCommand.createFiles(args, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileNameContainsIllegalCharactersStarTest(){
        DiffCommand diffCommand = new DiffCommand();
        String str = temporaryFolder.getPath();
        Path path = Paths.get(str);
        String[] args = {"Test*txt", "Test.txt"};
        diffCommand.createFiles(args, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileNameContainsIllegalCharactersVerticalLineTest(){
        DiffCommand diffCommand = new DiffCommand();
        String str = temporaryFolder.getPath();
        Path path = Paths.get(str);
        String[] args = {"Test.txt", "Test.tx|"};
        diffCommand.createFiles(args, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileNameContainsIllegalCharactersQuestionMarkTest(){
        DiffCommand diffCommand = new DiffCommand();
        String str = temporaryFolder.getPath();
        Path path = Paths.get(str);
        String[] args = {"Test.txt", "Test?.txt"};
        diffCommand.createFiles(args, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileNameConTest(){
        DiffCommand diffCommand = new DiffCommand();
        String str = temporaryFolder.getPath();
        Path path = Paths.get(str);
        String[] args = {"Test.txt", "con.txt"};
        diffCommand.createFiles(args, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileNamePrnTest(){
        DiffCommand diffCommand = new DiffCommand();
        String str = temporaryFolder.getPath();
        Path path = Paths.get(str);
        String[] args = {"prn.txt", "Test.txt"};
        diffCommand.createFiles(args, path);
    }

    @Test
    public void fileNameWithLegalCharactersTest(){
        DiffCommand diffCommand = new DiffCommand();
        String[] args = {"Test1.txt", "Test2.txt"};
        assertTrue(diffCommand.createFiles(args, temporaryFolder.toPath()));
    }

    @Test(expected = AssertionError.class)
    public void notEnoughArgumentsTest(){
        DiffCommand diffCommand = new DiffCommand();
        String temporaryFolderPath = temporaryFolder.getPath();
        Path path = Paths.get(temporaryFolderPath);
        String[] fileNames = {"Test1.txt"};
        diffCommand.execute(fileNames, path);
    }

    @Test(expected = AssertionError.class)
    public void nullPathTest(){
        DiffCommand diffCommand = new DiffCommand();
        String[] fileNames = {"Test1.txt", "Test2.txt"};
        diffCommand.execute(fileNames, null);
    }

    @Test
    public void sameTextButTabInTheOtherTest(){
        ExtendsDiffCommand extendsDiffCommand = new ExtendsDiffCommand();
        createTestFile("same thing   \nnot the same thing", "same thing\nnot the same thing");
        extendsDiffCommand.listFileToLines(testFile1, testFile2);
        extendsDiffCommand.lookAfterDifferences();
        assertEquals("Diff on row 0: same thing    vs same thing\r\n", outContent.toString());
    }

    @Test
    public void sameTextFileButEmptyRowBetweenLinesInTheOtherTest(){
        ExtendsDiffCommand extendsDiffCommand = new ExtendsDiffCommand();
        createTestFile("same thing\n\nnot the same thing", "same thing\nnot the same thing");
        extendsDiffCommand.listFileToLines(testFile1, testFile2);
        extendsDiffCommand.lookAfterDifferences();
        assertEquals("Diff on row 1:  vs not the same thing\r\n" +
                "Diff on row 2: not the same thing vs\r\n", outContent.toString());
    }

    @Test
    public void compareTextFilesWithTheSameNumbersTest(){
        ExtendsDiffCommand extendsDiffCommand = new ExtendsDiffCommand();
        createTestFile("1234\n5678", "1234\n5678");
        extendsDiffCommand.listFileToLines(testFile1, testFile2);
        extendsDiffCommand.lookAfterDifferences();
        assertEquals("", outContent.toString());
    }

    @Test
    public void compareTextFilesWithDifferentNumbersTest(){
        ExtendsDiffCommand extendsDiffCommand = new ExtendsDiffCommand();
        createTestFile("12345\n5678", "1234\n5678");
        extendsDiffCommand.listFileToLines(testFile1, testFile2);
        extendsDiffCommand.lookAfterDifferences();
        assertEquals("Diff on row 0: 12345 vs 1234\r\n", outContent.toString());

    }

    @Test
    public void compareTextFilesWithOtherCharactersThatAreTheSameTest(){
        ExtendsDiffCommand extendsDiffCommand = new ExtendsDiffCommand();
        createTestFile("!!!", "!!!");
        extendsDiffCommand.listFileToLines(testFile1, testFile2);
        extendsDiffCommand.lookAfterDifferences();
        assertEquals("", outContent.toString());
    }

    @Test
    public void compareTextFilesWithOtherCharactersTharAreNotTheSameTest(){
        ExtendsDiffCommand extendsDiffCommand = new ExtendsDiffCommand();
        createTestFile("???", "!!!");
        extendsDiffCommand.listFileToLines(testFile1, testFile2);
        extendsDiffCommand.lookAfterDifferences();
        assertEquals("Diff on row 0: ??? vs !!!\r\n", outContent.toString());
    }

    @Test
    public void compareTwoEmptyTextFilesTest(){
        ExtendsDiffCommand extendsDiffCommand = new ExtendsDiffCommand();
        createTestFile("", "");
        extendsDiffCommand.listFileToLines(testFile1, testFile2);
        extendsDiffCommand.lookAfterDifferences();
        assertEquals("", outContent.toString());
    }

    @Test
    public void compareTextFilesWithDifferentCasedLettersTest(){
        ExtendsDiffCommand extendsDiffCommand = new ExtendsDiffCommand();
        createTestFile("Hej", "hej");
        extendsDiffCommand.listFileToLines(testFile1, testFile2);
        extendsDiffCommand.lookAfterDifferences();
        assertEquals("Diff on row 0: Hej vs hej\r\n", outContent.toString());
    }

    @Test
    public void compareTextFilesWithTheSameTextButUtf8VsCp1252Test(){
        try{
        PrintWriter writer1 = new PrintWriter(testFile1, "windows-1252");
        PrintWriter writer2 = new PrintWriter(testFile2, "UTF-8");
        writer1.println("hej");
        writer2.println("hej");
        writer1.close();
        writer2.close();
        ExtendsDiffCommand extendsDiffCommand = new ExtendsDiffCommand();
        createTestFile("häj", "häj");
        extendsDiffCommand.listFileToLines(testFile1, testFile2);
        extendsDiffCommand.lookAfterDifferences();
        assertEquals("", outContent.toString());
        }catch(Exception e){
            fail();
        }
    }

    @Test
    public void compareTextFilesWithTheSameTextButNonAsciiCharacters(){
        ExtendsDiffCommand extendsDiffCommand = new ExtendsDiffCommand();
        createTestFile("åäö", "åäö");
        extendsDiffCommand.listFileToLines(testFile1, testFile2);
        extendsDiffCommand.lookAfterDifferences();
        assertEquals("", outContent.toString());
    }

    @Test
    public void asciiLetterVsNonAsciiLetterTest(){
        ExtendsDiffCommand extendsDiffCommand = new ExtendsDiffCommand();
        createTestFile("åä", "aa");
        extendsDiffCommand.listFileToLines(testFile1, testFile2);
        extendsDiffCommand.lookAfterDifferences();
        assertEquals("Diff on row 0: åä vs aa\r\n", outContent.toString());
    }

    @Test
    public void checkIfFileNameContainsIllegalCharacterTest(){
        assertTrue(diffCommand.checkIfFilenameContainsIllegalCharacter("test"));
    }
    @Test
    public void checkIfFilenameContainsReservdNameTest(){
        assertTrue(diffCommand.checkIfFilenameIsNotReserved("Test1.txt"));

    }
    @Test
    public void fileNameContainsIllegalCharacterTest(){
        assertFalse(diffCommand.checkIfFilenameContainsIllegalCharacter("<"));
    }
    @Test
    public void fileNameContainsReservedNameTest(){
        assertFalse(diffCommand.checkIfFilenameIsNotReserved("con.txt"));

    }

    @Test
    public void executeMethodWithExistingFilesAndPathTest(){
        DiffCommand diffCommand = new DiffCommand();
        String[] args = {"test1.txt", "test2.txt"};
        String str = temporaryFolder.getPath();
        Path path = Paths.get(str);
        diffCommand.execute(args, path);
    }

    @Test
    public void executeMethodWithNonExistingFiles(){
        DiffCommand diffCommand = new DiffCommand();
        String[] args = {"notatestfile.txt", "notatestfile2.txt"};
        String str = temporaryFolder.getPath();
        Path path = Paths.get(str);
        diffCommand.execute(args, path);
    }

    @Test
    public void nullFilesListFilesToLineTest(){
        try {
            FileChannel channel = new RandomAccessFile(testFile1, "rw").getChannel();
            channel.lock();
        }catch (FileNotFoundException e){
            fail();
        }catch (IOException e2){
            fail();
        }

        diffCommand.listFileToLines(testFile1, testFile2);
        assertEquals("Error: IOException\r\n", outContent.toString());
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }
}

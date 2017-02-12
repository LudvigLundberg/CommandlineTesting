package com.commandline.commands;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Object;
import java.util.List;


import static org.junit.Assert.*;


public class LsCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private LsCommand lsCommand;
    private File temporaryFolder;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void createLsCommand(){
        lsCommand = new LsCommand();
    }

    @Before
    public void createTemporaryFolder(){
        temporaryFolder = null;
        try {
            temporaryFolder = testFolder.newFolder("tempfolder");

        }catch (IOException e){
            fail();
        }
    }

    @Before
    public void setUpStreams(){
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    public File[] createTestFiles(int numberOfFiles) {
        File[] tempFileArray = new File[numberOfFiles];
        for (int i = 0; i < numberOfFiles; i++) {
            try {
                tempFileArray[i] = File.createTempFile("tmp", null, temporaryFolder);
            }catch (IOException e){
                fail();
            }
        }
        return tempFileArray;
    }

    public File[] createTestFiles(int numberOfFiles, String endsWith) {
        File[] tempFileArray = new File[numberOfFiles];
        for (int i = 0; i < numberOfFiles; i++) {
            try {
                tempFileArray[i] = File.createTempFile("tmp", endsWith, temporaryFolder);
            }catch (IOException e){
                fail();
            }
        }
        return tempFileArray;
    }

    public File[] merge(List<File[]> listOfArrays){
        int lenght = 0;
        int counter = 0;
        for(File[] file: listOfArrays){
            lenght += file.length;
        }
        File[] mergedArray = new File[lenght];
            for(File[] fileArray: listOfArrays){
                for(File file: fileArray){
                    mergedArray[counter] = file;
                    counter++;
                }
            }
        return mergedArray;
    }

    @Test
    public void createFileFromPathTest(){
        File userFolder = new File(Paths.get("").toString());
        File pathFile = lsCommand.createFileFromPath(Paths.get(""));
        assertEquals(userFolder, pathFile);
    }

    @Test
    public void listFileInTestFolderTest() {
        File[] tempFileArray = createTestFiles(1);
        lsCommand.printFiles(temporaryFolder);
        String output = "Files in current directory:\r\n" + tempFileArray[0].getName()+"\r\n";
        assertEquals(output, outContent.toString() );
    }

    @Test
    public void listFileInTestFolderViaExecuteTest() {
        File[] tempFileArray = createTestFiles(1);
        String[] args = {"ls"};
        lsCommand.execute(args,temporaryFolder.toPath());
        String output = "Files in current directory:\r\n" + tempFileArray[0].getName()+"\r\n";
        assertEquals(output, outContent.toString() );
    }

    @Test
    public void listFilesInTestFolderTest() {
        File[] tempFileArray = createTestFiles(2);
        Arrays.sort(tempFileArray);
        lsCommand.printFiles(temporaryFolder);
        String output = "Files in current directory:\r\n" + tempFileArray[0].getName()+"\r\n" + tempFileArray[1].getName() + "\r\n";
        assertEquals(output, outContent.toString() );
    }

    @Test
    public void list1000FilesInTestFolderTest(){
        File[] tempFileArray = createTestFiles(1000);
        Arrays.sort(tempFileArray);
        lsCommand.printFiles(temporaryFolder);
        String outputString = "Files in current directory:\r\n";
        for (File file: tempFileArray) {
            outputString += (file.getName() + "\r\n");
        }
        assertEquals(outputString, outContent.toString());
    }

   @Test
   public void listFilesOnSubstringTest() {
       File testfile1 = null;
       File testfile2 = null;
       File testfile3 = null;
       try {
           testfile1 = temporaryFolder.createTempFile("tempTestFile", "txt", temporaryFolder);
           testfile2 = temporaryFolder.createTempFile("tempFile", "xml", temporaryFolder);
           testfile3 = temporaryFolder.createTempFile("testFile", "xml", temporaryFolder);
       } catch (IOException e) {
           fail();
       }
       String[] sortOn = {"test"};
       lsCommand.printFiles(temporaryFolder,sortOn);
       String outputString = "Files of chosen type in current directory:\r\n" + testfile1.getName()+ "\r\n" + testfile3.getName()+ "\r\n";
       assertEquals(outputString,outContent.toString());
   }
    @Test
    public void listFilesWithOneFilterInTestFolderTest(){
        File[] tempFileArrayTxt = createTestFiles(20,"txt");
        File[] tempFileArrayDoc = createTestFiles(5,"doc");
        ArrayList<File[]> ListOfArrays = new ArrayList<File[]>();
        ListOfArrays.add(tempFileArrayDoc);
        ListOfArrays.add(tempFileArrayTxt);
        File[] joinedTempFileArray =  merge(ListOfArrays);
        Arrays.sort(joinedTempFileArray);
        String[] sortOn = {"doc"};
        lsCommand.printFiles(temporaryFolder,sortOn);
        String outputString = "Files of chosen type in current directory:\r\n";
        Arrays.sort(tempFileArrayDoc);
        for(File file: tempFileArrayDoc){
                outputString += (file.getName() + "\r\n");
        }
        assertEquals(outputString, outContent.toString());
    }

    @Test
    public void listFilesWithThreeFiltersInTestFolderTest(){
        File[] tempFileArrayTxt = createTestFiles(20,"txt");
        File[] tempFileArrayDoc = createTestFiles(5,"doc");
        File[] tempFileArrayXml = createTestFiles(9, "xml");
        ArrayList<File[]> listOfArrays = new ArrayList<File[]>();
        listOfArrays.add(tempFileArrayTxt);
        listOfArrays.add(tempFileArrayDoc);
        listOfArrays.add(tempFileArrayXml);
        File[] joinedTempFileArray =  merge(listOfArrays);
        Arrays.sort(joinedTempFileArray);
        String[] sortOn = {"txt", "xml"};
        lsCommand.printFiles(temporaryFolder,sortOn);
        ArrayList<File[]> arraysToMergeForAssert = new ArrayList<File[]>();
        arraysToMergeForAssert.add(tempFileArrayTxt);
        arraysToMergeForAssert.add((tempFileArrayXml));
        File[] mergedArrayForAssert = merge(arraysToMergeForAssert);
        Arrays.sort(mergedArrayForAssert);
        String outputString = "Files of chosen type in current directory:\r\n";
        for(File file: mergedArrayForAssert){
            outputString += (file.getName() + "\r\n");
        }
        assertEquals(outputString, outContent.toString());
    }

    @Test
    public void listFilesOnNonExistingFilter(){
        File[] tempFileArrayTxt = createTestFiles(4,"txt");
        Arrays.sort(tempFileArrayTxt);
        String[] sortOn = {"uml"};
        lsCommand.printFiles(temporaryFolder,sortOn);
        String outputString = "Files of chosen type in current directory:\r\n";
        assertEquals(outputString, outContent.toString());
    }

    @Test
    public void listFilesOnIllegalFilter(){
        File[] tempFileArrayTxt = createTestFiles(4,"txt");
        Arrays.sort(tempFileArrayTxt);

        String[] sortOn = {"ls","?"};
        lsCommand.execute(sortOn,temporaryFolder.toPath());
        String expectedOutputString = "Filter can't contain ?\r\n";

        assertEquals(expectedOutputString, outContent.toString());
    }

    @Test
    public void listFilesOnFilterViaExecuteTest(){
        File[] tempFileArrayTxt = createTestFiles(5, "txt");
        File[] tempFileArrayUml = createTestFiles(3,"uml");
        String[] args = {"ls", "uml"};
        lsCommand.execute(args, temporaryFolder.toPath());
        Arrays.sort(tempFileArrayUml);
        String outputString = "Files of chosen type in current directory:\r\n";
        for(File file: tempFileArrayUml){
            outputString += (file.getName() + "\r\n");
        }
        assertEquals(outputString, outContent.toString());
    }

    @Test
    public void listFilesInEmptyTestFolderTest(){
        File[] temporaryFolder = createTestFiles(1);
        lsCommand.printFiles(temporaryFolder[0]);
        assertEquals("", outContent.toString());
    }

    @Test (expected = AssertionError.class)
    public void assertionTestLsExecuteTest(){
        String[] args = null;
        lsCommand.execute(args, Paths.get("user.home"));
    }

    @Test (expected = AssertionError.class)
    public void pathIsNullTest(){
        String[] args = {"ls"};
        lsCommand.execute(args, null);
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }
}

package com.commandline.commands;

import com.commandline.DirectoryManagement;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CdCommandTest {
    DirectoryManagement directoryManagement;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private CdCommand cd;
    private File temporaryFolder;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void createTemporaryFolder(){
        temporaryFolder = null;
        try {
            temporaryFolder = testFolder.newFolder("tempfolder");
            temporaryFolder.mkdir();

        }catch (IOException e){
            fail();
        }
    }

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Before
    public void createDirectoryManagement(){
        directoryManagement = new DirectoryManagement();
    }

    @Before
    public void createCdCommand(){
        cd = new CdCommand(directoryManagement);
    }

    public File createDirectoryInTestFolder(){
        File folderInTemporaryFolder = new File(temporaryFolder.getPath() + File.separator + "temporaryFolder");
        folderInTemporaryFolder.mkdir();
        return folderInTemporaryFolder;
    }


    @Test
    public void setCurrentDirectoryTest(){
        String temporaryFolderPath = temporaryFolder.getPath();
        String[] str = {"cd", temporaryFolderPath};
        Path path = Paths.get(temporaryFolderPath);
        cd.execute(str, directoryManagement.getPath());
        assertEquals(path, directoryManagement.getPath());
    }

    @Test
    public void setDirectoryWithBlankSpaceInName(){
        //Hur gör vi detta?
    }

    @Test           //Är denna testad på ett vettigt sätt?
    public void pathEndsWithSlash(){
        String str = temporaryFolder.getPath() + "/";
        cd.setCurrentPath(str);
        assertEquals(temporaryFolder.toPath(), directoryManagement.getPath());
    }

    @Test (expected=IllegalArgumentException.class)
    public void incorrectPathNameTest(){
        String str = temporaryFolder.getPath() + File.separator + "INVALID_PATH_NAME";
        cd.setCurrentPath(str);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathStartsWithBlankSpaces(){
        String str = "   " + temporaryFolder.getPath();
        cd.setCurrentPath(str);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathEndsWithBlankSpaces(){
        String str = temporaryFolder.getPath() + "  ";
        cd.setCurrentPath(str);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathContainsIllegalCharactersQuotationMarkTest(){
        String str = temporaryFolder.getPath() + "\"";
        cd.setCurrentPath(str);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathContainsIllegalCharactersQuestionMarkTest(){
        String str = temporaryFolder.getPath() + "?";
        cd.setCurrentPath(str);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathContainsIllegalCharactersStarTest(){
        String str = temporaryFolder.getPath() + "*";
        cd.setCurrentPath(str);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathContainsIllegalCharactersGreaterThanSymbolTest(){
        String str = temporaryFolder.getPath() + ">";
        cd.setCurrentPath(str);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathContainsIllegalCharactersSmallerThanSymbolTest(){
        String str = temporaryFolder.getPath() + "<";
        cd.setCurrentPath(str);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathContainsIllegalCharactersVerticalLineTest(){
        String str = temporaryFolder.getPath() + "|";
        cd.setCurrentPath(str);
    }

    @Test
    public void changeToParentDirectoryTest(){
        File file = createDirectoryInTestFolder();
        directoryManagement.setDirectory(file.toPath());
        String str = file.getParent();
        String[] args = {"cd", ".."};
        cd.execute(args, directoryManagement.getPath());
        assertEquals(Paths.get(str), directoryManagement.getPath());
    }

    @Test(expected = NullPointerException.class)
    public void noParentExistsTest(){
       File file = new File(System.getProperty("C:\\"));
       String str = file.getParent();
       cd.changeToParentDirectory(str);
    }

    @Test(expected = NullPointerException.class) //Onödig?
    public void sendInNullAsArgumentTest(){
        String str = null;
        cd.setCurrentPath(str);
    }

    @Test(expected = AssertionError.class)
    public void onlyWriteCdAndNoArgumentsTest(){
        String args[] = {"cd"};
        File file = createDirectoryInTestFolder();
        directoryManagement.setDirectory(file.toPath());
        cd.execute(args, directoryManagement.getPath());
    }

    @Test(expected = AssertionError.class)
    public void sendInNullAsPathTest(){
        String[] args = {"cd", "C://Users"};
        cd.execute(args, null);
    }

    @Test(expected = NullPointerException.class)
    public void nullArrayToExecuteMethodTest(){
        String[] args = null;
        File file = createDirectoryInTestFolder();
        directoryManagement.setDirectory(file.toPath());
        cd.execute(args, directoryManagement.getPath());
    }

    @Test(expected = AssertionError.class)
    public void emptyStringAsArgumentTest(){
        String[] str = {""};
        File file = createDirectoryInTestFolder();
        directoryManagement.setDirectory(file.toPath());
        cd.execute(str, directoryManagement.getPath());
    }

    @Test
    public void startArgumentWithBlankSpacesTest(){
        String[] str = {"cd", "   .."};
        File file = createDirectoryInTestFolder();
        directoryManagement.setDirectory(file.toPath());
        cd.execute(str, directoryManagement.getPath());
        assertEquals("Error: Illegal path name\n", outContent.toString());
    }

    @Test
    public void endArgumentWithBlankSpaces(){
        String[] str = {"cd", "..   "};
        File file = createDirectoryInTestFolder();
        directoryManagement.setDirectory(file.toPath());
        cd.execute(str, directoryManagement.getPath());
        assertEquals("Error: Illegal path name\n", outContent.toString());
    }

    @Test
    public void oneDotArgumentTest(){
        String[] str = {"cd", "."};
        Path path = temporaryFolder.toPath();
        cd.execute(str, path);
        assertEquals("Error: Illegal path name\n", outContent.toString());
    }

    @Test
    public void threeDotArgumentTest(){
        String[] str = {"cd", "..."};
        Path path = temporaryFolder.toPath();
        cd.execute(str, path);
        assertEquals("Error: Illegal path name\n", outContent.toString());
    }

    @Test
    public void noParentExistsErrorMessageTest(){
        String[] args = {"", ".."};
        cd.execute(args, Paths.get("C:\\")); //Måste göras om till temporär fil
        assertEquals("Error: No parent directory exists\r\n", outContent.toString());
    }

    @Test
    public void illegalPathNameErrorMessageTest(){
        String[] args = {"cd", "--"};
        cd.execute(args, Paths.get(temporaryFolder.getPath()));
        assertEquals("Error: Illegal path name\n", outContent.toString());
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }

}

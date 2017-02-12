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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

public class MkdirCommandTest {

    File tempFolder;
    MkdirCommand mkdirCommand;
    DirectoryManagement directoryManagement;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setupClasses(){
        mkdirCommand = new MkdirCommand();
        directoryManagement = new DirectoryManagement();
    }

    @Before
    public void createTestFolder(){
        try {
            tempFolder = testFolder.newFolder();
        }catch(IOException e){
            fail();
        }
    }

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void createDirectoryThatDoesNotExistTest(){
        String[] args = {"mkdir", "Testfolder"};
        mkdirCommand.execute(args, tempFolder.toPath());
        Path path = Paths.get(tempFolder.getPath() + File.separator + "Testfolder");
        assertTrue(Files.exists(path));
    }

    @Test
    public void createDirectoryThatAlreadyExistsTest(){
        File tempFolderInTempFolder = new File(tempFolder.getPath() + File.separator + "DoubleFolder");
        if(!tempFolderInTempFolder.mkdirs()){
            fail();
        }
        String[] args = {"mkdir", "DoubleFolder"};
        mkdirCommand.execute(args, tempFolder.toPath());
        assertEquals("File already exists\r\n", outContent.toString());
    }

    @Test
    public void createDirectoryWithEmptyStringAsNameTest(){
        assertFalse(mkdirCommand.legalDirectoryName(""));
    }

    @Test
    public void createDirectoryWithNullAsNameTest(){
        String[] args = {"mkdir", null};
        mkdirCommand.execute(args, tempFolder.toPath());
        assertEquals("No directory name was entered\r\n", outContent.toString());
    }

    @Test
    public void createDirectoryWithIllegalCharacterQuestionMarkTest(){
        String[] args = {"mkdir", "?"};
        mkdirCommand.execute(args, tempFolder.toPath());
        assertEquals("Illegal character(s) in pathname\r\n", outContent.toString());
    }

    @Test
    public void createDirectoryWithIllegalCharacterBiggerThanSignTest(){
        String[] args = {"mkdir",">"};
        mkdirCommand.execute(args, tempFolder.toPath());
        assertEquals("Illegal character(s) in pathname\r\n", outContent.toString());
    }

    @Test
    public void createDirectoryWithSpaceInFileNameTest(){
        String[] args = {"mkdir","ab cd"};
        mkdirCommand.execute(args, tempFolder.toPath());
        Path path = Paths.get(tempFolder.getPath() + File.separator + "ab cd");
        assertTrue(Files.exists(path));
    }

    @Test
    public void createDirectoryWithBlankSpacesInBeginningOfNameTest(){
        String[] args = {"mkdir","    abcd"};
        mkdirCommand.execute(args, tempFolder.toPath());
        Path path = Paths.get(tempFolder.getPath() + File.separator + "abcd");
        assertTrue(Files.exists(path));
    }

    @Test
    public void createDirectoryWithBlankSpacesInEndOfNameTest(){
        String[] args = {"mkdir","abcd    "};
        mkdirCommand.execute(args, tempFolder.toPath());
        Path path = Paths.get(tempFolder.getPath() + File.separator + "abcd");
        assertTrue(Files.exists(path));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }



}

package com.commandline;

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

import static org.junit.Assert.*;

public class DirectoryManagementTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private File temporaryFolder;
    DirectoryManagement directoryManagement;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

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

    @Test
    public void createDirectoryManagementWithEmptyConstructor(){
        directoryManagement = new DirectoryManagement();
        assertEquals(Paths.get(System.getProperty("user.home")), directoryManagement.getPath());
    }

    @Test
    public void createDirectoryManagementWithDecidedPath(){
        Path path = Paths.get(temporaryFolder.getPath());
        directoryManagement = new DirectoryManagement(path);
        assertEquals(path, directoryManagement.getPath());
    }

    @Test (expected = AssertionError.class)
    public void createWithNullPath(){
        directoryManagement = new DirectoryManagement(null);
    }

    @Test (expected = AssertionError.class)
    public void setPathToNull(){
        directoryManagement = new DirectoryManagement();
        directoryManagement.setDirectory(null);
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }
}

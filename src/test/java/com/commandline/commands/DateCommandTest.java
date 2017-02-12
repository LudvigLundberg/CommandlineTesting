package com.commandline.commands;

import com.commandline.commands.DateCommand;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class DateCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void printingDateTest(){
        DateCommand date = new DateCommand();
        Date d = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        String todaysDate = dateFormat.format(d);
        String[] command = {"date"};
        date.execute(command,null);
        assertEquals(todaysDate+ "\r\n", outContent.toString());
    }
    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }
}

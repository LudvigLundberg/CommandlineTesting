package com.commandline;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CommandlineTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private Commandline commandline;
    private String input;
    private String expected;

    public CommandlineTest(String input, String expected){
        this.input = input;
        this.expected = expected;
    }

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();


    public File setUpTestFolderWithAFile(){
        File temporaryFolder = null;
        try {
            temporaryFolder = testFolder.newFolder("tempfolder");
        }catch (IOException e){
            fail();
        }
        return temporaryFolder;
    }

    @Before
    public void setUpStreams(){
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Parameters
    public static List<Object[]> testData(){
        return Arrays.asList(new Object[][]{
                {"cd", "cd"},
                {"ls", "ls"},
                {"mkdir", "mkdir"},
                {"date", "date"},
                {"diff", "diff"}
        });
    }

    @Before
    public void setUpCommandline(){
        commandline = new Commandline();
    }

    @Test
    public void differentCommandsGetInputTest(){

        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
//        commandline.userCommand();
        assertEquals(expected, commandline.getInput(System.in));
    }

    //Testa: vad som händer om man inte skriver nånting i kommandotolken

//    @Test
//    public void switchCommandLsTest(){
//        String input = "ls";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//        commandline.userCommand();
//    }
//
//    @Test
//    public void switchCommandMkdirTest() {
//        String input = "mkdir";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//        commandline.userCommand();
//    }
//
//    @Test
//    public void switchCommandDateTest(){
//        String input = "date";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//        commandline.userCommand();
//    }
//

    //@Test
    public void nullCommandTest(){ //vad katten är det här?
        String input = null;
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(null);
    }

    @Test
    public void emptyStringCommandTest(){
        String input = "";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
    }

    @Test
    public void stringSplitFirstPartTest(){
        String[] arr = commandline.stringSplit("cd file.txt");
        assertEquals("cd", arr[0]);
    }

    @Test
    public void stringSplitSecondPartTest(){
        String[] arr = commandline.stringSplit("cd file2.txt");
        assertEquals("file2.txt", arr[1]);
    }

    @Test
    public void emptyStringSplitTest(){
        String arr[] = commandline.stringSplit("");
        assertEquals("", arr[0]);
    }

    @Test
    public void notEnoughArgumentsReturnFalseTest(){
        String[] arr = {"cd"};
        assertFalse(commandline.enoughArgumentsInArray(arr, 2));
    }

    @Test
    public void enoughArgumentsReturnTrueTest(){
        String[] arr = {"cd", "arg2"};
        assertTrue(commandline.enoughArgumentsInArray(arr, 2));
    }

    @Test
    public void noArgumentDiffTest(){
        String[] args = {};
        commandline.diff(args);
        assertEquals("Error: not enough arguments\r\n", outContent.toString());
    }

    @Test(expected = NullPointerException.class)
    public void nullArgumnetDiffTest(){
        String[] args = null;
        commandline.diff(args);
    }

    @Test
    public void onlyOneArgumentDiffTest(){
        String[] args = {"arg"};
        commandline.diff(args);
        assertEquals("Error: not enough arguments\r\n", outContent.toString());
    }

    @Test(expected = NullPointerException.class)
    public void oneArgumentAndNullDiffTest(){
        String[] args = {"arg", null};
        commandline.diff(args);
    }

    @Test //ej klar! Hur ska asserten se ut?
    public void enoughArgumentDiffTest(){
        String[] args = {"arg1", "arg2"};
        commandline.diff(args);

    }

   // @Test
/*    public void severalCallsAndExitsTest(){

        String input = "ls";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        String input1 = "mkdir";
        InputStream in1 = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in1);


        String input2 = "mkdir";
        InputStream in2 = new ByteArrayInputStream(input2.getBytes());
        System.setIn(in2);


        String input3 = "exit";
        InputStream in3 = new ByteArrayInputStream(input3.getBytes());
        System.setIn(in3);
    }*/

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }
}
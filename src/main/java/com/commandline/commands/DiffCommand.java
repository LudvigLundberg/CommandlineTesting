package com.commandline.commands;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiffCommand implements Command{

    protected List<String> stringLineListFile1;
    protected List<String> stringLineListFile2;
    private File file1;
    private File file2;

    @Override
    public void execute(String[] fileNames, Path currentPath) {
        assert (currentPath != null && fileNames.length>1);
            checkIfTextFiles(fileNames);
            if(createFiles(fileNames, currentPath)){
                listFileToLines(file1, file2);
            }else{
                System.out.println("File(s) do not exist");
            }
            lookAfterDifferences();
    }


    public boolean checkIfTextFiles(String[] args){
            String filename1 = args[0];
            String filename2 = args[1];
            if(!filename1.endsWith(".txt") || !filename2.endsWith(".txt")){
                throw new IllegalArgumentException("Error: not a text file\n");
            }
            return true;
    }

    public boolean checkIfFilenameIsNotReserved(String filename){
        return (!filename.equals("con.txt") && !filename.equals("prn.txt"));
    }

    public boolean checkIfFilenameContainsIllegalCharacter(String filename){
        Pattern pattern = Pattern.compile("[<>:\"\\\\/|?*]");
        Matcher matcher = pattern.matcher(filename);
        return !matcher.find();
    }



    public boolean createFiles(String[] args, Path currentPath) {
        for (String arg : args) {
            if (!checkIfFilenameContainsIllegalCharacter(arg) || !checkIfFilenameIsNotReserved(arg)) {
                throw new IllegalArgumentException("Error: Illegal character(s) in file name");
            }
        }
        String filename1 = args[0];
        String filename2 = args[1];
        file1 = new File(currentPath.toString() + File.separator + filename1);
        file2 = new File(currentPath.toString() + File.separator + filename2);
        return (file1.exists() && file2.exists());
    }


    public void listFileToLines(File file1, File file2){
        try {
            String currentLine;
            stringLineListFile1 = new ArrayList<>();
            stringLineListFile2 = new ArrayList<>();
            BufferedReader bufferedReader1 = new BufferedReader(new FileReader(file1));
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(file2));
            while ((currentLine = bufferedReader1.readLine()) != null){
                stringLineListFile1.add(currentLine);
            }
            while ((currentLine = bufferedReader2.readLine()) != null){
                stringLineListFile2.add(currentLine);
            }
        }catch (IOException e){
            System.out.println("Error: IOException");
        }
    }

    public void lookAfterDifferences(){
       try {
            List<String> longer;
            List<String> shorter;
            if(stringLineListFile1.size()< stringLineListFile2.size()){
                longer = stringLineListFile2;
                shorter = stringLineListFile1;
            }else{
                longer = stringLineListFile1;
                shorter = stringLineListFile2;
            }
            for(int i = 0; i < longer.size(); i++){
                if(i>=shorter.size()){
                    System.out.println("Diff on row " + i + ": "+ longer.get(i) + " vs");
                }
                else if(!stringLineListFile1.get(i).equals(stringLineListFile2.get(i))){
                    System.out.println("Diff on row " + i + ": " + longer.get(i) + " vs " + shorter.get(i));
                }
            }
        }catch(Exception e){
            System.out.println("Error");
        }
    }




}

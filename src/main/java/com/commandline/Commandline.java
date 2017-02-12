package com.commandline;

import com.commandline.commands.*;

import java.io.InputStream;
import java.util.Scanner;


public class Commandline {
    private DirectoryManagement directoryManagement;
    private boolean runProgram = true;

    public Commandline() {
        directoryManagement = new DirectoryManagement();
    }

    public static void main(String[] args) {
        Commandline c = new Commandline();
        c.run();
    }

    public void run(){
        while(runProgram){
            userCommand();
        }
    }

    public String getInput(InputStream inputStream){
        Scanner scanner = new Scanner(inputStream);
        String command = scanner.nextLine();
        return command;
    }

    protected boolean enoughArgumentsInArray(String[] args, int requiredArguments){
        return args.length == requiredArguments;
    }

    public void userCommand(){
        System.out.print(directoryManagement.getPath().toString() + ": ");
        String command = getInput(System.in);
        String[] commandSplitArray = stringSplit(command);
        switch(commandSplitArray[0]) {
            case "cd":
                changeDirectory(commandSplitArray);
                break;
            case "ls":
                list(commandSplitArray);
                break;
            case "mkdir":
                makeDirectory(commandSplitArray);
                break;
            case "diff":
                diff(commandSplitArray);
                break;
            case "date":
                date(commandSplitArray);
                break;
            case "exit":
                runProgram = false;
                break;
            default:
                System.out.println("Command: " + command + " is not defined");
                break;
        }
    }

    public String[] stringSplit(String stringToSplit){
        stringToSplit = stringToSplit.trim();
        String[] splitArray = stringToSplit.split(" ",2);
        return splitArray;

    }
    public void changeDirectory(String [] args){
        if(enoughArgumentsInArray(args, 2)) {
            Command cd = new CdCommand(directoryManagement);
            cd.execute(args, directoryManagement.getPath());
        }
    }

    public void list(String [] args){
            Command ls = new LsCommand();
            ls.execute(args, directoryManagement.getPath());
            }

    public void makeDirectory(String [] args){
        if(enoughArgumentsInArray(args, 2)) {
            Command mkdir = new MkdirCommand();
            mkdir.execute(args, directoryManagement.getPath());
        }
    }

    public void diff(String[] args){
        String[] fileNames;
        if(args.length > 1) {
            fileNames = stringSplit(args[1]);
             if(enoughArgumentsInArray(fileNames, 2)){
                Command diffCommand = new DiffCommand();
                diffCommand.execute(fileNames, directoryManagement.getPath());
                }
                else{
                 System.out.println("Error: not enough files in arguments");
             }

        }else{
            System.out.println("Error: not enough arguments");
        }
    }

    public void date(String [] args){
            Command d = new DateCommand();
            d.execute(args, directoryManagement.getPath());
    }
}

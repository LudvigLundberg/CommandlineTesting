package com.commandline.commands;

import java.io.File;
import java.nio.file.Path;

public class LsCommand implements Command {


    @Override
    public void execute(String[] args, Path currentPath) {
        assert (currentPath != null && args != null);
        if(args.length>1){
            String[] filter = stringSplit(args[1]);
            File file = createFileFromPath(currentPath);
            if(checkForIllegalChars(filter)!= false){
                printFiles(file, filter);
            }
        }
        else {
            File file = createFileFromPath(currentPath);
            printFiles(file);
        }
    }

    public String[] stringSplit(String stringToSplit){
        stringToSplit = stringToSplit.trim();
        String[] splitArray = stringToSplit.split(" ");
        return splitArray;
    }

    public boolean checkForIllegalChars(String[] filter){
        String[] illegalChars = {"#", "<", "$", "+", "%", ">", "!", "`", "&", "*", "‘", "|", "{", "?", "“", "=", "}", "/", ":","@", " "};
        for(String s: filter){
            for(String illegal:illegalChars){
                if(s.contains(illegal)){
                    System.out.println("Filter can't contain" + " " + illegal);
                    return false;
                }
            }
        }
        return true;
    }

    public File createFileFromPath(Path currentPath){
        File file = new File(currentPath.toString());
        return file;
    }

    public void printFiles(File inputFile, String[] filters){
        File[] files = inputFile.listFiles();
        if (files!=null) {
            System.out.println("Files of chosen type in current directory:");
            for (File file : files) {
                for (String filter : filters) {
                    if(file.getName().toLowerCase().contains(filter.toLowerCase()))
                        System.out.println(file.getName());
                }
            }
        }
    }
    public void printFiles(File file){
        File[] files = file.listFiles();
        if (files!=null) {
            System.out.println("Files in current directory:");
            for (File fileName : files)
                System.out.println(fileName.getName());
        }
    }
}

package com.commandline.commands;


import java.io.File;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MkdirCommand implements Command {


    @Override
    public void execute(String[] args, Path currentPath) {
        try {
            assert (currentPath != null);
            if(args[1].startsWith(" ") || args[1].endsWith(" ")){
                args[1] = args[1].trim();
            }
            if (legalDirectoryName(args[1])) {
                if (!createDirectory(args[1], currentPath.toString())) {
                    System.out.println("File already exists");
                }
                else{
                    System.out.println("Directory: " + args[1] + " created");
                }
            } else {
                System.out.println("Illegal character(s) in pathname");
            }
        }catch(NullPointerException e){
            System.out.println("No directory name was entered");
        }
    }

    public boolean legalDirectoryName(String filename){
        if(!filename.equals("")) {
            Pattern pattern = Pattern.compile("[<>:\"\\/|?*]");
            Matcher matcher = pattern.matcher(filename);
            return !matcher.find();
        }else{
            return false;
        }
    }

    public Boolean createDirectory(String filename, String path) {
        File f = new File(path + File.separator + filename);
        return f.mkdir();
    }
}

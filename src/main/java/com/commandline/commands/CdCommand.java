package com.commandline.commands;

import com.commandline.SetDirectoryInterface;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.*;


public class CdCommand implements Command {

    private SetDirectoryInterface setDirectoryInterface;

    public CdCommand(SetDirectoryInterface setDirectoryInterface){
        this.setDirectoryInterface = setDirectoryInterface;
    }

    @Override
    public void execute(String[] args, Path currentPath){
        assert (currentPath != null && args.length>1);
        if (args[1].equals("..")) {
            try {
                changeToParentDirectory(currentPath.toString());
            } catch (NullPointerException exception) {
                System.out.println("Error: No parent directory exists");
            }
        }else
            try{
                if(args[1].endsWith(" ")){
                    throw new IllegalArgumentException("Error: Illegal path name\n");
                }
                setCurrentPath(args[1]);
            } catch (IllegalArgumentException exception) {
                System.out.print(exception.getMessage());
            }
        }


    public void setCurrentPath(String arg) {
        try {
            Path newPath;
            if (arg.startsWith(" ") || arg.endsWith(" ")) {
                throw new IllegalArgumentException("Error: Illegal path name\n");
            } else {
                newPath = Paths.get(arg);
            }
            if (arg.equals("") || arg.equals(".") || !Files.exists(newPath)) {
                throw new IllegalArgumentException("Error: Illegal path name\n");
            }
             setDirectoryInterface.setDirectory(newPath);
        }catch(InvalidPathException e){
            throw new IllegalArgumentException("Error: Illegal characters in path name\n");
        }
    }

    public void changeToParentDirectory(String str){
        File file = new File(str);
        String newString = file.getParent();
            setCurrentPath(newString);
    }
}


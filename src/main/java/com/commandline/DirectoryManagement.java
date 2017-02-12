package com.commandline;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryManagement implements SetDirectoryInterface{

    private Path currentPath;

    public DirectoryManagement(Path path){
        assert(path != null);
        this.currentPath = path;
    }

    public DirectoryManagement(){
        this(Paths.get(System.getProperty("user.home")));
    }

    public Path getPath(){
        return currentPath;
    }

    @Override
    public void setDirectory(Path path) {
        assert(path != null);
        currentPath = path;
    }
}





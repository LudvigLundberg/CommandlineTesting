package com.commandline.commands;

import java.io.IOException;
import java.nio.file.Path;

public interface Command {

    void execute(String[] args, Path currentPath);
}

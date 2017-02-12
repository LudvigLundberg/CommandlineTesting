package com.commandline.commands;

import java.util.List;

public class ExtendsDiffCommand extends DiffCommand {

    public List getListForFile1FromDiffCommand(){
        return stringLineListFile1;
    }

    public List getListForFile2FromDiffCommand(){
        return stringLineListFile2;
    }
}

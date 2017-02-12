package com.commandline.commands;
import java.nio.file.Path;
import java.util.Date;
import java.text.*;



public class DateCommand implements Command {

    @Override
    public void execute(String[] args, Path currentPath) {
        printCurrentDate();
    }

    public void printCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        System.out.println(currentDate);
    }


}

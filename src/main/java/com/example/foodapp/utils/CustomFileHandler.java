package com.example.foodapp.utils;

import com.example.foodapp.utils.NewLineFmt;

import java.io.File;
import java.util.logging.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;

/*
public class CustomFileHandler extends FileHandler {
    private String currentLogFileName;

    private static final String LOG_DIRECTORY = path();

    public static String path() {

        String localpath = "";

        try {
            File directory = new File("./Foodlogs/");
            localpath = directory.getCanonicalPath();
            if (!directory.exists()) {
                if(directory.mkdir()){
                    System.out.println("Directory created: " + directory.getCanonicalPath());
                } else {
                    System.err.println("Failed to create directory: " + directory.getCanonicalPath());
                }
            }
            localpath = directory.getCanonicalPath();
            System.out.println("Absolute name " +  localpath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localpath;
    }


    public CustomFileHandler() throws IOException, SecurityException {
        super(getLogFileName(), true);
        setFormatter(new NewLineFmt());
    }

    private static String getLogFileName() {
        LocalDate now = LocalDate.now();
        return LOG_DIRECTORY + File.separator + "papss-" + now.format(DateTimeFormatter.ISO_LOCAL_DATE) + ".log";
//        return "Admin Service-" + now.format(DateTimeFormatter.ISO_LOCAL_DATE) + ".log";
    }

    private synchronized void updateLogFile() throws IOException {
        String newLogFileName = getLogFileName();
        if (!newLogFileName.equals(currentLogFileName)) {
            close();
            setOutputStream(new java.io.FileOutputStream(newLogFileName, true));
            currentLogFileName = newLogFileName;
        }
    }

    @Override
    public synchronized void publish(LogRecord record) {
        try {
            updateLogFile();
            super.publish(record);
            flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/
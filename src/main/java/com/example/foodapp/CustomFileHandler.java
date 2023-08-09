package com.example.foodapp;

import java.util.logging.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;

public class CustomFileHandler extends FileHandler {
    private String currentLogFileName;

    public CustomFileHandler() throws IOException, SecurityException {
        super(getLogFileName(), true);
        setFormatter(new SimpleFormatter());
    }

    private static String getLogFileName() {
        LocalDate now = LocalDate.now();
        return "Admin Service-" + now.format(DateTimeFormatter.ISO_LOCAL_DATE) + ".log";
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
}


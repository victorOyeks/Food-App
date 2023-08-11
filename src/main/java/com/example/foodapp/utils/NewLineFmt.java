package com.example.foodapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class NewLineFmt extends Formatter {
//    @Override
//    public String format(LogRecord record) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(record.getMessage()).append(System.lineSeparator());
//        return sb.toString();
//    }

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();

        // Append date and time
        sb.append(dateFormat.format(new Date(record.getMillis()))).append(" ");

        // Append class name (if available)
        if (record.getSourceClassName() != null) {
            sb.append(record.getSourceClassName()).append(" ");
        }

        // Append method name (if available)
        if (record.getSourceMethodName() != null) {
            sb.append(record.getSourceMethodName()).append(" ");
        }

        // Append log level
        sb.append(record.getLevel()).append(": ");

        // Append log message
        sb.append(formatMessage(record)).append(System.lineSeparator());

        // Append an empty line
        sb.append(System.lineSeparator());

        return sb.toString();
    }
}

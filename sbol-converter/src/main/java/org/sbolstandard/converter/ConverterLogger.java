package org.sbolstandard.converter;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple logger for converter operations
 */
public class ConverterLogger {
    
    private final PrintStream out;
    private final PrintStream err;
    private boolean enableTimestamp;
    private boolean enableDebug;
    
    /**
     * Creates a new SimpleLogger with default System.out and System.err
     */
    public ConverterLogger() {
        this(System.out, System.err);
    }
    
    /**
     * Creates a new SimpleLogger with custom output streams
     * @param out Output stream for info messages
     * @param err Output stream for error messages
     */
    public ConverterLogger(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
        this.enableTimestamp = true;
        this.enableDebug = false;
    }
    
    /**
     * Enable or disable timestamps in log messages
     * @param enable true to enable timestamps, false to disable
     */
    public void setTimestampEnabled(boolean enable) {
        this.enableTimestamp = enable;
    }
    
    /**
     * Enable or disable debug messages
     * @param enable true to enable debug messages, false to disable
     */
    public void setDebugEnabled(boolean enable) {
        this.enableDebug = enable;
    }
    
    /**
     * Log an info message
     * @param message The message to log
     */
    public void info(String message) {
        log(out, "INFO", message);
    }
    
    /**
     * Log a warning message
     * @param message The message to log
     */
    public void warn(String message) {
        log(out, "WARN", message);
    }
    
    /**
     * Log an error message
     * @param message The message to log
     */
    public void error(String message) {
        log(err, "ERROR", message);
    }
    
    /**
     * Log an error message with exception
     * @param message The message to log
     * @param throwable The exception to log
     */
    public void error(String message, Throwable throwable) {
        log(err, "ERROR", message + ": " + throwable.getMessage());
        if (enableDebug) {
            throwable.printStackTrace(err);
        }
    }   
    
    /**
     * Log a debug message
     * @param message The message to log
     */
    public void debug(String message) {
        if (enableDebug) {
            log(out, "DEBUG", message);
        }
    }
    
    /**
     * Internal log method
     */
    private void log(PrintStream stream, String level, String message) {
        StringBuilder sb = new StringBuilder();
        
        if (enableTimestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sb.append("[").append(sdf.format(new Date())).append("] ");
        }
        
        sb.append("[").append(level).append("] ");
        sb.append(message);
        
        stream.println(sb.toString());
    }
}

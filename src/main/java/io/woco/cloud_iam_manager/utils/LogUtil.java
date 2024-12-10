package io.woco.cloud_iam_manager.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.Util;
import org.springframework.boot.logging.LogLevel;

/**
 * The type Log util.
 */
@Slf4j
public class LogUtil {
    /**
     * Log a message based on log level and class which called for the log.
     *
     * @param logLevel the log level
     * @param message  the message
     */
    private static void log(LogLevel logLevel, String message, Class loggedClass) {
        loggedClass = (loggedClass != null) ? loggedClass : Util.getCallingClass();

        switch (logLevel) {
            case DEBUG:
                log.debug(message);
                break;
            case INFO:
                log.info(message);
                break;
            case TRACE:
                log.trace(message);
                break;
            case WARN:
                log.warn(message);
                break;
            case ERROR:
            case FATAL:
                log.error(message);
                break;
        }
    }

    public static void debug(String message) {
        log(LogLevel.DEBUG, message, null);
    }

    public static void info(String message) {
        log(LogLevel.INFO, message, null);
    }

    public static void trace(String message) {
        log(LogLevel.TRACE, message, null);
    }

    public static void warn(String message) {
        log(LogLevel.WARN, message, null);
    }

    public static void error(String message) {
        log(LogLevel.ERROR, message, null);
    }

    public static void fatal(String message) {
        log(LogLevel.FATAL, message, null);
    }

    public static void debug(String message, Class<?> loggedClass) {
        log(LogLevel.DEBUG, message, loggedClass);
    }

    public static void info(String message, Class<?> loggedClass) {
        log(LogLevel.INFO, message, loggedClass);
    }

    public static void trace(String message, Class<?> loggedClass) {
        log(LogLevel.TRACE, message, loggedClass);
    }

    public static void warn(String message, Class<?> loggedClass) {
        log(LogLevel.WARN, message, loggedClass);
    }

    public static void error(String message, Class<?> loggedClass) {
        log(LogLevel.ERROR, message, loggedClass);
    }

    public static void fatal(String message, Class<?> loggedClass) {
        log(LogLevel.FATAL, message, loggedClass);
    }
}

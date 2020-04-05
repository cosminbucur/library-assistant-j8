package com.bucur.library.exceptions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.Thread.UncaughtExceptionHandler;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(DefaultExceptionHandler.class.getName());

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LOGGER.log(Level.ERROR, "Exception occurred {}", ex);
    }
}

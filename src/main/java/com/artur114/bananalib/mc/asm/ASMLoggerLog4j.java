package com.artur114.bananalib.mc.asm;

import com.artur114.bananalib.asm.util.IASMLogger;
import org.apache.logging.log4j.Logger;

public class ASMLoggerLog4j implements IASMLogger {
    private final Logger log;

    public ASMLoggerLog4j(Logger log) {
        this.log = log;
    }

    @Override
    public void warn(String log) {
        this.log.warn(log);
    }

    @Override
    public void info(String log) {
        this.log.info(log);
    }

    @Override
    public void error(String log) {
        this.log.error(log);
    }

    @Override
    public void debug(String log) {
        this.log.debug(log);
    }

    @Override
    public void warn(String log, Object arg) {
        this.log.warn(log, arg);
    }

    @Override
    public void info(String log, Object arg) {
        this.log.info(log, arg);
    }

    @Override
    public void error(String log, Object arg) {
        this.log.error(log, arg);
    }

    @Override
    public void debug(String log, Object arg) {
        this.log.debug(log, arg);
    }

    @Override
    public void warn(String log, Object... args) {
        this.log.warn(log, args);
    }

    @Override
    public void info(String log, Object... args) {
        this.log.info(log, args);
    }

    @Override
    public void error(String log, Object... args) {
        this.log.error(log, args);
    }

    @Override
    public void debug(String log, Object... args) {
        this.log.debug(log, args);
    }

    @Override
    public void warn(String log, Object arg, Object arg1) {
        this.log.warn(log, arg, arg1);
    }

    @Override
    public void info(String log, Object arg, Object arg1) {
        this.log.info(log, arg, arg1);
    }

    @Override
    public void error(String log, Object arg, Object arg1) {
        this.log.error(log, arg, arg1);
    }

    @Override
    public void debug(String log, Object arg, Object arg1) {
        this.log.debug(log, arg, arg1);
    }
}
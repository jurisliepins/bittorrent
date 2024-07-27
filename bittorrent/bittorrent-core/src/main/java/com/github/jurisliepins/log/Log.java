package com.github.jurisliepins.log;

import org.slf4j.LoggerFactory;

public final class Log {
    private Log() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static void debug(final Class<?> clazz, final String var1) {
        LoggerFactory.getLogger(clazz).debug(var1);
    }

    public static void debug(final Class<?> clazz, final String var1, final Object var2) {
        LoggerFactory.getLogger(clazz).debug(var1, var2);
    }

    public static void debug(final Class<?> clazz, final String var1, final Object var2, final Object var3) {
        LoggerFactory.getLogger(clazz).debug(var1, var2, var3);
    }

    public static void debug(final Class<?> clazz, final String var1, final Object... var2) {
        LoggerFactory.getLogger(clazz).debug(var1, var2);
    }

    public static void debug(final Class<?> clazz, final String var1, final Throwable var2) {
        LoggerFactory.getLogger(clazz).debug(var1, var2);
    }

    public static void info(final Class<?> clazz, final String var1) {
        LoggerFactory.getLogger(clazz).info(var1);
    }

    public static void info(final Class<?> clazz, final String var1, final Object var2) {
        LoggerFactory.getLogger(clazz).info(var1, var2);
    }

    public static void info(final Class<?> clazz, final String var1, final Object var2, final Object var3) {
        LoggerFactory.getLogger(clazz).info(var1, var2, var3);
    }

    public static void info(final Class<?> clazz, final String var1, final Object... var2) {
        LoggerFactory.getLogger(clazz).info(var1, var2);
    }

    public static void info(final Class<?> clazz, final String var1, final Throwable var2) {
        LoggerFactory.getLogger(clazz).info(var1, var2);
    }

    public static void warn(final Class<?> clazz, final String var1) {
        LoggerFactory.getLogger(clazz).warn(var1);
    }

    public static void warn(final Class<?> clazz, final String var1, final Object var2) {
        LoggerFactory.getLogger(clazz).warn(var1, var2);
    }

    public static void warn(final Class<?> clazz, final String var1, final Object... var2) {
        LoggerFactory.getLogger(clazz).warn(var1, var2);
    }

    public static void warn(final Class<?> clazz, final String var1, final Object var2, final Object var3) {
        LoggerFactory.getLogger(clazz).warn(var1, var2, var3);
    }

    public static void warn(final Class<?> clazz, final String var1, final Throwable var2) {
        LoggerFactory.getLogger(clazz).warn(var1, var2);
    }

    public static void error(final Class<?> clazz, final String var1) {
        LoggerFactory.getLogger(clazz).error(var1);
    }

    public static void error(final Class<?> clazz, final String var1, final Object var2) {
        LoggerFactory.getLogger(clazz).error(var1, var2);
    }

    public static void error(final Class<?> clazz, final String var1, final Object var2, final Object var3) {
        LoggerFactory.getLogger(clazz).error(var1, var2, var3);
    }

    public static void error(final Class<?> clazz, final String var1, final Object... var2) {
        LoggerFactory.getLogger(clazz).error(var1, var2);
    }

    public static void error(final Class<?> clazz, final String var1, final Throwable var2) {
        LoggerFactory.getLogger(clazz).error(var1, var2);
    }

}

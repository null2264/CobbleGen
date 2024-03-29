package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.CobbleGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CGLog {
    public static final Logger LOG = LoggerFactory.getLogger("CobbleGen");
    private static final String prefix = "[CobbleGen] ";

    public static void debug(String s, String... a) {
        if (!CobbleGen.META_CONFIG.debugLog) return;

        StringBuilder rt = new StringBuilder(prefix + s);
        for (String s1 : a) {
            rt.append(" ").append(s1);
        }
        LOG.info(rt.toString());
    }

    public static void info(String s, String... a) {
        StringBuilder rt = new StringBuilder(prefix + s);
        for (String s1 : a) {
            rt.append(" ").append(s1);
        }
        LOG.info(rt.toString());
    }

    public static void warn(String s, String... a) {
        StringBuilder rt = new StringBuilder(prefix + s);
        for (String s1 : a) {
            rt.append(" ").append(s1);
        }
        LOG.warn(rt.toString());
    }

    public static void error(String s, String... a) {
        StringBuilder rt = new StringBuilder(prefix + s);
        for (String s1 : a) {
            rt.append(" ").append(s1);
        }
        LOG.error(rt.toString());
    }

    public static void error(String prefix, Throwable t) {
        String builder = prefix + errorString(t);
        error(builder);
    }

    public static void error(Throwable t) {
        error(errorString(t));
    }

    public static String errorString(Throwable t) {
        StringBuilder builder = new StringBuilder();
        builder.append(t.toString());

        StackTraceElement[] trace = t.getStackTrace();
        for (StackTraceElement traceElement : trace)
            builder.append("\n\tat ").append(traceElement);
        return builder.toString();
    }
}
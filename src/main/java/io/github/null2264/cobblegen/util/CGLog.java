package io.github.null2264.cobblegen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CGLog {
    public static final Logger LOG = LoggerFactory.getLogger("CobbleGen");
    private static final String prefix = "[CobbleGen] ";

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
}
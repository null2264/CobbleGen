package io.github.null2264.cobblegen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CGLog {
    public static final Logger LOG = LoggerFactory.getLogger("CobbleGen");
    private static final String prefix = "[CobbleGen] ";

    public static void info(String s) {
        LOG.info(prefix + s);
    }

    public static void warn(String s) {
        LOG.warn(prefix + s);
    }

    public static void error(String s) {
        LOG.error(prefix + s);
    }
}
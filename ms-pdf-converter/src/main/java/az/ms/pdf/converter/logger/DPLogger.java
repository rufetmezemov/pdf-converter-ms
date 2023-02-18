package az.ms.pdf.converter.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.web.util.HtmlUtils;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Digital platform safe logger
 */
public class DPLogger {

    private final Logger logger;

    private DPLogger(Logger logger) {
        this.logger = logger;
    }

    public static DPLogger getLogger(Class<?> clazz) {
        Logger logger = LoggerFactory.getLogger(clazz);
        return new DPLogger(logger);
    }

    private Object[] filterValues(Object... argArray) {
        return Arrays.stream(argArray)
                .map(a -> a == null ? null : filterValue(a)).toArray();
    }

    private Object filterValue(Object a) {
        return a instanceof Throwable ? ((((Throwable) a).getMessage() != null &&
                (((Throwable) a).getMessage().contains("\r") ||
                        ((Throwable) a).getMessage().contains("\n"))) ? new Exception("CRLF Injection " +
                ((Throwable) a).getMessage()
                        .replace("\r", "&cr;")
                        .replace("\n", "&lf;")) : a) : HtmlUtils.htmlEscape(
                EnumSet.allOf(FilterPattern.class)
                        .stream()
                        .reduce(a,
                                (o, f) -> o.toString().replaceAll(f.getRegexp(), f.getMask()),
                                (s1, s2) -> s1.toString() + s2.toString())
                        .toString());
    }

    public String getName() {
        return logger.getName();
    }

    public void trace(String s, Object... args) {
        if (logger.isTraceEnabled()) {
            logger.trace(s, filterValues(args));
        }
    }

    public void trace(Marker marker, String s, Object... args) {
        if (logger.isTraceEnabled(marker)) {
            logger.trace(marker, s, filterValues(args));
        }
    }

    public void debug(String s, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(s, filterValues(args));
        }
    }

    public void debug(Marker marker, String s, Object... args) {
        if (logger.isDebugEnabled(marker)) {
            logger.debug(marker, s, filterValues(args));
        }
    }

    public void info(String s, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(s, filterValues(args));
        }
    }

    public void info(Marker marker, String s, Object... args) {
        if (logger.isInfoEnabled(marker)) {
            logger.info(marker, s, filterValues(args));
        }
    }

    public void warn(String s, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(s, filterValues(args));
        }
    }

    public void warn(Marker marker, String s, Object... args) {
        if (logger.isWarnEnabled(marker)) {
            logger.warn(marker, s, filterValues(args));
        }
    }

    public void error(String s, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(s, filterValues(args));
        }
    }

    public void error(Marker marker, String s, Object... args) {
        if (logger.isErrorEnabled(marker)) {
            logger.error(marker, s, filterValues(args));
        }
    }

    private enum FilterPattern {
        NAME_SURNAME("[A-Z]+[ ]+[A-Z]+[ ]?+[A-Z]?", "**** *******"),
        MOBILE_NUMBER("\\b(\\+?\\d{1,3}[- ]?)?\\d{9,10}\\b", "**********"),
        TIN("\\b\\d{10}\\b", "**********"),
        FIN("\\b([A-Z]+\\d[A-Z\\d]+)|(\\d+[A-Z][A-Z\\S]+)\\b", "*******"),
        EMAIL("\\b([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})\\b", "*@*.*"),
        AMOUNT_AND_IP("\\b[0-9]+\\.[0-9]+\\b", "*.*"),
        CARD_NUMBER("\\b\\d{16}\\b", "****************");

        private final String regexp;
        private final String mask;

        FilterPattern(String regexp, String mask) {
            this.regexp = regexp;
            this.mask = mask;
        }

        private String getRegexp() {
            return regexp;
        }

        private String getMask() {
            return mask;
        }
    }
}
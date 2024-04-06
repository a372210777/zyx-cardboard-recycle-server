package cn.com.qjun.cardboard.utils;

import com.google.common.base.Preconditions;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期时间工具
 *
 * @author RenQiang
 * @date 2021/11/15 0:56
 */
public class DateTimeUtils {
    /**
     * 定义默认日期、时间格式
     */
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd",
            TIME_FORMAT_PATTERN = "HH:mm:ss",
            DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 用于格式化Date的Formatter
     */
    private static final ThreadLocal<Map<String, SimpleDateFormat>> DATE_SDF_MAP = ThreadLocal.withInitial(() -> {
        HashMap<String, SimpleDateFormat> dateSdfMap = new HashMap<>(16);
        dateSdfMap.put(DATE_FORMAT_PATTERN, new SimpleDateFormat(DATE_FORMAT_PATTERN));
        dateSdfMap.put(DATE_TIME_FORMAT_PATTERN, new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN));
        dateSdfMap.put(TIME_FORMAT_PATTERN, new SimpleDateFormat(TIME_FORMAT_PATTERN));
        return dateSdfMap;
    });
    /**
     * 用于格式化Local日期、时间的Formatter
     */
    private static final ConcurrentHashMap<String, DateTimeFormatter> DTF_MAP;

    static {
        DTF_MAP = new ConcurrentHashMap<>(16);
        DTF_MAP.put(DATE_FORMAT_PATTERN, DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
        DTF_MAP.put(DATE_TIME_FORMAT_PATTERN, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN));
        DTF_MAP.put(TIME_FORMAT_PATTERN, DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN));
    }

    /**
     * LocalDate 转 Date
     *
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime 转 Date
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 以默认格式格式化日期
     * <p>
     * See {@link #DATE_FORMAT_PATTERN}
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return getSdfByPattern(DATE_FORMAT_PATTERN).format(date);
    }

    /**
     * 以默认格式解析日期
     * <p>
     * See {@link #DATE_FORMAT_PATTERN}
     *
     * @param text
     * @return
     */
    @SneakyThrows
    public static Date parseDate(String text) {
        return getSdfByPattern(DATE_FORMAT_PATTERN).parse(text);
    }

    /**
     * 以默认格式格式化时间
     * <p>
     * See {@link #TIME_FORMAT_PATTERN}
     *
     * @param datetime
     * @return
     */
    public static String formatTime(Date datetime) {
        return getSdfByPattern(TIME_FORMAT_PATTERN).format(datetime);
    }

    /**
     * 以默认格式格式化日期时间
     * <p>
     * See {@link #DATE_TIME_FORMAT_PATTERN}
     *
     * @param datetime
     * @return
     */
    public static String formatDateTime(Date datetime) {
        return getSdfByPattern(DATE_TIME_FORMAT_PATTERN).format(datetime);
    }

    /**
     * 以默认格式解析日期时间
     * <p>
     * See {@link #DATE_TIME_FORMAT_PATTERN}
     *
     * @param text
     * @return
     */
    @SneakyThrows
    public static Date parseDateTime(String text) {
        return getSdfByPattern(DATE_TIME_FORMAT_PATTERN).parse(text);
    }

    /**
     * 以指定格式格式化日期/时间
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        return getSdfByPattern(pattern).format(date);
    }

    /**
     * 以指定格式解析日期/时间
     *
     * @param text
     * @param pattern
     * @return
     */
    @SneakyThrows
    public static Date parse(String text, String pattern) {
        return getSdfByPattern(pattern).parse(text);
    }

    /**
     * 以默认格式格式化日期
     * <p>
     * See {@link #DATE_FORMAT_PATTERN}
     *
     * @param date
     * @return
     */
    public static String formatDate(LocalDate date) {
        return getDtfByPattern(DATE_FORMAT_PATTERN).format(date);
    }

    /**
     * 以默认格式解析日期
     * <p>
     * See {@link #DATE_FORMAT_PATTERN}
     *
     * @param text
     * @return
     */
    public static LocalDate parseLocalDate(String text) {
        return LocalDate.parse(text, getDtfByPattern(DATE_FORMAT_PATTERN));
    }

    /**
     * 以默认格式格式化日期
     * <p>
     * See {@link #TIME_FORMAT_PATTERN}
     *
     * @param time
     * @return
     */
    public static String formatTime(LocalTime time) {
        return getSdfByPattern(TIME_FORMAT_PATTERN).format(time);
    }

    /**
     * 以默认格式解析时间
     * <p>
     * See {@link #TIME_FORMAT_PATTERN}
     *
     * @param text
     * @return
     */
    public static LocalTime parseLocalTime(String text) {
        return LocalTime.parse(text, getDtfByPattern(TIME_FORMAT_PATTERN));
    }

    /**
     * 以默认格式格式化日期时间
     * <p>
     * See {@link #DATE_TIME_FORMAT_PATTERN}
     *
     * @param datetime
     * @return
     */
    public static String formatDateTime(LocalDateTime datetime) {
        return getDtfByPattern(DATE_TIME_FORMAT_PATTERN).format(datetime);
    }

    /**
     * 以默认格式解析日期时间
     * <p>
     * See {@link #DATE_TIME_FORMAT_PATTERN}
     *
     * @param text
     * @return
     */
    public static LocalDateTime parseLocalDateTime(String text) {
        return LocalDateTime.parse(text, getDtfByPattern(DATE_TIME_FORMAT_PATTERN));
    }

    /**
     * 以指定格式格式化日期/时间
     *
     * @param temporal
     * @param pattern
     * @return
     */
    public static String format(TemporalAccessor temporal, String pattern) {
        return getDtfByPattern(pattern).format(temporal);
    }

    /**
     * 以指定格式解析日期/时间
     *
     * @param query
     * @param text
     * @param pattern
     * @param <T>
     * @return
     */
    public static <T> T parse(TemporalQuery<T> query, String text, String pattern) {
        return getDtfByPattern(pattern).parse(text, query);
    }

    /**
     * 格式化年份
     *
     * @param year
     * @return
     */
    public static String formatYear(int year) {
        return String.format("%d年", year);
    }

    /**
     * 格式化月份
     *
     * @param month
     * @return
     */
    public static String formatMonth(int month) {
        return String.format("%d月", month);
    }

    /**
     * 格式化小时
     *
     * @param hour
     * @return
     */
    public static String formatHour(int hour) {
        return String.format("%02d:00", hour);
    }

    /**
     * 生成日期序列
     *
     * @param startDateInclusive 开始日期（包含）
     * @param endDateExclusive   结束日期（不包含）
     * @param stepPeriod         时间增长步长
     * @return 从开始日期到结束日期的日期序列
     */
    public static List<LocalDate> generateDateSequence(LocalDate startDateInclusive, LocalDate endDateExclusive, Period stepPeriod) {
        Preconditions.checkArgument(endDateExclusive.isAfter(startDateInclusive), "开始日期必须小于结束日期");

        List<LocalDate> dateSequence = new ArrayList<>();
        LocalDate date = LocalDate.ofEpochDay(startDateInclusive.toEpochDay());
        do {
            dateSequence.add(date);
            date = date.plus(stepPeriod);
        } while (date.isBefore(endDateExclusive));
        return dateSequence;
    }

    private static SimpleDateFormat getSdfByPattern(String pattern) {
        return DATE_SDF_MAP.get().computeIfAbsent(pattern, SimpleDateFormat::new);
    }

    private static DateTimeFormatter getDtfByPattern(String pattern) {
        return DTF_MAP.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }
}

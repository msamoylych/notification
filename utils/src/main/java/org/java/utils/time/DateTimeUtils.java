package org.java.utils.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by msamoylych on 25.04.2017.
 */
public final class DateTimeUtils {

    public static final ZoneId UTC = ZoneId.of("UTC");

    public static ZonedDateTime toZonedDateTime(long epochSecond, ZoneId zoneId) {
        Instant instant = Instant.ofEpochMilli(epochSecond);
        return ZonedDateTime.ofInstant(instant, zoneId);
    }
}

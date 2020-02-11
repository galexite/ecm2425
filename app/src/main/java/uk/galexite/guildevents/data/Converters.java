package uk.galexite.guildevents.data;

import androidx.room.TypeConverter;

import java.sql.Timestamp;

/**
 * A set of type converters used by Android Room to convert types it cannot handle in to those that
 * can be represented in a SQL database.
 * <p>
 * Based on:
 * https://developer.android.com/training/data-storage/room/referencing-data#type-converters
 */
public class Converters {

    /**
     * Creates a new Date object given a timestamp (Unix epoch).
     * @param value timestamp in seconds since the Unix epoch
     * @return a new Date at the time given by the timestamp
     */
    @TypeConverter
    public static Timestamp dateFromJDBCTimestamp(String value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    /**
     * Gets the timestamp associated with a Date object for storing in the SQL database.
     * @param date the Date object
     * @return the timestamp in seconds since the Unix epoch
     */
    @TypeConverter
    public static String longTimestampFromDate(Timestamp date) {
        return date == null ? null : date.toString();
    }
}

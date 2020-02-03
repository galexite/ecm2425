package uk.galexite.guildevents.data;

import androidx.room.TypeConverter;

import java.util.Date;

class Converters {

    @TypeConverter
    public static Date dateFromLongTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long longTimestampFromDate(Date date) {
        return date == null ? null : date.getTime();
    }
}

package sk.uniza.semestralka.entities.converters

import androidx.room.TypeConverter
import sk.uniza.semestralka.entities.Color
import sk.uniza.semestralka.entities.TaskType
import java.util.*

/**
 * Converts date to date stored in database and vice versa
 *
 */
class DateConverter {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return dateLong?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}

/**
 * Converts Enum TaskTypeConverter to enum stored in database and vice versa
 *
 */
class TaskTypeConverter {
    @TypeConverter
    fun toTaskType(value: String) = enumValueOf<TaskType>(value)

    @TypeConverter
    fun fromTaskType(value: TaskType) = value.name
}

/**
 * Converts Enum ColorConverter to enum stored in database and vice versa
 *
 */
class ColorConverter {
    @TypeConverter
    fun toColor(value: String) = enumValueOf<Color>(value)

    @TypeConverter
    fun fromColor(value: Color) = value.name
}

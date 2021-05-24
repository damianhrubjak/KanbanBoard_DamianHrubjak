package sk.uniza.semestralka.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import sk.uniza.semestralka.entities.Project
import sk.uniza.semestralka.entities.Task
import sk.uniza.semestralka.entities.converters.ColorConverter
import sk.uniza.semestralka.entities.converters.DateConverter
import sk.uniza.semestralka.entities.converters.TaskTypeConverter



@Database(entities = [Project::class, Task::class], version = 8, exportSchema = false)
@TypeConverters(DateConverter::class, TaskTypeConverter::class,ColorConverter::class)
/**
 * Class responsible for creating room database
 */
abstract class KanbanDatabase: RoomDatabase() {

    abstract val kanbanDatabaseDao: KanbanDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: KanbanDatabase? = null

        /**
         * Method for returning instance of database
         *
         * @param context
         * @return Instance of database
         */
        fun getInstance(context: Context): KanbanDatabase {
            synchronized(this){
                var instance: KanbanDatabase? = this.INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        KanbanDatabase::class.java,
                        "kanban_database"
                    ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}
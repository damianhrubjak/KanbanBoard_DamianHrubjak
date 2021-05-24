package sk.uniza.semestralka.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

/**
 * Task data class
 *
 * @property taskID - id of task
 * @property projectID - ID of the project to which the task is assigned to
 * @property taskName - name of task
 * @property taskDescription - description of task
 * @property taskType - type of task
 * @property color - color of task
 * @property expanded - defines state of task in UI, whether options menu is hidden on not
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = Project::class,
        parentColumns = ["projectID"],
        childColumns = ["project_id"],
        onDelete = CASCADE
    )]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskID: Long = 0L,

    @ColumnInfo(name = "project_id", index = true)
    val projectID: Long = 0L,

    @ColumnInfo(name = "task_name")
    val taskName: String,

    @ColumnInfo(name = "task_description")
    val taskDescription: String,

    @ColumnInfo(name = "task_type")
    var taskType: TaskType,

    @ColumnInfo(name = "task_color")
    var color: Color = Color.DEFAULT,

    @ColumnInfo(name = "expanded")
    var expanded: Boolean = false
){
    //override default constructor
    constructor(
        projectID: Long,
        taskName: String,
        taskDescription: String,
        taskType: TaskType,
        color: Color
    ) : this(0, projectID, taskName, taskDescription, taskType, color, false)
}


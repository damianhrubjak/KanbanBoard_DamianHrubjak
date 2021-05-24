package sk.uniza.semestralka.database

import androidx.room.*
import sk.uniza.semestralka.entities.Color
import sk.uniza.semestralka.entities.Project
import sk.uniza.semestralka.entities.Task
import sk.uniza.semestralka.entities.TaskType
import sk.uniza.semestralka.entities.relations.ProjectWithTasks

/**
 * Interface of queries for database
 * Contains queries for project and tasks
 */
@Dao
interface  KanbanDatabaseDao {
    @Insert
    fun insert(project: Project)

    @Insert
    fun insertTask(task: Task)

    @Update
    fun update(project: Project)

    @Query("UPDATE tasks SET task_name = :taskName, task_description = :taskDescription, task_type = :taskType, task_color = :taskColor WHERE taskID = :taskID ")
    fun updateTask(taskID:Long,taskName:String, taskDescription: String, taskType: TaskType, taskColor: Color)

    @Query("UPDATE tasks SET task_color = :taskColor WHERE taskID = :taskID ")
    fun updateTaskColor(taskID:Long, taskColor: Color)

    @Query("UPDATE tasks SET task_type = :taskType WHERE taskID = :taskID ")
    fun updateTaskType(taskID:Long, taskType: TaskType)

    @Query("SELECT * from projects WHERE projectID = :key")
    fun get(key: Long):  Project?

    @Query("SELECT * from tasks WHERE taskID = :key")
    fun getTask(key: Long):  Task?

    @Query("DELETE FROM projects WHERE projectID = :key")
    fun deleteById(key: Long)

    @Query("DELETE FROM tasks WHERE taskID = :key")
    fun deleteTaskById(key: Long)

    @Query("SELECT * from projects")
    fun getAllProjects():  List<Project>?

    @Query("SELECT * from tasks WHERE tasks.project_id = :key")
    fun getTasksAssignedToProject(key: Long):  MutableList<Task>?

    @Query("SELECT * from tasks WHERE tasks.project_id = :key AND task_type = :type")
    fun getTasksAssignedToProjectByProjectIDAndType(key: Long, type:TaskType):  MutableList<Task>?

    @Transaction
    @Query("SELECT * FROM projects ORDER BY projectDeadline ASC")
    fun getProjectWithTasksByDeadlineAscending(): List<ProjectWithTasks>

    @Transaction
    @Query("SELECT * FROM projects ORDER BY projectDeadline ASC LIMIT :limit")
    fun getProjectWithTasksByDeadlineAscendingLimit(limit : Int): List<ProjectWithTasks>

    @Query("DELETE  FROM projects")
    fun clear()

    @Query("DELETE  FROM tasks")
    fun clearTasks()
}
package sk.uniza.semestralka.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Project data class
 *
 * @property projectID - id of project
 * @property projectName - name of project
 * @property projectDescription - description of project
 * @property projectDeadline - deadline of project
 */
@Entity(tableName = "projects")
data class Project  (
    @PrimaryKey(autoGenerate = true)
    val projectID: Long,

    val projectName: String,

    val projectDescription: String,

    val projectDeadline: Date
){
    //override default constructor
    constructor(projectName: String, projectDescription: String,projectDeadline : Date) : this(0, projectName, projectDescription, projectDeadline)
}
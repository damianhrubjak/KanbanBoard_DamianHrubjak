package sk.uniza.semestralka.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import sk.uniza.semestralka.entities.Project
import sk.uniza.semestralka.entities.Task

/**
 * This class defines relation between Project and Task
 *
 */
class ProjectWithTasks {
    @Embedded
    var project: Project? = null

    @Relation(
        parentColumn = "projectID",
        entityColumn = "project_id",
        entity = Task::class
    )
    var tasks:List<Task>? = null
}
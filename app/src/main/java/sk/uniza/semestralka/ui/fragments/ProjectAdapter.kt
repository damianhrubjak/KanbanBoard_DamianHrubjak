package sk.uniza.semestralka.ui.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import sk.uniza.semestralka.R
import sk.uniza.semestralka.entities.relations.ProjectWithTasks
import sk.uniza.semestralka.formatDate

/**
 * Class used for creating project adapter for project recyclerview
 *
 * @property context
 * @property projects
 */
class ProjectAdapter(
    private val context: Context,
    private val projects: List<ProjectWithTasks>
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {

        init {
            //set listeners
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        private var projectHeading: TextView = itemView.findViewById(R.id.project_item_project_heading)
        private var projectDesc: TextView = itemView.findViewById(R.id.project_item_project_description)
        private var projectDeadline: TextView = itemView.findViewById(R.id.project_item_project_deadline)
        private var noOfTasks: TextView = itemView.findViewById(R.id.project_item_number_of_tasks)
        private var projectID: Long = 0

        /**
         * When user clicks on item, redirect to the tasks page
         *
         * @param view
         */
        override fun onClick(view: View?) {
            val bundle = bundleOf("argProjectID" to projectID)
            view!!.findNavController().navigate(R.id.action_nav_projects_to_TaskFragment, bundle)
        }

        /**
         * When user long clicks on item, redirect to the edit page
         *
         * @param view
         * @return true
         */
        override fun onLongClick(view: View?): Boolean {
            val bundle = bundleOf("argProjectID" to projectID)
            view!!.findNavController().navigate(
                R.id.action_nav_projects_to_editProjectFragment,
                bundle
            )

            return true
        }

        /**
         * Set data to project TextViews
         *
         * @param projectWithTasks - ProjectWithTasks objects, which contains data
         */
        fun setDataToView(projectWithTasks: ProjectWithTasks) {
            projectHeading.text = projectWithTasks.project!!.projectName
            projectDesc.text = projectWithTasks.project!!.projectDescription
            projectDeadline.text = formatDate(projectWithTasks.project!!.projectDeadline)
            projectID = projectWithTasks.project!!.projectID
            val plural = context.resources.getString(R.string.task_word_plural)
            val singular = context.resources.getString(R.string.task_word)
            noOfTasks.text =
                if (projectWithTasks.tasks!!.size > 1 || projectWithTasks.tasks!!.isEmpty()) projectWithTasks.tasks!!.size.toString() + " " + plural else projectWithTasks.tasks!!.size.toString() + " " + singular
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project, parent, false)

        return ProjectViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.setDataToView(projects[position])
    }

    override fun getItemCount(): Int = projects.size
}
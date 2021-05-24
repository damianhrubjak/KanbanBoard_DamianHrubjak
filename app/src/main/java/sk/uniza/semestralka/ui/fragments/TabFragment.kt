package sk.uniza.semestralka.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import sk.uniza.semestralka.R
import sk.uniza.semestralka.database.KanbanDatabase
import sk.uniza.semestralka.database.KanbanDatabaseDao
import sk.uniza.semestralka.databinding.ItemTaskBinding
import sk.uniza.semestralka.entities.Color
import sk.uniza.semestralka.entities.Direction
import sk.uniza.semestralka.entities.Task
import sk.uniza.semestralka.entities.TaskType

/**
 * This class creates recycler view in ViewPager2
 * provides functionality of recycler view - moving to other category
 */
class TabFragment : Fragment() {
    private var movedItem: Boolean = false
    lateinit var database: KanbanDatabaseDao
    var taskType: TaskType = TaskType.UNDEFINED
    var callbacks: Callbacks? = null
    lateinit var adapter: TaskViewAdapter
    var projectID: Long = 0L
    private lateinit var recyclerView: RecyclerView

    /**
     * Define interface of callbacks
     *
     */
    interface Callbacks {
        fun addTaskToViewModel(task: Task, type: TaskType)
        fun getTaskListFromDatabase(projectID: Long, type: TaskType): MutableList<Task>?
    }

    /**
     * Reassign activity as callback
     *
     * @param context
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = requireActivity() as Callbacks
    }

    /**
     * Remove activity as callback
     *
     */
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_task_tab, container, false)

        projectID = arguments?.getLong("project_ID") as Long

        this.taskType = when (arguments?.getInt("tasks_type")) {
            0 -> TaskType.TO_DO
            1 -> TaskType.DOING
            2 -> TaskType.DONE
            else -> throw Exception("Bad task TYPE")
        }

        this.database = KanbanDatabase.getInstance(root!!.context).kanbanDatabaseDao

        callbacks = requireActivity() as Callbacks

        //paint recycler view
        recyclerView = root.findViewById(R.id.rv_tasks) as RecyclerView

        setDataForRecyclerView(projectID)

        return root
    }

    /**
     * Change data when fragments resumes
     *
     * I had to do this, because, a lot of bugs were happening
     */
    override fun onResume() {
        super.onResume()
        if (this.projectID != 0L) {
            //https://stackoverflow.com/questions/37758285/android-recyclerview-adding-duplicate-elements-on-each-activity-creation
            setDataForRecyclerView(this.projectID)
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * Setup recycler view, adapter and data for it
     *
     * @param projectID
     */
    private fun setDataForRecyclerView(projectID: Long) {
        //obtain list of task from database by main activity
        recyclerView.visibility = View.INVISIBLE
        val tasks = callbacks!!.getTaskListFromDatabase(projectID, this.taskType)!!
        adapter = TaskViewAdapter(tasks)
        recyclerView.adapter = adapter
        recyclerView.visibility = View.VISIBLE
    }


    inner class TaskViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        private val binding = ItemTaskBinding.bind(itemView)
        lateinit var task: Task

        init {
            this.setOnClickListeners()
        }

        private fun setOnClickListeners() {
            this.binding.taskItemParent.setOnClickListener(this)
            this.binding.taskItemParent.setOnLongClickListener(this)
            this.binding.taskItemColorYellowButton.setOnClickListener(this)
            this.binding.taskItemColorBlueButton.setOnClickListener(this)
            this.binding.taskItemColorRedButton.setOnClickListener(this)
            this.binding.taskItemColorGreenButton.setOnClickListener(this)
            this.binding.expandOptionsButton.setOnClickListener(this)
            this.binding.taskItemArrowGoBack.setOnClickListener(this)
            this.binding.taskItemArrowGoForward.setOnClickListener(this)
        }

        /**
         * Override on click method and launch specific methods according to the clicked id
         *
         * @param view
         */
        override fun onClick(view: View?) {
            when (view?.id) {
                binding.expandOptionsButton.id -> {
                    onClickExpandButton()
                }
                binding.taskItemColorYellowButton.id -> {
                    changeColor(Color.YELLOW)
                }
                binding.taskItemColorBlueButton.id -> {
                    changeColor(Color.BLUE)
                }
                binding.taskItemColorRedButton.id -> {
                    changeColor(Color.RED)
                }
                binding.taskItemColorGreenButton.id -> {
                    changeColor(Color.GREEN)
                }
                binding.taskItemArrowGoBack.id -> {
                    moveItem(Direction.BACK)
                }
                binding.taskItemArrowGoForward.id -> {
                    moveItem(Direction.FORWARD)
                }
            }
        }

        /**
         * This method is called when user click forward / back button
         * Defines new type of task based on direction
         * When direction is fine, call method setTaskType with new type of task
         *
         * @param direction
         */
        private fun moveItem(direction: Direction) {
            when (taskType) {
                TaskType.TO_DO -> {
                    if (direction == Direction.BACK) {
                        return
                    }
                    if (direction == Direction.FORWARD) {
                        setTaskType(TaskType.DOING)
                    }
                }
                TaskType.DOING -> {
                    if (direction == Direction.BACK) {
                        setTaskType(TaskType.TO_DO)
                    }
                    if (direction == Direction.FORWARD) {
                        setTaskType(TaskType.DONE)
                    }
                }
                TaskType.DONE -> {
                    if (direction == Direction.BACK) {
                        setTaskType(TaskType.DOING)
                    }
                    if (direction == Direction.FORWARD) {
                        return
                    }
                }
                TaskType.UNDEFINED -> {
                    Toast.makeText(
                        requireActivity(),
                        requireActivity().resources.getText(R.string.error_bad_task_type),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        /**
         * This method changes TaskType of task
         * Removes task from adapter list and notifies adapter, that item was removed
         *
         * @param newType
         */
        private fun setTaskType(newType: TaskType) {
            task.expanded = false
            //add this task with set new task type to new view model
            callbacks?.addTaskToViewModel(task, newType)

            //remove task from this view model
            adapter.tasks.remove(task)

            //notify adapter that item was removed
            adapter.notifyItemRemoved(this.bindingAdapterPosition)
        }

        /**
         * When user long clicks on item, redirect to the edit task fragment
         *
         * @param view
         * @return
         */
        override fun onLongClick(view: View?): Boolean {
            //redirect to edit page
            val bundle = bundleOf("argTaskID" to task.taskID)
            view!!.findNavController().navigate(
                R.id.action_taskFragment_to_editTaskFragment,
                bundle
            )
            return true
        }

        /**
         * Changes color, when user click on specific color view
         *
         * @param color
         */
        private fun changeColor(color: Color) {
            if (task.color != color) {
                //change color
                task.color = color

                //update database with new color
                database.updateTaskColor(task.taskID, color)

                //repaint box with new color
                paintItemViewAccordingToColor()

                //adapter.notifyItemChanged(this.bindingAdapterPosition)
            }
        }

        /**
         * Expand view, when user clicks on it
         *
         */
        private fun onClickExpandButton() {
            //expand options menu
            this.task.expanded = !this.task.expanded
            binding.expandableOptions.visibility =
                if (!this.task.expanded) View.GONE else View.VISIBLE

            //adapter.notifyItemChanged(this.bindingAdapterPosition)
        }

        /**
         * Set item's TextViews
         *
         * @param task - Task
         */
        fun setDataToView(task: Task) {
            //Assign given task to this viewholder
            this.task = task

            binding.taskItemTaskHeading.text = task.taskName
            binding.taskItemTaskDescription.text = task.taskDescription

            paintItemViewAccordingToColor()
        }

        /**
         * Paint view, according to the task.color attribute
         *
         */
        private fun paintItemViewAccordingToColor() {
            //Assign corresponding drawable as background for task item according to the task.color property
            val taskCardDrawableResId = when (task.color) {
                Color.YELLOW -> R.drawable.radio_selected_yellow
                Color.BLUE -> R.drawable.radio_selected_blue
                Color.GREEN -> R.drawable.radio_selected_green
                Color.RED -> R.drawable.radio_selected_red
                Color.DEFAULT -> R.drawable.radio_selected_default
            }

            // set card background
            binding.taskItemParent.background =
                ResourcesCompat.getDrawable(resources, taskCardDrawableResId, null)
        }

        /**
         * Initialize holder
         * This method is launched when creating TaskViewHolder
         *
         * @return
         */
        fun initializeHolder(): TaskViewHolder {
            //forbid user to click on back_button, when item is in to_DO and so on
            if (taskType == TaskType.TO_DO) {
                binding.taskItemArrowGoBack.visibility = View.INVISIBLE
            }

            if (taskType == TaskType.DONE) {
                binding.taskItemArrowGoForward.visibility = View.INVISIBLE
            }

            //hide color menu
            binding.expandableOptions.visibility = View.GONE

            return this
        }
    }

    /**
     * Adapter for recycler view
     *
     * @property tasks
     */
    inner class TaskViewAdapter(var tasks: MutableList<Task>) :
        RecyclerView.Adapter<TaskViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            //paint item task for every recycler view
            val view = layoutInflater.inflate(R.layout.item_task, parent, false)

            //return TaskAdapter with prepared listeners and prepare view
            return TaskViewHolder(view).initializeHolder()
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val task: Task = tasks[position]
            holder.setDataToView(task)
        }

        override fun getItemCount(): Int = tasks.size
    }

    /**
     * Function is used to add data to adapter
     * This method is called from activity
     *
     * @param task
     */
    fun addTaskToViewModel(task: Task) {
        this.movedItem = true
        adapter.tasks.add(task)

        //adapter.notifyDataSetChanged()
        adapter.notifyItemInserted(adapter.tasks.size)
    }
}
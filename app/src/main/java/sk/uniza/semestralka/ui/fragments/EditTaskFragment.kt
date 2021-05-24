package sk.uniza.semestralka.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import sk.uniza.semestralka.R
import sk.uniza.semestralka.database.KanbanDatabase
import sk.uniza.semestralka.database.KanbanDatabaseDao
import sk.uniza.semestralka.databinding.FragmentEditTaskBinding
import sk.uniza.semestralka.entities.Color
import sk.uniza.semestralka.entities.Task
import sk.uniza.semestralka.entities.TaskType
import sk.uniza.semestralka.hideSoftKeyboard
import java.lang.Exception

/**
 * Class used for displaying fragment_edit_task
 * Allows editing of task
 *
 */
class EditTaskFragment : Fragment(), RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private var taskType: TaskType = TaskType.TO_DO
    private var taskColor: Color = Color.DEFAULT
    private lateinit var task: Task
    private lateinit var colorRadioGroup: RadioGroup
    private lateinit var typeRadioGroup: RadioGroup
    private lateinit var taskNameInput: TextInputEditText
    private lateinit var taskDescInput: TextInputEditText
    private lateinit var editBtn: Button
    private lateinit var deleteBtn: Button
    private lateinit var database: KanbanDatabaseDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //create data binding object for easier access to the views
        val binding = DataBindingUtil.inflate<FragmentEditTaskBinding>(
            inflater, R.layout.fragment_edit_task, container, false)

        colorRadioGroup = binding.taskColorRadioGroup
        typeRadioGroup = binding.taskTypeRadioGroup

        colorRadioGroup.setOnCheckedChangeListener(this)
        typeRadioGroup.setOnCheckedChangeListener(this)

        this.taskNameInput = binding.editTaskTaskNameInput
        this.taskDescInput = binding.editTaskTaskDescriptionInput
        this.editBtn = binding.editTaskSubmitBtn
        this.deleteBtn = binding.deleteTaskSubmitBtn

        //create listeners
        this.editBtn.setOnClickListener(this)
        this.deleteBtn.setOnClickListener(this)

        //get instance of database
        database = KanbanDatabase.getInstance(binding.root.context.applicationContext).kanbanDatabaseDao

        //get task object from database
        val id = arguments?.getLong("argTaskID")!!
        task = database.getTask(id)!!

        setInputValues()

        return binding.root
    }

    /**
     * Set Input values according to the task attribute
     *
     */
    private fun setInputValues() {
        taskNameInput.setText(task.taskName)
        taskDescInput.setText(task.taskDescription)

        val colorCheckId = when (task.color) {
            Color.BLUE -> R.id.task_color_radio_blue
            Color.RED ->  R.id.task_color_radio_red
            Color.GREEN ->  R.id.task_color_radio_green
            Color.YELLOW -> R.id.task_color_radio_yellow
            Color.DEFAULT ->  R.id.task_color_radio_default
        }

        val typeCheckId = when (task.taskType) {
            TaskType.TO_DO -> R.id.task_type_radio_to_do
            TaskType.DOING -> R.id.task_type_radio_doing
            TaskType.DONE -> R.id.task_type_radio_done
            TaskType.UNDEFINED-> throw Exception("Bad Task Type")
        }

        colorRadioGroup.check(colorCheckId)
        typeRadioGroup.check(typeCheckId)
    }

    /**
     * Set taskType attribute, based on clicked radio button
     *
     * @param checkedId
     */
    private fun onRadioButtonClickedTaskType(checkedId: Int) {
        when (checkedId) {
            R.id.task_type_radio_to_do ->
                this.taskType = TaskType.TO_DO

            R.id.task_type_radio_doing ->
                this.taskType = TaskType.DOING

            R.id.task_type_radio_done ->
                this.taskType = TaskType.DONE
        }
    }

    /**
     * Set taskColor attribute, based on clicked radio button
     *
     * @param checkedId
     */
    private fun onRadioButtonClickedTaskColor(checkedId: Int) {
        when (checkedId) {
            R.id.task_color_radio_blue ->
                this.taskColor = Color.BLUE

            R.id.task_color_radio_green ->
                this.taskColor = Color.GREEN

            R.id.task_color_radio_red ->
                this.taskColor = Color.RED

            R.id.task_color_radio_yellow ->
                this.taskColor = Color.YELLOW

            R.id.task_color_radio_default ->
                this.taskColor = Color.DEFAULT
        }
    }

    /**
     * This method is fired when user clicks on radio group
     *
     * @param group
     * @param checkedId
     */
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (group) {
            colorRadioGroup -> onRadioButtonClickedTaskColor(checkedId)
            typeRadioGroup -> onRadioButtonClickedTaskType(checkedId)
        }
    }

    /**
     * This method is fired when user clicks specific view
     *
     * @param v
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            editBtn.id -> editTask()
            deleteBtn.id -> deleteTask()
        }
    }

    /**
     * This method is fired, when user click on delete button
     * shows AlertDialog, to choose, whether task should be deleted or not
     */
    private fun deleteTask() {
        //show alert dialog, when user click on delete button
        hideSoftKeyboard(requireActivity())
        AlertDialog.Builder(context)
            .setTitle(R.string.button_delete_task)
            .setMessage(R.string.dialog_box_project_delete_message)
            .setPositiveButton(
                R.string.button_delete_task
            ) { _, _ ->
                //if pressed yes, delete task and redirect to tasks fragment
                database.deleteTaskById(task.taskID)
                val bundle = bundleOf("argProjectID" to this.task.projectID)
                findNavController().navigate(R.id.action_editTaskFragment_to_taskFragment, bundle)
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.cancel, null)
            .setIcon(R.drawable.ic_round_delete_forever_24)
            .show()
    }

    /**
     * This method is fired, when user click on edit button
     * if all conditions are met, edit task
     */
    private fun editTask() {
        //hide keyboard
        hideSoftKeyboard(requireActivity())

        //boolean variables, that are defining, whether specific input is empty or not
        val emptyName: Boolean = this.taskNameInput.text.toString().isEmpty()
        val emptyDesc: Boolean = this.taskDescInput.text.toString().isEmpty()

        //error string, that will be displayed when input is empty
        val errEmpty = requireActivity().resources.getString(R.string.error_empty_input)

        if (emptyName) {
            this.taskNameInput.error = errEmpty
        }

        if (emptyDesc) {
            this.taskDescInput.error = errEmpty
        }

        if (!emptyName && !emptyDesc && taskType != TaskType.UNDEFINED && task.projectID != 0L) {
            //update task
            database.updateTask(
                task.taskID,
                this.taskNameInput.text.toString(),
                this.taskDescInput.text.toString(),
                taskType,
                taskColor
            )

            //create toast and redirect to the tasks fragment
            Toast.makeText(requireContext(), R.string.successful_edit_task, Toast.LENGTH_LONG)
                .show()
            val bundle = bundleOf("argProjectID" to this.task.projectID)
            findNavController().navigate(R.id.action_editTaskFragment_to_taskFragment, bundle)
        }
    }
}
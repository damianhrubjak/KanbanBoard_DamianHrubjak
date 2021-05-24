package sk.uniza.semestralka.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import sk.uniza.semestralka.R
import sk.uniza.semestralka.database.KanbanDatabase
import sk.uniza.semestralka.database.KanbanDatabaseDao
import sk.uniza.semestralka.entities.Color
import sk.uniza.semestralka.entities.Task
import sk.uniza.semestralka.entities.TaskType
import androidx.core.os.bundleOf
import sk.uniza.semestralka.hideSoftKeyboard

/**
 * Class used for displaying fragment_add_task
 * Allows creating of new task
 *
 */
class AddTaskFragment : Fragment(), RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private var taskType: TaskType = TaskType.TO_DO
    private var taskColor: Color = Color.DEFAULT
    private var projectId : Long = 0L
    private lateinit var colorRadioGroup: RadioGroup
    private lateinit var typeRadioGroup: RadioGroup
    private lateinit var taskName: TextInputEditText
    private lateinit var taskDesc: TextInputEditText
    private lateinit var submitBtn: Button
    private lateinit var database:KanbanDatabaseDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_add_task, container, false)

        colorRadioGroup = root.findViewById(R.id.task_color_radio_group)
        typeRadioGroup = root.findViewById(R.id.task_type_radio_group)

        //retrieve project id
        this.projectId = arguments?.getLong("argProjectID")!!

        //create listener when user picks radio button
        colorRadioGroup.setOnCheckedChangeListener(this)
        typeRadioGroup.setOnCheckedChangeListener(this)

        this.taskName = root.findViewById(R.id.add_task_task_name_input)
        this.taskDesc = root.findViewById(R.id.add_task_task_description_input)
        this.submitBtn = root.findViewById(R.id.add_task_task_submit_button)

        //create listener, which is fired when user clicks submit button
        this.submitBtn.setOnClickListener(this)

        //get instance of database
        database = KanbanDatabase.getInstance(root.context.applicationContext).kanbanDatabaseDao

        return root
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
     * This method is fired when user clicks submit button
     *
     * @param v - view
     */
    override fun onClick(v: View?) {
        //hide keyboard
        hideSoftKeyboard(requireActivity())

        //boolean variables, that are defining, whether specific input is empty or not
        val emptyName : Boolean = this.taskName.text.toString().isEmpty()
        val emptyDesc : Boolean = this.taskDesc.text.toString().isEmpty()

        //error string, that will be displayed when input is empty
        val errEmpty = requireActivity().resources.getString(R.string.error_empty_input)

        if (emptyName) {
            this.taskName.error = errEmpty
        }

        if(emptyDesc){
            this.taskDesc.error = errEmpty
        }

        //if all entered parameters are fine
        if(!emptyName && !emptyDesc && taskType != TaskType.UNDEFINED && projectId != 0L){
            //insert new task
            database.insertTask(
                Task(
                    projectId,
                    this.taskName.text.toString(),
                    this.taskDesc.text.toString(),
                    taskType,taskColor
                )
            )

            //create toast and redirect to other fragment
            Toast.makeText(requireContext(), R.string.successful_add_task, Toast.LENGTH_LONG).show()
            val bundle = bundleOf("argProjectID" to this.projectId)
            findNavController().navigate(R.id.action_addTask_to_taskFragment,bundle)
        }
    }
}

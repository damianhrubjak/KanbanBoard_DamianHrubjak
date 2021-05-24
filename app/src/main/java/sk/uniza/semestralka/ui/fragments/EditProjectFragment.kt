package sk.uniza.semestralka.ui.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import sk.uniza.semestralka.*
import sk.uniza.semestralka.database.KanbanDatabase
import sk.uniza.semestralka.database.KanbanDatabaseDao
import sk.uniza.semestralka.entities.Project
import java.text.SimpleDateFormat
import java.util.*

/**
 * Class used for displaying fragment_edit_project
 * Allows editing of project
 *
 */
class EditProjectFragment: Fragment(), DatePickerDialog.OnDateSetListener{
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private lateinit var database : KanbanDatabaseDao
    private var projectId : Long  = 0L
    private lateinit var projectDeadlineInput : TextInputEditText
    private lateinit var projectNameInput : TextInputEditText
    private lateinit var projectDescInput : TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_project, container, false)

        projectId = arguments?.getLong("argProjectID")!!
        database = KanbanDatabase.getInstance(requireContext().applicationContext).kanbanDatabaseDao
        val editingProject : Project = database.get(projectId)!!

        projectNameInput = root.findViewById(R.id.edit_project_project_name_input)
        projectDeadlineInput = root.findViewById(R.id.edit_project_project_deadline_input)
        projectDescInput  = root.findViewById(R.id.edit_project_project_description_input)

        val deleteButton : Button = root.findViewById(R.id.delete_project_submit_btn)
        val editButton : Button = root.findViewById(R.id.edit_project_submit_btn)

        //creates listener on TextInputEditText project deadline
        initializeDatePicker()

        //set data of TextInputEditText
        setInputData(editingProject)

        //create listener on delete button
        deleteButton.setOnClickListener {
            deleteTask()
        }

        //create listener on edit button
        editButton.setOnClickListener {
            updateTask()
        }

        return root
    }

    /**
     * Set text of TextInputEditText, when user arrives to the edit fragment
     *
     * @param editingProject
     */
    private fun setInputData(editingProject:Project) {
        projectNameInput.setText(editingProject.projectName)

        projectDeadlineInput.setText(
            SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault()).format(
                editingProject.projectDeadline
            )
        )

        projectDescInput.setText(editingProject.projectDescription)
    }

    /**
     * This method is fired, when user click on edit button
     *
     */
    private fun updateTask() {
        //hide keyboard
        hideSoftKeyboard(requireActivity())

        //boolean variables, that are defining, whether specific input is empty or not
        val emptyName : Boolean = projectNameInput.text.toString().isEmpty()
        val emptyDeadline : Boolean = projectDeadlineInput.text.toString().isEmpty()
        val emptyDesc : Boolean = projectDescInput.text.toString().isEmpty()

        //error string, that will be displayed when input is empty
        val errEmpty = requireActivity().resources.getString(R.string.error_empty_input)

        if (emptyName) {
            projectNameInput.error = errEmpty
        }
        if (emptyDeadline) {
            projectDeadlineInput.error = errEmpty
        }
        if(emptyDesc){
            projectDescInput.error = errEmpty
        }

        if(!emptyDeadline && !emptyName && !emptyDesc){
            //update project
            database.update(
                Project(
                    projectId,
                    projectNameInput.text.toString(),
                    projectDescInput.text.toString(),
                    SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault()).parse(
                        projectDeadlineInput.text.toString()
                    )!!
                )
            )

            //update widget, create toast and redirect to the project fragment
            Toast.makeText(
                requireContext(),
                R.string.successful_edit_project,
                Toast.LENGTH_LONG
            ).show()
            updateWidgetScreen(requireContext())
            findNavController().navigate(R.id.action_editProjectFragment_to_nav_projects)
        }
    }

    /**
     * This method is fired, when user click on delete button
     *
     */
    private fun deleteTask() {
        //show alert dialog, when user click on delete button
        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_box_project_delete_title)
            .setMessage(R.string.dialog_box_project_delete_message)
            .setPositiveButton(
                R.string.delete_confirm_message
            ) { _, _ ->
                //if pressed yes, delete project, update widget and redirect to home fragment
                database.deleteById(projectId)
                updateWidgetScreen(requireContext())
                findNavController().navigate(R.id.action_editProjectFragment_to_nav_projects)
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.cancel, null)
            .setIcon(R.drawable.ic_round_delete_forever_24)
            .show()
    }

    /**
     * This method shows DatePickerDialog, with current date, when user clicks on input
     *
     */
    private fun initializeDatePicker() {
        this.projectDeadlineInput.setOnClickListener {
            initializeDateVars()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }
    }

    private fun initializeDateVars() {
        val now = Calendar.getInstance()
        year = now.get(Calendar.YEAR)
        month = now.get(Calendar.MONTH)
        day = now.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * This method is fired, when user submits pick of date
     *
     * @param view
     * @param year - picked year
     * @param month - picked month
     * @param dayOfMonth - picked day
     */
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        this.projectDeadlineInput.setText(
            SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault()).format(
                toDate(
                    year,
                    month,
                    dayOfMonth
                )
            )
        )
    }
}
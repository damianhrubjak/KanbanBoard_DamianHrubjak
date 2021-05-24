package sk.uniza.semestralka.ui.fragments

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
import sk.uniza.semestralka.entities.Project
import java.text.SimpleDateFormat
import java.util.*

/**
 * Class used for displaying fragment_add_project
 * Allows creating of new project
 *
 */
class AddProjectFragment : Fragment(), DatePickerDialog.OnDateSetListener{

    private lateinit var dateInput: TextInputEditText
    private lateinit var submitButton: Button

    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_project, container, false)

        //initialize onclick on datetime and create date dialog
        this.dateInput = root.findViewById(R.id.add_project_project_deadline_input)
        initializeDatePicker()

        // create listener on submit button
        this.submitButton = root.findViewById(R.id.add_project_project_submit_name)
        onSubmitButton()

        return root
    }

    /**
     * This method is fired when user clicks ADD PROJECT button
     * If all inputs are fine, then create new project
     *
     */
    private fun onSubmitButton() {
        this.submitButton.setOnClickListener {
            //hide keyboard
            hideSoftKeyboard(requireActivity())

            //get input objects
            val projectNameInput : TextInputEditText = requireActivity().findViewById(R.id.add_task_task_name_input)
            val projectDeadlineInput : TextInputEditText = requireActivity().findViewById(R.id.add_project_project_deadline_input)
            val projectDescInput : TextInputEditText = requireActivity().findViewById(R.id.add_task_task_description_input)

            //get database instance
            val database = KanbanDatabase.getInstance(requireContext().applicationContext).kanbanDatabaseDao

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
                // insert project to database
                database.insert(
                    Project(
                        projectNameInput.text.toString(),
                        projectDescInput.text.toString(),
                        SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault()).parse(
                            projectDeadlineInput.text.toString()
                        )!!
                    )
                )
                //update widget, create toast and redirect to the project fragment
                updateWidgetScreen(requireContext())
                Toast.makeText(requireContext(), R.string.successful_add_project, Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_addProjectFragment_to_nav_projects)
            }
        }
    }


    /**
     * This method shows DatePickerDialog, with current date, when user clicks on input
     *
     */
    private fun initializeDatePicker() {
        this.dateInput.setOnClickListener {
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
        this.dateInput.setText(
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
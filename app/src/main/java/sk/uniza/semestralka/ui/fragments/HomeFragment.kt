package sk.uniza.semestralka.ui.fragments

import android.app.*
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import sk.uniza.semestralka.*
import sk.uniza.semestralka.database.KanbanDatabase
import sk.uniza.semestralka.database.KanbanDatabaseDao
import sk.uniza.semestralka.entities.relations.ProjectWithTasks
import java.util.*


/**
 * Class used for displaying fragment_home
 * Used for displaying projects in recyclerview
 *
 */
class HomeFragment : Fragment() {
    private lateinit var database: KanbanDatabaseDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        database =
            KanbanDatabase.getInstance(requireContext().applicationContext).kanbanDatabaseDao

        setUpRecyclerView(root)

        //create onclick listener for add project button
        addListenerAddButton(root)

        return root
    }

    /**
     * Create listener for ADD NEW PROJECT button
     *
     * @param root
     */
    private fun addListenerAddButton(root: View) {
        val fab: ExtendedFloatingActionButton = root.findViewById(R.id.fabAddNewProject)
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_nav_projects_to_addProjectFragment)
        }
    }


    /**
     * Method, which is responsible for correct setting of recycler view
     * This method also schedules alarm for notifications
     *
     * @param root
     */
    private fun setUpRecyclerView(root: View) {
        val list: List<ProjectWithTasks> =
            KanbanDatabase.getInstance(requireContext().applicationContext).kanbanDatabaseDao.getProjectWithTasksByDeadlineAscending()

        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH)
        val day = now.get(Calendar.DAY_OF_MONTH)

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val enabledNotifications =
            prefs.getBoolean(Constants.PREFERENCE_NOTIFICATIONS_ENABLED_KEY, true)


        //if notifications are enabled
        if (enabledNotifications) {
            val preferenceNotificationHour =
                prefs.getInt(Constants.PREFERENCE_NOTIFICATIONS_TIME_KEY, 15)

            val milliseconds = calculateMillisecondsToNotification(preferenceNotificationHour)

            for (item in list) {
                if (item.project?.projectDeadline!! == toDate(year, month, day)) {
                    setAlarm(item.project!!.projectName, false, milliseconds)
                } else if (item.project?.projectDeadline!!.before(toDate(year, month, day))) {
                    setAlarm(item.project!!.projectName, true, milliseconds)
                }
            }
        }

        // create recycler view and its adapter
        val recyclerView: RecyclerView = root.findViewById(R.id.rvProjects)
        recyclerView.adapter = ProjectAdapter(requireContext(), list)

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true)
    }

    /**
     * Calculate milliseconds to the next time, when notification should be displayed
     *
     * @param preferenceNotificationHour - hour, when notification should be displayed
     * @return LONG - milliseconds to the next time, when notification should be displayed
     */
    private fun calculateMillisecondsToNotification(preferenceNotificationHour: Int): Long {
        val now = Calendar.getInstance()

        now.apply {
            if (now.get(Calendar.HOUR_OF_DAY) >= preferenceNotificationHour) {
                add(Calendar.DAY_OF_MONTH,1)
            }
            now.set(Calendar.HOUR_OF_DAY,preferenceNotificationHour)
            now.set(Calendar.MINUTE,0)
            now.set(Calendar.SECOND,0)
        }

        return now.timeInMillis
    }

    /**
     * This method schedules alarm for notification
     * Creates intent of ReminderBroadcast
     *
     * @param projectName
     * @param afterDeadline
     * @param milliseconds
     */
    private fun setAlarm(projectName: String, afterDeadline: Boolean, milliseconds: Long) {
        val context = requireContext()
        val intent = Intent(context, ReminderBroadcast::class.java)
        intent.apply {
            action = "send.notification.extras"
            putExtra("projectName", projectName)
            putExtra("afterDeadline", afterDeadline)
        }


        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            milliseconds,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}
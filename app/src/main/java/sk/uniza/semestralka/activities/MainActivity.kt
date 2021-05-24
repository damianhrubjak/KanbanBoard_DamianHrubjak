package sk.uniza.semestralka.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import sk.uniza.semestralka.R
import sk.uniza.semestralka.database.KanbanDatabase
import sk.uniza.semestralka.database.KanbanDatabaseDao
import sk.uniza.semestralka.entities.Task
import sk.uniza.semestralka.entities.TaskType
import sk.uniza.semestralka.ui.fragments.TabFragment
import java.lang.Exception

class MainActivity : AppCompatActivity(), TabFragment.Callbacks,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var database: KanbanDatabaseDao
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //obtain instance of database
        this.database = KanbanDatabase.getInstance(this).kanbanDatabaseDao

        //set up side drawer
        setUpDrawer()
    }

    /**
     * Method used for setting up drawer
     *
     * Paint layout, and setup navigation
     */
    private fun setUpDrawer(){
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.projects_fragment_navigation, R.id.taskFragment), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener(this)
        navView.menu[0].isChecked = true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Callback method, provides communication between ViewPager's fragments
     *
     * @param task - task to be added to next fragment
     * @param type - defines type of destination fragment
     */
    override fun addTaskToViewModel(task: Task,type: TaskType) {
        //connection between TabFragment and TaskFragment
        //update task with new task type in database
        task.taskType = type
        database.updateTaskType(task.taskID, type)

        //find fragment
        val tabFragment = findTabFragment(type)!!
        tabFragment.addTaskToViewModel(task)
    }


    /**
     * Method for finding TabFragment, according to the type in parameter
     *
     * @param type - defines type searched fragment
     * @return TabFragment - instance of fragment according to given type
     */
    private fun findTabFragment(type: TaskType) : TabFragment?{
        for (fragment in supportFragmentManager.fragments){
            try {
                val tabFragment = fragment as TabFragment
                if(tabFragment.taskType == type){
                    return tabFragment
                }
            } catch (e:Exception){
                //Log.w("bad fragment type",fragment.toString())
            }
        }
        return null
    }


    /**
     * Method for obtaining task from database
     *
     * @param projectID - ID of the project to which the tasks are assigned to
     * @param type - type of tasks (TO_DO, DOING, DONE)
     * @return if there are any task, return MutableList of Task, otherwise return empty list
     */
    override fun getTaskListFromDatabase(projectID: Long, type: TaskType): MutableList<Task>? {
        return if(projectID != 0L) database.getTasksAssignedToProjectByProjectIDAndType(
            projectID,
            type
        ) else mutableListOf()
    }

    /**
     * Method for navigating from drawer to specific fragments
     *
     * @param item
     * @return true
     */
    override fun onNavigationItemSelected(item: MenuItem):  Boolean {
        when(item.itemId){
            R.id.link_to_projects -> this.findNavController(R.id.nav_host_fragment)
                .navigate(R.id.globalNavigationToProjects)
            R.id.nav_settings -> this.findNavController(R.id.nav_host_fragment)
                .navigate(R.id.settingsFragment)

        }
        item.isChecked = true
        return true
    }
}
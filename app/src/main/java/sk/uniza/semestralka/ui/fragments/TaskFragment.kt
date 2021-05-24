package sk.uniza.semestralka.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import sk.uniza.semestralka.R

/**
 * Class used for creating ViewPager2 and assigning TabFragment to it
 *
 */
class TaskFragment : Fragment() {
    var projectID: Long = 0L
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_task, container, false)

        this.projectID = arguments?.getLong("argProjectID")!!

        viewPager = root.findViewById(R.id.view_pager_tasks)
        viewPager.apply {
            adapter = TasksFragmentStateAdapter(requireActivity())
            offscreenPageLimit = 2 // two pages are offscreen
        }

        tabLayout = root.findViewById(R.id.tabs_tasks)

        //synchronizes viewpager's position with the selected tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.to_do_text)
                1 -> getString(R.string.doing_text)
                // position 2
                else -> getString(R.string.done_text)
            }
        }.attach()


        val addTask: Button = root.findViewById(R.id.fab_add_new_task)

        //when user click on add button, redirect to add task
        addTask.setOnClickListener {
            val bundle = bundleOf("argProjectID" to this.projectID)
            findNavController().navigate(R.id.action_taskFragment_to_addTask, bundle)
        }

        return root
    }

    /**
     * Adapter class for view pager
     *
     * @constructor
     * TODO
     *
     * @param fa FragmentActivity
     */
    private inner class TasksFragmentStateAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun createFragment(position: Int): Fragment {
            //Create argument bundle with task list type
            val tasklistFragmentArguments = Bundle().apply {
                putInt("tasks_type", position)
                putLong("project_ID", projectID)
            }

            //Attach the argument bundle to new fragment instance and return the fragment
            return TabFragment().apply {
                arguments = tasklistFragmentArguments
            }
        }

        override fun getItemCount(): Int = 3 // three categories
    }
}


package sk.uniza.semestralka.ui.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import sk.uniza.semestralka.R
import sk.uniza.semestralka.database.KanbanDatabase
import sk.uniza.semestralka.entities.relations.ProjectWithTasks
import sk.uniza.semestralka.formatDate

class ProjectWidgetFactory : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return WidgetFactory(applicationContext, intent!!)
    }

    /**
     * Class that paints widget
     *
     * @property context
     * @property intent
     */
    class WidgetFactory(var context: Context, var intent: Intent) : RemoteViewsService.RemoteViewsFactory {
        private var appWidgetId: Int
        var projects: List<ProjectWithTasks>

        init {
            //obtain from database 5 projects, which deadline is closest to the today's date
            projects =
                KanbanDatabase.getInstance(context).kanbanDatabaseDao.getProjectWithTasksByDeadlineAscendingLimit(
                    5
                )
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        override fun onCreate() {
        }

        override fun onDataSetChanged() {
            //when this function is invoked, replace widget data
            projects =
                KanbanDatabase.getInstance(context).kanbanDatabaseDao.getProjectWithTasksByDeadlineAscendingLimit(
                    5
                )
        }

        override fun onDestroy() {
        }

        override fun getCount(): Int = projects.size

        /**
         * This method paints widget item
         * This method Sets project name and project deadline
         *
         * @param position
         * @return
         */
        override fun getViewAt(position: Int): RemoteViews {
            var views = RemoteViews(context.packageName, R.layout.item_widget_project)
            views.setTextViewText(
                R.id.widget_project_item_project_heading,
                projects[position].project?.projectName
            )
            views.setTextViewText(
                R.id.widget_project_item_project_deadline,
                formatDate(projects[position].project?.projectDeadline!!)
            )

            var fillIntent = Intent()
            fillIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId)

            return views
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return projects[position].project?.projectID!!
        }

        override fun hasStableIds(): Boolean {
            return true
        }
    }
}
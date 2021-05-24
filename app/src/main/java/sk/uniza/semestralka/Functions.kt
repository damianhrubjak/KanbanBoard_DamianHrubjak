package sk.uniza.semestralka

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import sk.uniza.semestralka.ui.widgets.ProjectWidget
import java.text.SimpleDateFormat
import java.util.*

/**
 * Function used to obtain DATE object from Calendar
 *
 * @param year
 * @param month
 * @param dayOfMonth
 * @param hour
 * @return Date object
 */
fun toDate(year: Int, month: Int, dayOfMonth: Int, hour: Int = 0) : Date {
    val cal = Calendar.getInstance()
    cal[Calendar.YEAR] = year
    cal[Calendar.MONTH] = month
    cal[Calendar.DAY_OF_MONTH] = dayOfMonth
    cal[Calendar.HOUR_OF_DAY] = hour
    cal[Calendar.MINUTE] = 0
    cal[Calendar.SECOND] = 0
    cal[Calendar.MILLISECOND] = 0
    return cal.time
}

/**
 * Function to format date according to format string
 *
 * @param date
 * @return formatted string
 */
fun formatDate(date: Date): String {
    return SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault()).format(date)
}

/**
 * This function updates widget, when new item is added
 *
 * @param context
 */
fun updateWidgetScreen(context: Context) {
    //create new intent that defines that widget should update
    val intent = Intent(context.applicationContext, ProjectWidget::class.java)
    intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

    val appWidgetManager = AppWidgetManager.getInstance(context)
    //ids of widgets
    val appWidgetIds = appWidgetManager.getAppWidgetIds(
        ComponentName(
            context,
            ProjectWidget::class.java
        )
    )

    // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
    // since it seems the onUpdate() is only fired on that:
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)

    context.sendBroadcast(intent)
    //notify widget data has changed
    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.rl_widget_project_list_view)
}

/**
 * Function for hiding keyboard after submitting form
 *
 * @param activity
 */
fun hideSoftKeyboard(activity: Activity) {
    val inputMethodManager: InputMethodManager = activity.getSystemService(
        Activity.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    if (inputMethodManager.isAcceptingText) {
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus!!.windowToken,
            0
        )
    }
}
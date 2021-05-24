package sk.uniza.semestralka.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import sk.uniza.semestralka.R
import sk.uniza.semestralka.activities.MainActivity
import java.util.*


class ProjectWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

/**
 * Method that paints widget, set its data and creates intents, when user click on it
 *
 * @param context
 * @param appWidgetManager
 * @param appWidgetId
 */
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {

    val intent = Intent(context,MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0 ,intent,0)

    val serviceIntent = Intent(context, ProjectWidgetFactory::class.java)
    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.project_widget)

    views.setOnClickPendingIntent(R.id.project_widget_no_project,pendingIntent)
    views.setOnClickPendingIntent(R.id.project_widget_no_project_desc,pendingIntent)

    views.setRemoteAdapter(R.id.list_view_widget, serviceIntent)
    views.setEmptyView(R.id.list_view_widget, R.id.project_widget_no_project_layout)
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.list_view_widget)
}


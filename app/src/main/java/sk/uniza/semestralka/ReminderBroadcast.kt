package sk.uniza.semestralka

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.navigation.NavDeepLinkBuilder
import sk.uniza.semestralka.activities.MainActivity

class ReminderBroadcast : BroadcastReceiver() {

    /**
     * When intent, which has action 'send.notification.extras' is received, show notification
     *
     * @param context
     * @param intent intent containing all action and extras [projectName,afterDeadline]
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "send.notification.extras") {
            val projectName = intent.extras!!.getString("projectName")
            val afterDeadline = intent.extras!!.getBoolean("afterDeadline")
            showNotification(context, projectName!!, afterDeadline)
        }
    }

    /**
     * Method for showing notification
     *
     * @param context
     * @param projectName - name of project used in notification
     * @param afterDeadline - defines whether project if after deadline
     */
    private fun showNotification(
        context: Context?,
        projectName: String,
        afterDeadline: Boolean = false
    ) {
        val resources = context!!.resources
        val channelID = resources.getString(R.string.notification_channel_id)

        //instance of NotificationManager
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: Notification.Builder

        //create intent, that redirects user to the application, when he clicks notification
        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.projects_fragment_navigation)
            .createPendingIntent()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //create notification channel
            val channel = NotificationChannel(
                channelID,
                resources.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )

            //set all parameter to NotificationChannel
            channel.apply {
                enableLights(true)
                lightColor = Color.GREEN //color of led diode
                enableVibration(true)
            }

            //create notification channel
            manager.createNotificationChannel(channel)

            //build notification with channelID - newest
            builder =
                Notification.Builder(context, channelID)
            initializeNotificationBuilder(builder,afterDeadline, projectName, resources, pendingIntent)

        } else {
            //It is deprecated in newer versions, for older, it will work normally
            @Suppress("DEPRECATION")
            builder =
                Notification.Builder(context)
            initializeNotificationBuilder(builder,afterDeadline,projectName, resources, pendingIntent)
        }

        //send notification
        manager.run {
            notify(NotificationID.iD, builder.build())
        }
    }

    /**
     * This method is used, to set all necessary parameters for Notification.Builder
     *
     * @param builder
     * @param afterDeadline
     * @param projectName
     * @param resources
     * @param pendingIntent
     */
    private fun initializeNotificationBuilder(
        builder: Notification.Builder,
        afterDeadline: Boolean,
        projectName: String,
        resources: Resources,
        pendingIntent: PendingIntent
    ) {
        builder.apply {
            style = if (afterDeadline) {
                setContentTitle(resources.getString(R.string.deadline_late) + " " + projectName)
                Notification.BigTextStyle()
                    .bigText(resources.getString(R.string.deadline_late_desc))
            } else {
                setContentTitle(resources.getString(R.string.deadline_today) + " " + projectName)
                Notification.BigTextStyle()
                    .bigText(resources.getString(R.string.deadline_today_desc))
            }
            setSmallIcon(R.mipmap.ic_launcher_round)
            setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
            setContentIntent(pendingIntent)
        }
    }
}
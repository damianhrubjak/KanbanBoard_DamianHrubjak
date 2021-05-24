package sk.uniza.semestralka

object Constants {
    //format of date
    const val DATE_FORMAT = "dd. MM. yyyy"

    /**
     * key of preference in XML whether notification is enabled or not
     */
    const val NOTIFICATIONS_ENABLED_KEY: String = "notification_value"
    /**
     * key of preference in XML that defines time, when notification should appear
     */
    const val NOTIFICATIONS_TIME_KEY: String = "notification_time"


    /**
     * key of preference whether notification is enabled or not
     */
    const val PREFERENCE_NOTIFICATIONS_ENABLED_KEY: String = "notification_enabled_preference"
    /**
     * key of preference, that defines time, when notification should appear
     */
    const val PREFERENCE_NOTIFICATIONS_TIME_KEY: String = "notification_time_preference"
}
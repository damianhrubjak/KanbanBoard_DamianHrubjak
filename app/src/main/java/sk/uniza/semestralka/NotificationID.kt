package sk.uniza.semestralka

import java.util.concurrent.atomic.AtomicInteger

//https://stackoverflow.com/questions/25713157/generate-int-unique-id-as-android-notification-id
/**
 * Object which provides unique id for notification
 */
object NotificationID {
    private val c: AtomicInteger = AtomicInteger(0)
    val iD: Int
        get() = c.incrementAndGet()
}
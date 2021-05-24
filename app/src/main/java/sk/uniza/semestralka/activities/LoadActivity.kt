package sk.uniza.semestralka.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import sk.uniza.semestralka.R


/**
 * Fullscreen activity
 * Loading screen
 */
class LoadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)
        initThread()
    }

    /**
     * Create thread to start main activity after 1.25 second
     */
    private fun initThread(){
        val welcomeThread: Thread = object : Thread() {
            override fun run() {
                try {
                    super.run()
                    sleep(1250)
                } catch (e: Exception) {
                } finally {
                    val i = Intent(
                        baseContext,
                        MainActivity::class.java
                    )
                    startActivity(i)
                    finish()
                }
            }
        }
        welcomeThread.start()
    }
}
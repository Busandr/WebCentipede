package com.busandr.webcentipede

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2

class HistoryActivity : AppCompatActivity() {
    val TAG = "HistoryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        setContentView(R.layout.activity_history)


        val toolbarHistory = findViewById<Toolbar>(R.id.toolbar_history)
        setSupportActionBar(toolbarHistory)

        val dbHelper = DatabaseHelper.DatabaseManager.getInstance(this)//, DATABASE_VERSION = 1)
        val linkHistory = dbHelper!!.readAllSnapshotsByURL(intent.getStringExtra("url")!!)
        var textHistory: TextView = findViewById(R.id.text_history)
        val buttonPrevVersion: Button = findViewById(R.id.prev_version)
        val buttonNextVersion: Button = findViewById(R.id.next_version)

        var currentLinkIndex = linkHistory.size - 1
        Log.i(TAG, "size = $currentLinkIndex")

        fun diffAndColorize(oldStr: String, newStr: String): String {
            val oldChars = StringBuilder()
            val newChars = StringBuilder()

            val maxLength = maxOf(oldStr.length, newStr.length)

            for (i in 0 until maxLength) {
                val oldChar = if (i < oldStr.length) oldStr[i] else '\u0000'
                val newChar = if (i < newStr.length) newStr[i] else '\u0000'
                oldChars.append(oldChar)
                newChars.append(newChar)
            }

            val itogueString = "Old: $oldChars New: $newChars"
            Log.i(TAG, "Old: $oldChars New: $newChars")
            return itogueString
        }

        if (linkHistory.size > 1)
            textHistory.text = diffAndColorize(
                linkHistory[currentLinkIndex].content,
                linkHistory[currentLinkIndex - 1].content
            )//[currentIndex - 1].content
        else
            textHistory.text = "Nothing to compare"
        buttonPrevVersion.setOnClickListener {
            if (currentLinkIndex > 1 && linkHistory.isNotEmpty()/*|| currentLinkIndex != linkHistory.size - 1*/) {
                // FIXME: too much work. put me in other thread, sempai
                textHistory.text = diffAndColorize(
                    linkHistory[currentLinkIndex].content,
                    linkHistory[currentLinkIndex - 1].content
                )
                currentLinkIndex--
            } else
                textHistory.text = "Yoe`ve reached the beginning"
        }
        buttonNextVersion.setOnClickListener {
            if (/*currentLinkIndex >= 0 ||*/ linkHistory.isNotEmpty() && currentLinkIndex < linkHistory.size - 1) {
                textHistory.text = diffAndColorize(
                    linkHistory[currentLinkIndex].content,
                    linkHistory[currentLinkIndex + 1].content
                )
                currentLinkIndex++
            } else
                textHistory.text = "Yoe`ve reached the end"
        }
    }
}

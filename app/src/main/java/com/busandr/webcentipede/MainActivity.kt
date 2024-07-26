package com.busandr.webcentipede

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.busandr.webcentipede.ui.MainSettingsActivity
import android.util.Log



//activity shows link_list + settings + fab

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val TAG = "MainActivity"
        val toolbar_main = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar_main)

        val switchButton = findViewById<SwitchCompat>(R.id.switch_button)
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startService(Intent(this, VisitService::class.java))
            } else {
                stopService(Intent(this, VisitService::class.java))
            }
        }

        //button to 2 activity
        findViewById<Button>(R.id.supabutton)
            .setOnClickListener {
                val intent = Intent(this, LinkViewActivity::class.java)
                startActivity(intent)
            }

        //filling recyclerview with data
        val dbHelper = DatabaseHelper(this)
        val linkList = dbHelper.readAll()

        val linkAdapter = LinkAdapter(this, linkList)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = linkAdapter

        //fab starting dialog for adding link
        val setLink: View = findViewById(R.id.setLink)
        setLink.setOnClickListener {

            val browserIntent = Intent(this, BrowserActivity::class.java)
            this.startActivity(intent)



        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, MainSettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
    }

    }
}

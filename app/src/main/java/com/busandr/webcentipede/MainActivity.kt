package com.busandr.webcentipede

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.busandr.webcentipede.service.VisitService
import com.busandr.webcentipede.ui.MainSettingsActivity

//activity shows link_list + settings + fab

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var linkList: MutableList<Link>
    private lateinit var linkAdapter: LinkAdapter

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Thanks", Toast.LENGTH_SHORT)
            } else {
                Toast.makeText(this, "you dont wanna be aware of...", Toast.LENGTH_LONG)
            }
        }

    private val toBrowser =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.i(TAG, "toBrowser")
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val tempLink = data?.getParcelableExtra<Link>("link")
                tempLink?.let { linkList.add(it) }
                tempLink?.let { dbHelper.insertLink(it) }
                linkAdapter.notifyItemInserted(linkList.size - 1)
            }
        }


    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbarMain = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbarMain)
        var switchButton = findViewById<SwitchCompat>(R.id.switch_button)
        if (isServiceRunning(this, VisitService::class.java))
            switchButton.isChecked
        else
            !switchButton.isChecked

        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("MainActivity", "Switch is turned ON")
                startService(Intent(this, VisitService::class.java))
            } else {
                stopService(Intent(this, VisitService::class.java))
                Log.d("MainActivity", "Switch is turned OFF")
            }
        }
        Log.i(TAG, "onCreate")
        dbHelper = DatabaseHelper.DatabaseManager.getInstance(this)!!
        linkList = dbHelper.readAllLinks()
        linkAdapter = LinkAdapter(this, linkList)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = linkAdapter

        val setLink: View = findViewById(R.id.addLink)
        setLink.setOnClickListener {
            val browserIntent = Intent(this, BrowserActivity::class.java)
            toBrowser.launch(browserIntent)
            onStop()
        }
        requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    override fun onStart() {
        super.onStart()
        var switchButton = findViewById<SwitchCompat>(R.id.switch_button)
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("MainActivity", "Switch is turned ON")
                startService(Intent(this, VisitService::class.java))
            } else {
                Log.d("MainActivity", "Switch is turned OFF")
                stopService(Intent(this, VisitService::class.java))
            }
        }
        if (isServiceRunning(this, VisitService::class.java))
            switchButton.isChecked
        else
            !switchButton.isChecked
    }

    override fun onResume() {
        super.onResume()
        var switchButton = findViewById<SwitchCompat>(R.id.switch_button)
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("MainActivity", "Switch is turned ON")
                startService(Intent(this, VisitService::class.java))
            } else {
                Log.d("MainActivity", "Switch is turned OFF")
                stopService(Intent(this, VisitService::class.java))
            }
        }
        if (isServiceRunning(this, VisitService::class.java))
            switchButton.isChecked
        else
            !switchButton.isChecked
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
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

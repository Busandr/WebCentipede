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


//activity shows link_list + settings + fab

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar_main = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar_main)


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
            val addLinkDialog = LayoutInflater.from(this).inflate(R.layout.add_link_dialog, null)

            val positiveButtonClick =
                { dialog: DialogInterface, which: Int ->

                    val textInput = addLinkDialog.findViewById<TextInputEditText>(R.id.textInputEditText)
                    val linkName = textInput.text.toString()


                    //add the link to list
                    if (linkName.isEmpty())
                        Toast.makeText(this, "Fill this", Toast.LENGTH_SHORT).show()
                    else {
                        linkList.add(Link(name = linkName))
                        linkAdapter.notifyItemInserted(linkList.lastIndex)

                        //now insert to db
                        var insertCheck = dbHelper.insertLink(Link(name = linkName))
                        //todo make warning for not saving
                        if (insertCheck < 0)
                            Toast.makeText(this, "Not saved", Toast.LENGTH_SHORT).show()
                    }

                }

            val negativeButtonClick = { dialog: DialogInterface, which: Int ->
                var site = addLinkDialog.findViewById<TextInputEditText>(R.id.textInputEditText_title)

                val str = site.text.toString()

                val intent = Intent(this, BrowserActivity::class.java)
                intent.putExtra("site",str)
                this.startActivity(intent)

            }


            val builder = AlertDialog.Builder(this).setView(addLinkDialog)

            //set the alert dialog
            builder.setTitle("Adding new link")
                .setMessage("Add new link?")
                .setPositiveButton("OK", positiveButtonClick)
                .setNegativeButton("Cancel", negativeButtonClick)
                .create()
                .show()

            builder.setCancelable(true)

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

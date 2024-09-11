package com.busandr.webcentipede

class HistoryActivity : FragmentActivity(){
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_history)

    val toolbarHistory = findViewById<Toolbar>(R.id.toolbar_history)
    val dbHelper = DatabaseHelper.DatabaseManager.getInstance(this)

    var linkInstance: Link = dbHelper?.readLinkById(intent.getStringExtra("link_id").toString())!!
    var textHistory: TextView = findViewById(R.id.text_history)
    val buttonPrevVersion: Button = findViewById(R.id.prev_version)
    val buttonNextVersion: Button = findViewById(R.id.next_version)

    textHistory.text = linkInstance.content
    
  }
}

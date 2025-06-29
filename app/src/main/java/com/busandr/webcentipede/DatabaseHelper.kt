package com.busandr.webcentipede

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context, DATABASE_VERSION: Int) :
    SQLiteOpenHelper(context, "links_base", null, DATABASE_VERSION) {
    object DatabaseManager {
        private var databaseHelper: DatabaseHelper? = null
        fun getInstance(context: Context?): DatabaseHelper? {
            if (databaseHelper == null) {
                databaseHelper = DatabaseHelper(context!!, 1)
            }
            return databaseHelper
        }
    }

    private val DB_VERSION = DATABASE_VERSION

    companion object {
        private const val DATABASE_NAME = "WebCentipedeDatabase"
        private const val TABLE_NAME = "links"
        private val TAG = "DatabaseHelper"

        const val NAME = "name"
        const val URL = "url"
        const val ID = "id"
        const val FAV = "favicon"
        const val CONTENT = "content"
        const val DATETIME = "datetime"
    }


    override fun onCreate(db: SQLiteDatabase) {
        Log.i(TAG, "onCreate $DB_VERSION, $TABLE_NAME")
        val CREATE_USER_TABLE = ("CREATE TABLE " + TABLE_NAME +
                "(" +
                NAME + " TEXT, " +
                URL + " TEXT, " +
                ID + " TEXT, " +
                FAV + " BLOB, " +
                DATETIME + " TEXT, " +
                CONTENT + " TEXT" +
                ")")
        db.execSQL(CREATE_USER_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (db.needUpgrade(newVersion)) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
            onCreate(db)
        }

        when (oldVersion) {
            1 -> {
                db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $CONTENT TEXT")
            }

        }
    }


    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 2 && newVersion == 1) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    fun deleteTable(db: SQLiteDatabase) {
        val deleteQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(deleteQuery)
    }

    fun readAllLinks(): MutableList<Link> {
        val list: MutableList<Link> = mutableListOf()
        val db = this.readableDatabase
        val query = "WITH ranked AS (\n" +
                "    SELECT *,\n" +
                "           ROW_NUMBER() OVER (PARTITION BY $URL) as row_num\n" +
                "    FROM $TABLE_NAME)" +
                "SELECT *\n" +
                "FROM ranked\n" +
                "WHERE row_num = 1;"

        val result = try {
            db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        var name: String
        val creationTime: Long = System.currentTimeMillis()
        var url: String
        var id: String
        var favicon: ByteArray
        var datetime: String
        var content: String
        if (result.moveToFirst()) {
            do {
                name = result.getString(result.getColumnIndexOrThrow("name"))
                url = result.getString(result.getColumnIndexOrThrow("url"))
                id = result.getString(result.getColumnIndexOrThrow("id"))
                favicon = result.getBlob(result.getColumnIndexOrThrow("favicon"))
                    ?: "fav is lost".toByteArray()
                content =
                    result.getString(result.getColumnIndexOrThrow("content"))// ?: "content is lost"
                datetime =
                    result.getString(result.getColumnIndexOrThrow("datetime"))// ?: "content is lost"
                val link =
                    Link(name, url, id, favicon, datetime, content)//, creationTime, url, favicon)
                Log.i(TAG, link.toString())
                list.add(link)
            } while (result.moveToNext())
        }
        result.close()
        return list
    }

    fun insertLink(link: Link): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(NAME, link.name)
            put(URL, link.url)
            put(ID, link.id)
            put(FAV, link.favicon)
            put(DATETIME, link.dateTime)
            put(CONTENT, link.content)
        }
        val success = db.insert(TABLE_NAME, null, values)
        db.close()
        Log.i(TAG, "insertLink has worked with $success")
        return success
    }

    fun readSnapshot(url: String): Map<String, String>? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $URL = ? ORDER BY $DATETIME DESC LIMIT 1"
        val cursor: Cursor = db.rawQuery(query, arrayOf(url))

        return if (cursor.moveToFirst()) {
            val result = mutableMapOf<String, String>()
            result[DATETIME] = cursor.getString(cursor.getColumnIndexOrThrow(DATETIME))
            result[URL] = cursor.getString(cursor.getColumnIndexOrThrow(URL))
            result[CONTENT] = cursor.getString(cursor.getColumnIndexOrThrow(CONTENT))
            cursor.close()
            db.close()
            result
        } else {
            cursor.close()
            db.close()
            null
        }
    }

    fun readAllSnapshotsByURL(url: String): MutableList<Link> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $URL = ? ORDER BY $DATETIME DESC"//" LIMIT 1"
        val cursor: Cursor = db.rawQuery(query, arrayOf(url))
        val snapshots = mutableListOf<Link>()

        with(cursor) {
            while (moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val url = cursor.getString(cursor.getColumnIndexOrThrow("url"))
                val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
                val favicon =
                    cursor.getBlob(cursor.getColumnIndexOrThrow("favicon")) //?: "fav is lost".toByteArray()
                val datetime = cursor.getString(cursor.getColumnIndexOrThrow("datetime"))
                val content =
                    cursor.getString(cursor.getColumnIndexOrThrow("content"))// ?: "content is lost"

                val link = Link(name, url, id, favicon, datetime, content)
                snapshots.add(link)
            }
            cursor.close()
            db.close()
            return snapshots
        }
    }

    fun updateLink(link: Link): Int {
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put(NAME, link.name)
//            put(URL, link.url)
//            put(ID, link.id)
//            put(LASTCHECKTIME, link.lastCheckTime)
            // TODO: add LCT to existing links... or create new
//            put(LASTCHECKRESULT, link.lastCheckResult)
        }
        val success = db.update(TABLE_NAME, cv, "$ID = ?", arrayOf(link.id))
        db.close()
        return success
    }

    fun deleteLink(id: String): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_NAME, "id=?", arrayOf(id))
        db.close()
        return success
    }

    fun deleteAll(): Int {
        val db = this.writableDatabase
        val success = db.delete(DATABASE_NAME, null, null)
        Log.i(TAG, "$success deleteAll")
        db.close()
        return success
    }

    fun addColumn() {
        val db = writableDatabase // assume you have a SQLiteDatabase object
        db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $CONTENT TEXT")
    }
}

package com.busandr.webcentipede

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    object DatabaseManager {
        private var databaseHelper: DatabaseHelper? = null
        fun getInstance(context: Context?): DatabaseHelper? {
            if (databaseHelper == null) {
                databaseHelper = DatabaseHelper(context!!, 1)
            }
            return databaseHelper
        }
    }

    companion object {
        private const val DATABASE_NAME = "links_base.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "links"
        
        const val NAME = "name"
        const val URL = "url"
        const val ID = "id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_USER_TABLE = ("CREATE TABLE " + TABLE_NAME +
                " (" + 
                NAME + " TEXT," +
                URL + " TEXT," +
                ID + " TEXT" +
                ")" )
        db.execSQL(CREATE_USER_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
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

    fun readAll(): MutableList<Link> {
        val list: MutableList<Link> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val result = try {
            db.rawQuery(query, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(query)
            return ArrayList()
        }
        var name: String
        var url: String
        var id: String
        if (result.moveToFirst()) {
            do {
                name = result.getString(result.getColumnIndexOrThrow("name"))
                url = result.getString(result.getColumnIndexOrThrow("url"))
                id = result.getString(result.getColumnIndexOrThrow("id"))
                val link = Link(name, url,id)
                list.add(link)
            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }



    fun insertLink(link: Link): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(NAME, link.name)
            put(URL, link.url)
            put(ID, link.id)
        }
        val success = db.insert(TABLE_NAME, null, values)
        db.close()
        return success
    }

    fun updateLink(link: Link): Int{
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put(NAME, link.name)
            put(URL, link.url)
            put(ID, link.id)
        }
        val success = db.update(TABLE_NAME, cv, "id=" + link.id, null)
        db.close()
        return success
    }

    fun deleteLink(id: String): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_NAME, "id=?", arrayOf(id))
        db.close()
        return success
    }

    fun deleteAll(id: String): Int {
        val db = this.writableDatabase

        val success = db.delete(TABLE_NAME, null, null)

        db.close()
        return success
    }

}

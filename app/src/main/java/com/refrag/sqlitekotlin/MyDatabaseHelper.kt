package com.refrag.sqlitekotlin

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import org.jetbrains.anko.db.*


class MyDatabaseHelper(val ctx : Context) : ManagedSQLiteOpenHelper(ctx, "Favorite.db", null, 1) {
    companion object {
        private var instance: MyDatabaseHelper? = null
        private const val TABLE_NAME = "my_barang"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_TITLE = "barang_title"
        private const val COLUMN_TIME = "barang_time"
        private const val COLUMN_LOCATION = "barang_loc"
        private const val COLUMN_IMAGE = "barang_image"

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseHelper {
            if (instance == null) {
                instance = MyDatabaseHelper(ctx.applicationContext)
            }
            return instance as MyDatabaseHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable(
            "my_barang", true,
            "_id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            "barang_title" to TEXT,
            "barang_time" to TEXT,
            "barang_loc" to TEXT,
            "barang_image" to BLOB
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("my_barang", true)
    }

    fun addBook(
        title: String?,
        time: String?,
        location: String?,
        image: ByteArray?
    ) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(MyDatabaseHelper.COLUMN_TITLE, title)
        cv.put(MyDatabaseHelper.COLUMN_TIME, time)
        cv.put(MyDatabaseHelper.COLUMN_LOCATION, location)
        cv.put(MyDatabaseHelper.COLUMN_IMAGE, image)
        val result = db.insert(MyDatabaseHelper.TABLE_NAME, null, cv)
        if (result == -1L) {
            Toast.makeText(ctx, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(ctx, "Barang Ditambahkan", Toast.LENGTH_SHORT).show()
        }
    }

    fun readAllData(): Cursor? {
        val query = "SELECT * FROM " + MyDatabaseHelper.TABLE_NAME
        val db = this.readableDatabase
        var cursor: Cursor? = null
        if (db != null) {
            cursor = db.rawQuery(query, null)
        }
        return cursor
    }

    fun updateData(
        row_id: String,
        title: String?,
        time: String?,
        location: String?,
        image: ByteArray?
    ) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(MyDatabaseHelper.COLUMN_TITLE, title)
        cv.put(MyDatabaseHelper.COLUMN_TIME, time)
        cv.put(MyDatabaseHelper.COLUMN_LOCATION, location)
        cv.put(MyDatabaseHelper.COLUMN_IMAGE, image)
        val result =
            db.update(MyDatabaseHelper.TABLE_NAME, cv, "_id=?", arrayOf(row_id))
                .toLong()
        if (result == -1L) {
            Toast.makeText(ctx, "Failed to Update.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(ctx, "Successfully Update!.", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteOneRow(row_id: String) {
        val db = this.writableDatabase
        val result =
            db.delete(MyDatabaseHelper.TABLE_NAME, "_id=?", arrayOf(row_id)).toLong()
        if (result == -1L) {
            Toast.makeText(ctx, "Failed to Delete", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(ctx, "Successfully Delete", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteAllData() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM " + MyDatabaseHelper.TABLE_NAME)
    }

    fun fetchAnImage(id:String): Bitmap {
        val db = this.writableDatabase
        val cursor =
            db.rawQuery("SELECT * FROM my_barang WHERE _id = $id", null)

        var img = byteArrayOf()
        while (cursor.moveToNext()) {
            img = cursor.getBlob(4)
        }
        return BitmapFactory.decodeByteArray(img, 0, img.size)
    }

}

// Access property for Context
val Context.database: MyDatabaseHelper
    get() = MyDatabaseHelper.getInstance(applicationContext)
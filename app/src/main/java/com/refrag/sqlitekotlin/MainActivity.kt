package com.refrag.sqlitekotlin

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var customAdapter: CustomAdapter? = null

    private lateinit var myDB : MyDatabaseHelper
    private lateinit var barang_id : MutableList<String>
    private lateinit var barang_title : MutableList<String>
    private lateinit var barang_time : MutableList<String>
    private lateinit var barang_location : MutableList<String>
    private lateinit var barang_gambar : MutableList<Bitmap>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        floatingActionButton.setOnClickListener{
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            startActivityForResult(intent,99)
        }
        myDB = MyDatabaseHelper(this@MainActivity)
        barang_id = mutableListOf()
        barang_title = mutableListOf()
        barang_time = mutableListOf()
        barang_location = mutableListOf()
        barang_gambar = mutableListOf()
        storeDataInArrays()
        customAdapter = CustomAdapter(this@MainActivity,  barang_id, barang_title, barang_time, barang_location, barang_gambar)
        rv_barang.setAdapter(customAdapter)
        rv_barang.setLayoutManager(LinearLayoutManager(this@MainActivity))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                recreate()
            }
        }else if(requestCode == 99){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                recreate()
            }
        }
    }

    fun storeDataInArrays() {
        val cursor: Cursor = myDB.readAllData()!!
        try {
            if (cursor.count == 0) {
                empty_image!!.visibility = View.VISIBLE
                tv_nodata!!.visibility = View.VISIBLE
            } else {
                while (cursor.moveToNext()) {
                    barang_id!!.add(cursor.getString(0))
                    barang_title.add(cursor.getString(1))
                    barang_time.add(cursor.getString(2))
                    barang_location.add(cursor.getString(3))
                    val img = cursor.getBlob(4)
                    barang_gambar!!.add(BitmapFactory.decodeByteArray(img, 0, img.size))
                }
                empty_image!!.visibility = View.GONE
                tv_nodata!!.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.deleteall_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_all) {
            confirmDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete All?")
        builder.setMessage("Are you sure you want to delete all Data?")
        builder.setPositiveButton("Yes") { dialogInterface, i ->
            val myDB = MyDatabaseHelper(this@MainActivity)
            myDB.deleteAllData()
            //Refresh Activity
            val intent = Intent(this@MainActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No") { dialogInterface, i -> }
        builder.create().show()
    }
}

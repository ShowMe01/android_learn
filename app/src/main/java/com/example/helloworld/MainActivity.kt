package com.example.helloworld

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_item -> {
                Toast.makeText(baseContext, "addItem", Toast.LENGTH_LONG).show()
            }
            R.id.remove_item -> {
                Toast.makeText(baseContext, "remove_item", Toast.LENGTH_LONG).show()
            }
            R.id.more_1 -> {
                Toast.makeText(baseContext, "more_1", Toast.LENGTH_LONG).show()
            }
            R.id.more_2 -> {
                Toast.makeText(baseContext, "more_2", Toast.LENGTH_LONG).show()
            }
        }
        return true
    }
}
package com.example.helloworld.layoutinflater

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.helloworld.databinding.ActivityLayoutInflaterBinding

class LayoutInflaterActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LayoutInflaterActivity"
    }

    private lateinit var viewBinding: ActivityLayoutInflaterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLayoutInflaterBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//        viewBinding.rv.layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
        viewBinding.rv.adapter = BtnAdapter()
    }
}
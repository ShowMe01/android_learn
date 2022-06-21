package com.example.helloworld.rv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.R
import com.example.helloworld.databinding.ActivityRvBinding

class RvActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityRvBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRvBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        val data = generateData()
        viewBinding.rv.adapter = CoverFlowAdapter(this, data)
        viewBinding.rv.layoutManager = GalleryLayoutManager()
//        viewBinding.rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun generateData(): List<String> {
        val result = mutableListOf<String>()
        repeat(40) { index ->
            result.add("第${index}个item")
        }
        return result
    }
}
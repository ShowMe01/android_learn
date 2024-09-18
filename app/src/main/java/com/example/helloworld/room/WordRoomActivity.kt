package com.example.helloworld.room

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helloworld.R
import com.example.helloworld.application.MyApplication
import com.example.helloworld.base.BaseViewBindingActivity
import com.example.helloworld.databinding.ActivityWordRoomBinding

class WordRoomActivity : BaseViewBindingActivity<ActivityWordRoomBinding>() {

    private lateinit var wordViewModel: WordViewModel
    private val adapter by lazy { WordListAdapter() }

    private val newWordActivityRequestCode = 1


    override fun init() {

        wordViewModel = ViewModelProvider(
            this, WordViewModelFactory((application as MyApplication).repository)
        )[WordViewModel::class.java]

        wordViewModel.allWords.observe(this) { words: List<Word>? ->
            words?.let {
                adapter.submitList(it)
            }
        }
        vb.recyclerview.adapter = adapter
        vb.recyclerview.layoutManager = LinearLayoutManager(this)
        vb.fab.setOnClickListener {
            val intent = Intent(this, NewWordActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let {
                val word = Word(it)
                wordViewModel.insert(word)
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun viewBinding(): ActivityWordRoomBinding {
        return ActivityWordRoomBinding.inflate(layoutInflater)
    }
}
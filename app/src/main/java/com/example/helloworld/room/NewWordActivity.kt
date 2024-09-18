package com.example.helloworld.room

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import com.example.helloworld.base.BaseViewBindingActivity
import com.example.helloworld.databinding.ActivityNewWordBinding

class NewWordActivity : BaseViewBindingActivity<ActivityNewWordBinding>() {
    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }

    override fun init() {

        vb.buttonSave.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(vb.editWord.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val word = vb.editWord.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    override fun viewBinding(): ActivityNewWordBinding {
        return ActivityNewWordBinding.inflate(layoutInflater)
    }


}
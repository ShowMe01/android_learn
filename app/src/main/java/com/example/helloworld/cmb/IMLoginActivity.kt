package com.example.helloworld.cmb

import android.view.View
import android.widget.Toast
import com.example.helloworld.base.BaseViewBindingActivity
import com.example.helloworld.databinding.ActivityLoginImBinding

class IMLoginActivity : BaseViewBindingActivity<ActivityLoginImBinding>() {

    override fun init() {

        getUser()?.let {
            viewBinding.usernameEditText.setText(it.userid)
            viewBinding.passwordEditText.setText(it.pwd)
        }

        viewBinding.loginButton.setOnClickListener {
            val username = viewBinding.usernameEditText.text.toString()
            val password = viewBinding.passwordEditText.text.toString()
            // Here you would handle the login logic

            viewBinding.progressBar.visibility = View.VISIBLE
            viewBinding.loginButton.isEnabled = false

            XMPPManager.connect(
                username,
                password,
                XMPPManager.DOMAIN,
                XMPPManager.HOST,
                5222
            ) { success, em ->
                viewBinding.progressBar.visibility = View.GONE
                if (success) {
                    CMBChatActivity.start(this, username)
                    saveUser(username, password)
                    this.finish()
                } else {
                    Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show()
                    viewBinding.loginButton.isEnabled = true
                }
            }
        }
    }

    override fun viewBinding(): ActivityLoginImBinding {
        return ActivityLoginImBinding.inflate(layoutInflater)
    }
}
package com.example.helloworld.cmb

import android.view.View
import android.widget.Toast
import com.example.helloworld.base.BaseViewBindingActivity
import com.example.helloworld.databinding.ActivityLoginImBinding

class IMLoginActivity : BaseViewBindingActivity<ActivityLoginImBinding>() {

    override fun init() {

        getUser()?.let {
            vb.usernameEditText.setText(it.userid)
            vb.passwordEditText.setText(it.pwd)
        }

        vb.loginButton.setOnClickListener {
            val username = vb.usernameEditText.text.toString()
            val password = vb.passwordEditText.text.toString()
            // Here you would handle the login logic

            vb.progressBar.visibility = View.VISIBLE
            vb.loginButton.isEnabled = false

            XMPPManager.connect(
                username,
                password,
                XMPPManager.DOMAIN,
                XMPPManager.HOST,
                5222
            ) { success, em ->
                vb.progressBar.visibility = View.GONE
                if (success) {
                    IMMainActivity.start(this)
                    saveUser(username, password)
                    this.finish()
                } else {
                    Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show()
                    vb.loginButton.isEnabled = true
                }
            }
        }
    }

    override fun viewBinding(): ActivityLoginImBinding {
        return ActivityLoginImBinding.inflate(layoutInflater)
    }
}
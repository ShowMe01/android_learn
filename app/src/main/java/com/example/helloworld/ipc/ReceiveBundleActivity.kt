package com.example.helloworld.ipc

import com.example.helloworld.base.BaseViewBindingActivity
import com.example.helloworld.databinding.ActivityReceiveBundleBinding
import com.example.helloworld.screenshot.safe

class ReceiveBundleActivity : BaseViewBindingActivity<ActivityReceiveBundleBinding>() {

    override fun init() {
        val bundle = intent.getBundleExtra("keyExtra")
        val stringValue = bundle?.getString("key1")
        viewBinding.tvInfo.text = stringValue.safe()
    }

    override fun viewBinding(): ActivityReceiveBundleBinding {
        return ActivityReceiveBundleBinding.inflate(layoutInflater)
    }


}
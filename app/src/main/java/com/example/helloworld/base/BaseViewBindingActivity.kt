package com.example.helloworld.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.example.helloworld.launchmode.BaseActivity

abstract class BaseViewBindingActivity<VB : ViewBinding> : BaseActivity() {

    lateinit var viewBinding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = viewBinding()
        setContentView(viewBinding.root)
        init()
    }

    abstract fun init()

    abstract fun viewBinding(): VB

}
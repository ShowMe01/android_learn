package com.example.helloworld.cmb

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.helloworld.base.BaseVBFragment
import com.example.helloworld.databinding.LayoutFragmentProfileBinding

class ProfileFragment : BaseVBFragment<LayoutFragmentProfileBinding>() {
    override fun init() {

    }

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutFragmentProfileBinding {
        return LayoutFragmentProfileBinding.inflate(inflater)
    }
}
package com.example.helloworld.cmb

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.helloworld.base.BaseVBFragment
import com.example.helloworld.databinding.LayoutContactsBinding

class ContactsFragment : BaseVBFragment<LayoutContactsBinding>() {
    override fun init() {

    }

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutContactsBinding {
        return LayoutContactsBinding.inflate(inflater)
    }


}
package com.example.helloworld.cmb

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helloworld.base.BaseVBFragment
import com.example.helloworld.databinding.LayoutFragmentSessionsBinding


class SessionsFragment : BaseVBFragment<LayoutFragmentSessionsBinding>() {

    override fun init() {
        viewbinding.sessions.layoutManager = LinearLayoutManager(context)
    }

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutFragmentSessionsBinding {
        return LayoutFragmentSessionsBinding.inflate(inflater)
    }


}

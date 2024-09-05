package com.example.helloworld.cmb

import android.content.Context
import android.content.Intent
import androidx.viewpager2.widget.ViewPager2
import com.example.helloworld.R
import com.example.helloworld.base.BaseViewBindingActivity
import com.example.helloworld.databinding.LayoutActivityChatMainBinding


class IMMainActivity : BaseViewBindingActivity<LayoutActivityChatMainBinding>() {

    companion object{
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, IMMainActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun init() {
        initView()
    }

    private fun initView() {
        vb.viewPager.adapter = ImPagerAdapter(this)
        vb.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        vb.bottomNavigation.setSelectedItemId(R.id.nav_conversations)
                        vb.title.text = "会话"
                    }

                    1 -> {
                        vb.bottomNavigation.setSelectedItemId(R.id.nav_contacts)
                        vb.title.setText("联系人")
                    }

                    2 -> {
                        vb.bottomNavigation.setSelectedItemId(R.id.nav_profile)
                        vb.title.setText("个人")
                    }
                }
            }
        })

        vb.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.getItemId()) {
                R.id.nav_conversations -> {
                    vb.viewPager.setCurrentItem(0)
                    return@setOnItemSelectedListener true
                }

                R.id.nav_contacts -> {
                    vb.viewPager.setCurrentItem(1)
                    return@setOnItemSelectedListener true
                }

                R.id.nav_profile -> {
                    vb.viewPager.setCurrentItem(2)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    override fun viewBinding(): LayoutActivityChatMainBinding {
        return LayoutActivityChatMainBinding.inflate(layoutInflater)
    }
}
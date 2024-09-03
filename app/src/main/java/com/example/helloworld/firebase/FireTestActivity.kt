package com.example.helloworld.firebase

import android.os.Bundle
import com.example.helloworld.databinding.LayoutFirebaseActivityBinding
import com.example.helloworld.launchmode.BaseActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth


class FireTestActivity : BaseActivity() {

    private val TAG = "FireTestActivity"
    private lateinit var vb: LayoutFirebaseActivityBinding

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        vb = LayoutFirebaseActivityBinding.inflate(layoutInflater)
        setContentView(vb.root)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        vb.btnLog.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "itemName")
            mFirebaseAnalytics.logEvent("clickLogButton", bundle)
        }

        vb.btnLogin.setOnClickListener {

        }
    }

    // [START auth_fui_result]


}
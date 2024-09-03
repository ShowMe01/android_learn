package com.example.helloworld.cmb

import com.tencent.mmkv.MMKV


const val KEY_IM_USER_ID = "KEY_IM_USER_ID"
const val KEY_IM_USER_PWD = "KEY_IM_USER_PWD"

data class UserData(val userid: String, val pwd: String)


fun saveUser(userid: String, pwd: String) {
    MMKV.defaultMMKV().putString(KEY_IM_USER_ID, userid)
    MMKV.defaultMMKV().putString(KEY_IM_USER_PWD, pwd)
}

fun getUser(): UserData? {
    val userid = MMKV.defaultMMKV().getString(KEY_IM_USER_ID, "")
    val pwd = MMKV.defaultMMKV().getString(KEY_IM_USER_PWD, "")

    if (!userid.isNullOrEmpty() && !pwd.isNullOrEmpty()) {
        return UserData(userid, pwd)
    } else {
        return null
    }
}
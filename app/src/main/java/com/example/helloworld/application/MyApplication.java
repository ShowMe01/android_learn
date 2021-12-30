package com.example.helloworld.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.helloworld.BuildConfig;
import com.example.helloworld.lua.GlideImageProvider;
import com.example.helloworld.lua.LuaUtils;
import com.immomo.mls.MLSEngine;
import com.immomo.mls.global.LVConfigBuilder;

public class MyApplication extends Application {

    private String TAG = "MyApplication";
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Log.d(TAG, "onCreate");

        MLSEngine.init(this, BuildConfig.DEBUG)
                .setLVConfig(new LVConfigBuilder(this)
                        .setSdcardDir(LuaUtils.INSTANCE.getLuaSdDir())  //设置sdcard目录
                        .setRootDir(LuaUtils.INSTANCE.getLuaRootDir())    //设置lua根目录
                        .setImageDir(LuaUtils.INSTANCE.getLuaImgDir())  //设置lua图片根目录
                        .setCacheDir(LuaUtils.INSTANCE.getLuaCacheDir())  //设置lua缓存目录
                        .setGlobalResourceDir(LuaUtils.INSTANCE.getLuaResDir())  //设置资源文件目录
                        .build())
                .setImageProvider(new GlideImageProvider())//lua加载图片工具，不实现的话，图片无法展示
                .build(true);
    }

    public static Context getInstance() {
        return mContext;
    }

    public static String getPackageNameImpl() {
        String sPackageName = mContext.getPackageName();
        if (sPackageName.contains(":")) {
            sPackageName = sPackageName.substring(0, sPackageName.lastIndexOf(":"));
        }
        return sPackageName;
    }
}

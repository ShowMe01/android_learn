package com.example.helloworld.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.helloworld.BuildConfig;
import com.example.helloworld.lua.utils.GlideImageProvider;
import com.example.helloworld.lua.utils.LuaUtils;
import com.immomo.mls.HotReloadHelper;
import com.immomo.mls.MLSEngine;
import com.immomo.mls.adapter.MLSQrCaptureAdapter;
import com.immomo.mls.global.LVConfigBuilder;
import com.tencent.mmkv.MMKV;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyApplication extends Application {

    private String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.INSTANCE.init(this);

        /*MLSEngine.init(this, BuildConfig.DEBUG)
                .setLVConfig(new LVConfigBuilder(this)
                        .setSdcardDir(LuaUtils.INSTANCE.getLuaSdDir())  //设置sdcard目录
                        .setRootDir(LuaUtils.INSTANCE.getLuaRootDir())    //设置lua根目录
                        .setImageDir(LuaUtils.INSTANCE.getLuaImgDir())  //设置lua图片根目录
                        .setCacheDir(LuaUtils.INSTANCE.getLuaCacheDir())  //设置lua缓存目录
                        .setGlobalResourceDir(LuaUtils.INSTANCE.getLuaResDir())  //设置资源文件目录
                        .build())
                .setImageProvider(new GlideImageProvider())//lua加载图片工具，不实现的话，图片无法展示
                .build(true);*/

        MMKV.initialize(AppContext.INSTANCE.getAppContext());
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                Log.d(TAG, "onActivityResumed: activity: "+activity.getClass().getName());
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });

    }

    public static Context getInstance() {
        return AppContext.INSTANCE.getAppContext();
    }

    public static String getPackageNameImpl() {
        String sPackageName = AppContext.INSTANCE.getAppContext().getPackageName();
        if (sPackageName.contains(":")) {
            sPackageName = sPackageName.substring(0, sPackageName.lastIndexOf(":"));
        }
        return sPackageName;
    }
}

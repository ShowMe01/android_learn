package com.example.helloworld.util

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object ThreadUtil {

    private val executor by lazy { Executors.newScheduledThreadPool(10) }

    fun scheduleTask(
        r: Runnable,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ) {
        executor.scheduleAtFixedRate(r, initialDelay, period, unit)
    }

    fun executeTask(r: Runnable) {
        executor.execute(r)
    }
}
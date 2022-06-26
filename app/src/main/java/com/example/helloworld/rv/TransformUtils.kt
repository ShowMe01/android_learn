package com.example.helloworld.rv

import kotlin.math.pow
import kotlin.math.sin

object TransformUtils {

    fun mix(x: Float, y: Float, a: Float): Float {
        val ratio = Math.max(0f, Math.min(1f, a))
        return y * ratio + (1 - ratio) * x
    }

    fun easeInOutCubic(x: Float): Float {
        return if (x < 0.5) {
            4 * x * x * x
        } else {
            (1 - Math.pow(-2 * x + 2.0, 3.0) / 2).toFloat()
        }
    }

    fun easeOutQuint(x: Float): Float {
        return (1 - (1 - x).pow(5f))
    }

    fun easeOutSine(x: Float): Float {
        return sin((x * Math.PI) / 2).toFloat()
    }
}
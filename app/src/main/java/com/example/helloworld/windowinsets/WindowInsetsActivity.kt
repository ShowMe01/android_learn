package com.example.helloworld.windowinsets

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.helloworld.R

class WindowInsetsActivity : AppCompatActivity() {

    private var showBars = true
    private var fitsSystem = true;
    private var cutouts = false;
    private val colors = listOf(
        Color.TRANSPARENT,
        Color.argb(120, 200, 0, 0),
        Color.argb(120, 0, 200, 0),
        Color.argb(120, 0, 0, 200),
    )
    private var colorIndex = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_window_insets)

        findViewById<View>(R.id.showBars).setOnClickListener {
            showBars = !showBars
            val windowInsetsController =
                WindowCompat.getInsetsController(window, window.decorView)
            if (showBars) {
                windowInsetsController?.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                windowInsetsController?.show(WindowInsetsCompat.Type.statusBars());
            } else {
                windowInsetsController?.hide(WindowInsetsCompat.Type.statusBars());
            }
            refreshState()

        }

        refreshState()

        findViewById<View>(R.id.fitsSystem).setOnClickListener {
            fitsSystem = !fitsSystem
            WindowCompat.setDecorFitsSystemWindows(window, fitsSystem)
            refreshState()

        }

        findViewById<View>(R.id.cutouts).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                cutouts = !cutouts
                val params = window.attributes
                params.layoutInDisplayCutoutMode =
                    if (cutouts) WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES else WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
                window.attributes = params
                refreshState()

            }
        }

        findViewById<View>(R.id.statusColor).setOnClickListener {
            window.statusBarColor = colors[colorIndex]
            colorIndex = (colorIndex+1) % colors.size
            refreshState()
            
        }
    }

    fun refreshState() {
        findViewById<TextView>(R.id.showBarsState).text = if (showBars) "show" else "hide";
        findViewById<TextView>(R.id.fitsSystemState).text = fitsSystem.toString()
        findViewById<TextView>(R.id.cutoutsState).text = cutouts.toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            findViewById<TextView>(R.id.statusColorState).text = Color.valueOf(colors[colorIndex]).toString()
        }


    }
}
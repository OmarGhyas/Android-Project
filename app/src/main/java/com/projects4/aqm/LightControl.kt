package com.projects4.aqm

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class LightControl : AppCompatActivity() {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private var lightSwitch: SwitchCompat? = null
    private var prev: ImageView? = null
    private var lightIcon: ImageView? = null
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_light_control)

        context = this

        lightSwitch = findViewById(R.id.switchCompat)
        lightIcon = findViewById(R.id.lightIcon)
        prev = findViewById(R.id.back)

        // Check if views are null to prevent NullPointerException
        if (prev != null) {
            prev?.setOnClickListener { finish() }
        } else {
            Log.e("LightControl", "Back button not found")
        }

        if (lightSwitch != null) {
            lightSwitch?.setOnCheckedChangeListener { _, isChecked ->
                if (lightIcon != null) {
                    if (isChecked) {
                        lightIcon?.alpha = 1.0f   // bulb bright
                        Toast.makeText(context, "Light Turned ON", Toast.LENGTH_SHORT).show()
                    } else {
                        lightIcon?.alpha = 0.4f   // dim bulb
                        Toast.makeText(context, "Light Turned OFF", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Log.e("LightControl", "Light switch not found")
        }
    }
}

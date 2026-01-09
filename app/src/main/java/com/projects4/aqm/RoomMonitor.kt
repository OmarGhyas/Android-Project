package com.projects4.aqm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.projects4.aqm.controller.RoomDaoImplementation
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.sql.SQLException

class RoomMonitor : AppCompatActivity() {

    private var co2Field: TextView? = null
    private var tempField: TextView? = null
    private var humField: TextView? = null
    private var lightField: TextView? = null
    private var percField: TextView? = null
    private var comment: TextView? = null
    private var prev: ImageView? = null
    private var more: ImageView? = null
    private var db: FirebaseDatabase? = null
    private var pb: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.overview)

        // Intent Values
        val id = intent.getStringExtra("id")
        val title = intent.getStringExtra("title")
        val cp = intent.getStringExtra("capacity")
        val sz = intent.getStringExtra("size")

        // ProgressBar
        pb = findViewById(R.id.progress_circular)

        // Back Button
        prev = findViewById(R.id.back)
        prev?.setOnClickListener { finish() }

        // More Button
        more = findViewById(R.id.more)
        more?.setOnClickListener { view ->
            val context: Context = this
            val popup = PopupMenu(context, view)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.edit_room_menu, popup.menu)
            
            // Remove the redirect item
            popup.menu.removeItem(R.id.menu_predict)

            popup.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.menu_edit) {
                    Toast.makeText(context, "Updating element ...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, UpdateRoom::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("title", title)
                    intent.putExtra("capacity", cp)
                    intent.putExtra("size", sz)
                    context.startActivity(intent)
                } else if (item.itemId == R.id.menu_delete) {
                    Toast.makeText(context, "Deleting element ...", Toast.LENGTH_SHORT).show()
                    try {
                        if (id != null) {
                            RoomDaoImplementation().delete(id)
                            finish()
                        } else {
                            Toast.makeText(context, "Error: Room ID is missing", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: SQLException) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
                false
            }
            popup.show()
        }

        // Fields in the Layout
        co2Field = findViewById(R.id.co2_value)
        tempField = findViewById(R.id.temp_value)
        humField = findViewById(R.id.hum_value)
        lightField = findViewById(R.id.lux_value)
        percField = findViewById(R.id.perc_value)
        comment = findViewById(R.id.comment_value)

        // Fetch Realtime AQI Data
        FetchDelhiDataTask().execute()
    }

    private fun showBuzzingNotification(perc: String) {
        // Create a notification channel
        val channelId = "buzzing_channel"
        val channelName = "Buzzing Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        var channel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = NotificationChannel(channelId, channelName, importance)
        }

        // Configure additional channel settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel?.enableVibration(true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel?.vibrationPattern = longArrayOf(0, 500, 200, 500)
        }

        // Register the notification channel with the system
        val notificationManager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (channel != null) {
                notificationManager.createNotificationChannel(channel)
            }
        }

        // Create a notification builder
        val message = "AQI : $perc"
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.co2)
            .setContentTitle("Attention Needed!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)

        // Set vibration pattern
        val vibrationPattern = longArrayOf(0, 500, 200, 500)
        builder.setVibrate(vibrationPattern)

        // Create a unique notification ID
        val notificationId = 1
        notificationManager.notify(notificationId, builder.build())
    }

    @SuppressLint("StaticFieldLeak")
    private inner class FetchDelhiDataTask : AsyncTask<Void, Void, JsonObject?>() {
        override fun doInBackground(vararg voids: Void): JsonObject? {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.waqi.info/feed/delhi/?token=demo")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful && response.body != null) {
                        val json = response.body!!.string()
                        val jsonObject = Gson().fromJson(json, JsonObject::class.java)
                        return jsonObject.getAsJsonObject("data")
                    }
                }
            } catch (e: IOException) {
                Log.e("RoomMonitor", "Error fetching AQI", e)
            }
            return null
        }

        override fun onPostExecute(data: JsonObject?) {
            if (data != null) {
                try {
                    val aqi = data.get("aqi").asInt
                    val temperature = data.getAsJsonObject("iaqi").getAsJsonObject("t").get("v").asDouble
                    val humidity = data.getAsJsonObject("iaqi").getAsJsonObject("h").get("v").asDouble

                    co2Field?.text = aqi.toString()
                    tempField?.text = temperature.toString()
                    humField?.text = humidity.toString()


                    pb?.progress = aqi
                    percField?.text = aqi.toString()


                    val message: String
                    val color: Int

                    if (aqi <= 50) {
                        message = "Good"
                        color = Color.rgb(0, 153, 102) // Green
                    } else if (aqi <= 100) {
                        message = "Moderate"
                        color = Color.rgb(255, 222, 51) // Yellow
                    } else if (aqi <= 150) {
                        message = "Unhealthy for Sensitive Groups"
                        color = Color.rgb(255, 153, 51) // Orange
                    } else if (aqi <= 200) {
                        message = "Unhealthy"
                        color = Color.rgb(204, 0, 51) // Red
                        showBuzzingNotification("AQI: $aqi")
                    } else if (aqi <= 300) {
                        message = "Very Unhealthy"
                        color = Color.rgb(102, 0, 153) // Purple
                        showBuzzingNotification("AQI: $aqi")
                    } else {
                        message = "Hazardous"
                        color = Color.rgb(126, 0, 35)
                        showBuzzingNotification("AQI: $aqi")
                    }

                    comment?.text = message
                    comment?.setTextColor(color)

                    val layerDrawable = pb?.progressDrawable as LayerDrawable
                    layerDrawable.getDrawable(1).setColorFilter(color, PorterDuff.Mode.SRC_IN)

                } catch (e: Exception) {
                    Log.e("RoomMonitor", "Error parsing data", e)
                }
            }
        }
    }
}

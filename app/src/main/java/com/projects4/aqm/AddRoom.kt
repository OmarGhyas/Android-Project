package com.projects4.aqm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.projects4.aqm.controller.RoomDaoImplementation
import com.projects4.aqm.dto.Room
import java.sql.SQLException

class AddRoom : AppCompatActivity() {
    private var title: EditText? = null
    private var size: EditText? = null
    private var capacity: EditText? = null
    private var ok: Button? = null
    private var prev: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_room)

        title = findViewById(R.id.title)
        size = findViewById(R.id.size)
        capacity = findViewById(R.id.capacity)

        prev = findViewById(R.id.back)
        prev?.setOnClickListener { finish() }

        ok = findViewById(R.id.proceed)
        ok?.setOnClickListener {
            val impl = RoomDaoImplementation()
            try {
                val room = Room(
                    "unused",
                    title?.text.toString(),
                    capacity?.text.toString(),
                    size?.text.toString()
                )
                impl.insert(room)
                val intent = Intent(this, FloorsList::class.java)
                startActivity(intent)
            } catch (e: SQLException) {
                Toast.makeText(this, "Error" + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package com.projects4.aqm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.projects4.aqm.controller.RoomDaoImplementation
import com.projects4.aqm.dto.Room
import java.sql.SQLException
import java.util.Objects

class UpdateRoom : AppCompatActivity() {

    private var title: TextInputEditText? = null
    private var size: TextInputEditText? = null
    private var capacity: TextInputEditText? = null
    private var ok: Button? = null
    private var prev: ImageView? = null
    private var id: String? = null
    private var text: String? = null
    private var cp: String? = null
    private var sz: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_room)

        val intent = intent
        id = intent.getStringExtra("id")
        text = intent.getStringExtra("title")
        cp = intent.getStringExtra("capacity")
        sz = intent.getStringExtra("size")

        val impl = RoomDaoImplementation()

        title = findViewById(R.id.title)
        size = findViewById(R.id.size)
        capacity = findViewById(R.id.capacity)

        prev = findViewById(R.id.back)
        prev?.setOnClickListener { finish() }

        ok = findViewById(R.id.proceed)
        ok?.setOnClickListener {
            val currentId = id
            try {
                if (currentId != null && title?.text != null && capacity?.text != null && size?.text != null) {
                    impl.update(
                        Room(
                            currentId,
                            Objects.requireNonNull(title?.text).toString(),
                            Objects.requireNonNull(capacity?.text).toString(),
                            Objects.requireNonNull(size?.text).toString()
                        )
                    )
                    startActivity(Intent(this, FloorsList::class.java))
                }
            } catch (e: SQLException) {
                Toast.makeText(this, "Error" + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        title?.setText(text)
        capacity?.setText(cp)
        size?.setText(sz)
    }
}

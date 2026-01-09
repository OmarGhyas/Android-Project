package com.projects4.aqm

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projects4.aqm.adapters.MyAdapter
import com.projects4.aqm.controller.RoomDaoImplementation
import com.projects4.aqm.dto.Room
import java.util.LinkedList
import kotlin.concurrent.thread

class FloorsList : AppCompatActivity() {

    private lateinit var roomsRecycler: RecyclerView
    private var rooms: List<Room> = emptyList()
    private val dao = RoomDaoImplementation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floors_list)

        roomsRecycler = findViewById<RecyclerView>(R.id.list_rooms).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@FloorsList)
        }

        findViewById<View>(R.id.fab_add).setOnClickListener {
            startActivity(Intent(this, AddRoom::class.java))
        }
        findViewById<View>(R.id.back).setOnClickListener { finish() }

        findViewById<EditText>(R.id.search).doOnTextChanged { text, _, _, _ ->
            val query = text.toString()
            val filtered = if (query.isEmpty()) rooms else rooms.filter {
                it.title.contains(query, ignoreCase = true)
            }
            updateList(filtered)
        }
    }

    override fun onResume() {
        super.onResume()
        loadRooms()
    }

    private fun loadRooms() {
        val dialog = ProgressDialog.show(this, "", "Loading...", true)
        thread {
            val result = dao.all
            runOnUiThread {
                rooms = result
                updateList(rooms)
                dialog.dismiss()
            }
        }
    }

    private fun updateList(list: List<Room>) {
        roomsRecycler.adapter = MyAdapter(LinkedList(list), this)
    }
}

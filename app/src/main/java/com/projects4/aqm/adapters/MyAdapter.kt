package com.projects4.aqm.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.projects4.aqm.FloorsList
import com.projects4.aqm.R
import com.projects4.aqm.RoomMonitor
import com.projects4.aqm.UpdateRoom
import com.projects4.aqm.controller.RoomDaoImplementation
import com.projects4.aqm.dto.Room
import java.sql.SQLException
import java.util.LinkedList

class MyAdapter(rooms: LinkedList<Room>?, private val context: Context) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private val rooms: LinkedList<Room>

    init {
        this.rooms = LinkedList()
        if (rooms != null) {
            this.rooms.addAll(rooms)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLayoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.room_layout,
            parent, false
        )
        return MyViewHolder(itemLayoutView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val room = rooms[position]
        val ms1 = "Capacity : " + room.capacity + " persons"
        val ms2 = "Size : " + room.size + " meters squared"
        holder.room = room
        holder.identification.text = room.title
        holder.capacity.text = ms1
        holder.size.text = ms2

        // Update device status
        if (room.isFanPresent) {
            holder.fanIcon.alpha = 1.0f
        } else {
            holder.fanIcon.alpha = 0.3f // Dim if not present
        }

        if (room.isLightPresent) {
            holder.lightIcon.alpha = 1.0f
        } else {
            holder.lightIcon.alpha = 0.3f // Dim if not present
        }

        // Update environment values
        holder.tempText.text = "Temp: ${room.temperature}°C"
        holder.humText.text = "Hum: ${room.humidity}%"
    }

    override fun getItemCount(): Int {
        return rooms.size
    }

    inner class MyViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView),
        View.OnClickListener, View.OnLongClickListener {
        var room: Room? = null
        var identification: TextView = itemLayoutView.findViewById(R.id.identification)
        var capacity: TextView = itemLayoutView.findViewById(R.id.capacity)
        var size: TextView = itemLayoutView.findViewById(R.id.size)
        
        // New views
        var fanIcon: ImageView = itemLayoutView.findViewById(R.id.ic_fan)
        var lightIcon: ImageView = itemLayoutView.findViewById(R.id.ic_light)
        var tempText: TextView = itemLayoutView.findViewById(R.id.temp_text)
        var humText: TextView = itemLayoutView.findViewById(R.id.hum_text)
        
        var impl: RoomDaoImplementation = RoomDaoImplementation()

        init {
            itemLayoutView.setOnClickListener(this)
            itemLayoutView.setOnLongClickListener(this)
        }

        override fun onClick(v: View) {
            val intent = Intent(context, RoomMonitor::class.java)
            intent.putExtra("id", room?.id)
            intent.putExtra("title", room?.title)
            intent.putExtra("capacity", room?.capacity)
            intent.putExtra("size", room?.size)
            context.startActivity(intent)
        }

        override fun onLongClick(view: View): Boolean {
            val popup = PopupMenu(context, view)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.room_popup_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.menu_view) {
                    Toast.makeText(context, "Viewing element ...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, RoomMonitor::class.java)
                    intent.putExtra("id", room?.id)
                    intent.putExtra("title", room?.title)
                    intent.putExtra("capacity", room?.capacity)
                    intent.putExtra("size", room?.size)
                    context.startActivity(intent)
                } else if (item.itemId == R.id.menu_edit) {
                    Toast.makeText(context, "Updating element ...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, UpdateRoom::class.java)
                    intent.putExtra("id", room?.id)
                    intent.putExtra("title", room?.title)
                    intent.putExtra("capacity", room?.capacity)
                    intent.putExtra("size", room?.size)
                    context.startActivity(intent)
                } else if (item.itemId == R.id.menu_delete) {
                    Toast.makeText(context, "Deleting element ...", Toast.LENGTH_SHORT).show()
                    try {
                        room?.let { impl.delete(it.id) }
                        context.startActivity(Intent(context, FloorsList::class.java))
                    } catch (e: SQLException) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
                false
            }
            popup.show()
            return true
        }
    }
}

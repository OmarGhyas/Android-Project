package com.projects4.aqm.controller

import com.projects4.aqm.dto.Room
import com.projects4.aqm.interfaces.RoomDao
import com.projects4.aqm.model.Model
import java.sql.SQLException

class RoomDaoImplementation : RoomDao {
    private var model: Model = Model()

    @Throws(SQLException::class)
    override fun get(id: String): Room? {
        return model.get(id)
    }

    override val all: List<Room>
        get() = model.all

    @Throws(SQLException::class)
    override fun insert(room: Room) {
        model.insert(room)
    }

    @Throws(SQLException::class)
    override fun update(room: Room) {
        model.update(room)
    }

    @Throws(SQLException::class)
    override fun delete(id: String) {
        model.delete(id)
    }
}

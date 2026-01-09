package com.projects4.aqm.interfaces

import java.sql.SQLException

interface Dao<T> {
    @Throws(SQLException::class)
    fun get(id: String): T?

    @get:Throws(SQLException::class)
    val all: List<T>

    @Throws(SQLException::class)
    fun insert(t: T)

    @Throws(SQLException::class)
    fun update(t: T)

    @Throws(SQLException::class)
    fun delete(id: String)
}

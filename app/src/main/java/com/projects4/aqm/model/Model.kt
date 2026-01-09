package com.projects4.aqm.model

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.projects4.aqm.dto.Room
import java.util.concurrent.ExecutionException

class Model {
    private val db = FirebaseFirestore.getInstance()

    fun get(id: String): Room? {
        val task = db.collection("rooms").document(id).get()
        return try {
            val doc = Tasks.await(task)
            if (doc.exists()) {
                Room(
                    id,
                    doc.getString("title")!!,
                    doc.getString("capacity"),
                    doc.getString("size"),
                    doc.getBoolean("isFanPresent") ?: false,
                    doc.getBoolean("isLightPresent") ?: false,
                    doc.getDouble("temperature") ?: 0.0,
                    doc.getDouble("humidity") ?: 0.0
                )
            } else {
                Log.d("not ok", "Document does not exist")
                null
            }
        } catch (e: Exception) {
            Log.w("not ok", "Error finding document", e)
            null
        }
    }

    val all: List<Room>
        get() {
            val rooms = ArrayList<Room>()
            val terminated = BooleanArray(1)

            db.collection("rooms").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val title = document["title"].toString()
                            // Filter out specific room names
                            val blockedNames = listOf("Adam", "Hostel", "Joseph", "Adam Hostel")
                            
                            if (blockedNames.none { it.equals(title, ignoreCase = true) }) {
                                rooms.add(
                                    Room(
                                        document.id,
                                        title,
                                        document["capacity"].toString(),
                                        document["size"].toString(),
                                        document.getBoolean("isFanPresent") ?: true,
                                        document.getBoolean("isLightPresent") ?: true,
                                        document.getDouble("temperature") ?: 24.5,
                                        document.getDouble("humidity") ?: 45.0
                                    )
                                )
                            }
                        }
                        terminated[0] = true
                    } else Log.d("not ok", "Error getting documents: ", task.exception)
                }

            while (!terminated[0]) println("...")
            return rooms
        }

    fun insert(room: Room) {
        val r = hashMapOf(
            "title" to room.title,
            "capacity" to room.capacity,
            "size" to room.size,
            "isFanPresent" to room.isFanPresent,
            "isLightPresent" to room.isLightPresent,
            "temperature" to room.temperature,
            "humidity" to room.humidity
        )
        
        db.collection("rooms")
            .add(r)
            .addOnSuccessListener { Log.w("ok", "Added Successfully") }
            .addOnFailureListener { e -> Log.w("not ok", "Error adding document", e) }
    }

    fun update(room: Room) {
        val updates = mapOf(
            "title" to room.title,
            "capacity" to room.capacity,
            "size" to room.size,
            "isFanPresent" to room.isFanPresent,
            "isLightPresent" to room.isLightPresent,
            "temperature" to room.temperature,
            "humidity" to room.humidity
        )

        db.collection("rooms")
            .document(room.id)
            .update(updates)
            .addOnSuccessListener { Log.d("ok", "Fields updated successfully") }
            .addOnFailureListener { e -> Log.w("not ok", "Error updating fields", e) }
    }

    fun delete(id: String) {
        db.collection("rooms").document(id).delete()
    }
}

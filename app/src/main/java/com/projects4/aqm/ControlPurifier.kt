package com.projects4.aqm

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ControlPurifier : AppCompatActivity() {

    private var status: TextView? = null
    private var fan: ImageView? = null
    private var prev: ImageView? = null
    private var db: FirebaseDatabase? = null

    // ObjectAnimator rotationAnimator;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private var aSwitch: Switch? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_fan)

        prev = findViewById(R.id.back)
        prev?.setOnClickListener { finish() }

        fan = findViewById(R.id.fan)
        status = findViewById(R.id.status)


        db = FirebaseDatabase.getInstance()
        val fanDb = db?.getReference("isFanEnabled")

        aSwitch = findViewById(R.id.switchCompat)
        aSwitch?.setOnClickListener {
            val message: String
            if (aSwitch?.isChecked == true) {
                fanDb?.setValue("True")
                message = "STATUS : ON"
                aSwitch?.isChecked = true
                status?.text = message
            } else {
                fanDb?.setValue("False")
                message = "STATUS : OFF"
                aSwitch?.isChecked = false
                status?.text = message
            }
        }

        fanDb?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(String::class.java)
                if (value != null) {

                    val message: String
                    if (value.trim { it <= ' ' } == "False") {
                        message = "STATUS : OFF"
                        aSwitch?.isChecked = false
                    } else {
                        message = "STATUS : ON"
                        aSwitch?.isChecked = true
                    }
                    status?.text = message

                    Log.d("DataChange", "Value is: $value")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Cancelled", "Failed to read value.", error.toException())
            }
        })
    }
}

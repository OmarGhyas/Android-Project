package com.projects4.aqm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projects4.aqm.authentication.LoginActivity
import java.util.Objects

class DashBoard : AppCompatActivity() {

    private var view1: CardView? = null
    private var view2: CardView? = null
    private var view3: CardView? = null
    private var view4: CardView? = null
    private var username: TextView? = null
    private var org: TextView? = null
    private var email: TextView? = null
    private var settings: ImageView? = null
    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        db = FirebaseFirestore.getInstance()

        view1 = findViewById(R.id.card_view_2)
        view2 = findViewById(R.id.card_view_3)
        view3 = findViewById(R.id.card_view_4)
        view4 = findViewById(R.id.card_view_5)

        username = findViewById(R.id.title_value)
        org = findViewById(R.id.building)
        email = findViewById(R.id.email)

        if (currentUser != null) {
            db?.collection("users")
                ?.document(Objects.requireNonNull(currentUser.email!!))?.get()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val doc = task.result
                        val ms1 = "Address : " + doc.getString("building")
                        val ms2 = "Hello, " + doc.getString("name")
                        val ms3 = "Email : " + currentUser.email

                        org?.text = ms1
                        username?.text = ms2
                        email?.text = ms3
                    }
                }
                ?.addOnFailureListener { e -> Log.e("not ok", "Error occurred", e) }
        }

        settings = findViewById(R.id.settings)

        view1?.setOnClickListener { startActivity(Intent(this, FloorsList::class.java)) }
        view2?.setOnClickListener { startActivity(Intent(this, RoomMonitor::class.java)) }
        view3?.setOnClickListener { startActivity(Intent(this, ControlPurifier::class.java)) }
        view4?.setOnClickListener { startActivity(Intent(this, LightControl::class.java)) }

        settings?.setOnClickListener { view ->
            val context: Context = this
            val popup = PopupMenu(context, view)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.settings, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.logout) {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                false
            }
            popup.show()
        }
    }
}

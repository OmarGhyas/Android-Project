package com.projects4.aqm.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.projects4.aqm.DashBoard
import com.projects4.aqm.R
import java.util.HashMap

class SignupActivity : AppCompatActivity(), View.OnClickListener {

    private var login: TextView? = null
    private var nameInp: EditText? = null
    private var orgInp: EditText? = null
    private var emailInp: EditText? = null
    private var passwordInp: EditText? = null
    private var signup: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        nameInp = findViewById(R.id.name)
        orgInp = findViewById(R.id.org)
        emailInp = findViewById(R.id.email)
        passwordInp = findViewById(R.id.password)
        signup = findViewById(R.id.signup_button)
        login = findViewById(R.id.signin_button)
        signup?.setOnClickListener(this)
        login?.setOnClickListener(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth?.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            Toast.makeText(this, currentUser.email, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DashBoard::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(view: View) {
        if (view.id == R.id.signup_button) {
            val name = nameInp?.text.toString()
            val org = orgInp?.text.toString()
            val email = emailInp?.text.toString()
            val password = passwordInp?.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill the required fields!", Toast.LENGTH_SHORT).show()
            } else {
                mAuth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user: MutableMap<String, Any> = HashMap()
                            user["name"] = name
                            user["building"] = org
                            user["password"] = password.hashCode()

                            db?.collection("users")
                                ?.document(email)?.set(user)
                                ?.addOnSuccessListener {
                                    Log.d("ok", "DocumentSnapshot added with ID: $email")
                                }
                                ?.addOnFailureListener { e ->
                                    Log.w("not ok", "Error adding document", e)
                                }

                            Log.d("Ok", "createUserWithEmail:success")
                            Toast.makeText(this, "Authentication success.", Toast.LENGTH_SHORT)
                                .show()

                            val cuser = mAuth?.currentUser
                            updateUI(cuser)
                        } else {
                            Log.w("Error", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        } else if (view.id == R.id.signin_button) {
            finish()
        }
    }
}

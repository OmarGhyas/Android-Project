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
import com.projects4.aqm.DashBoard
import com.projects4.aqm.R

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var signup: TextView? = null
    private var re: TextView? = null
    private var loginEmail: EditText? = null
    private var loginPassword: EditText? = null
    private var loginButton: Button? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginEmail = findViewById(R.id.login_email)
        loginPassword = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        signup = findViewById(R.id.create_account)
        re = findViewById(R.id.forgot_password)
        loginButton?.setOnClickListener(this)
        signup?.setOnClickListener(this)
        re?.setOnClickListener(this)
        mAuth = FirebaseAuth.getInstance()
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
        if (view.id == R.id.login_button) {
            val email = loginEmail?.text.toString()
            val password = loginPassword?.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill the required fields!", Toast.LENGTH_SHORT).show()
            } else {
                mAuth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("Ok", "signInWithEmail:success")
                            Toast.makeText(this, "Authentication Success.", Toast.LENGTH_SHORT).show()
                            val user = mAuth?.currentUser
                            updateUI(user)
                        } else {
                            Log.w("Error", "signInWithEmail:failure", task.exception)
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }
            }
        } else if (view.id == R.id.create_account) {
            startActivity(Intent(this, SignupActivity::class.java))
        } else if (view.id == R.id.forgot_password) {
            Toast.makeText(this, "PASSWORD RESET", Toast.LENGTH_SHORT).show()
        }
    }
}

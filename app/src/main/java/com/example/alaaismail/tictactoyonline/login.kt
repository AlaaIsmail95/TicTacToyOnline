package com.example.alaaismail.tictactoyonline

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class login : AppCompatActivity() {

    /**
     *default user name = "alaa@gmail.com" and Password is "123456"
     **/
    private var auth: FirebaseAuth? = null


    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.

        updateUI()

    }

    fun buSignUpEvent(view: View) {
        val email = et_name.text.toString()
        val password = et_password.text.toString()
        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth!!.currentUser
                    if (user != null) {
                        myRef.child("User").child(splitString(user.email.toString())).child("Request")
                            .setValue(user.uid)
                        Log.d("", "createUserWithEmail:success")

                    }
                    updateUI()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                // ...
            }
    }

    fun buLoginEvent(view: View) {

        auth!!.signInWithEmailAndPassword(et_name.text.toString(), et_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("", "signInWithEmail:success")
                    val user = auth!!.currentUser
                    updateUI()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                // ...
            }

    }

    fun updateUI() {
        var user = auth!!.currentUser
        if (user != null) {
            //save in the database

            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("Email", user.email)
            intent.putExtra("UID", user.uid)
            startActivity(intent)

        }
    }

    fun splitString(str: String): String {
        var split = str.split("@")
        return split[0]
    }
}

package com.example.alaaismail.tictactoyonline

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var auth: FirebaseAuth? = null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference

    var myEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        auth = FirebaseAuth.getInstance()

        var bundle: Bundle = intent.extras
        myEmail = bundle.getString("Email")
        incomingCalls()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.log_out -> {
                    auth!!.signOut()
                    var intent = Intent(this, login::class.java)
                    startActivity(intent)

                }


            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun buSelect(view: View) {
        val buChoise = view as Button
        var cellID = 0
        when (buChoise.id) {
            R.id.button1 -> cellID = 1
            R.id.button2 -> cellID = 2
            R.id.button3 -> cellID = 3
            R.id.button4 -> cellID = 4
            R.id.button5 -> cellID = 5
            R.id.button6 -> cellID = 6
            R.id.button7 -> cellID = 7
            R.id.button8 -> cellID = 8
            R.id.button9 -> cellID = 9

        }
        myRef.child("playerOnline").child(sessionID!!).child(cellID.toString()).setValue(myEmail)

    }

    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()
    var activePlayer = 1
    fun playGame(cellID: Int, buChoise: Button) {

        if (activePlayer == 1) {
            buChoise.text = "X"
            buChoise.setBackgroundResource(R.color.blue)
            player1.add(cellID)
            activePlayer = 2
            //AutoPlay()

        } else {
            buChoise.text = "O"
            buChoise.setBackgroundResource(R.color.darkgreen)
            player2.add(cellID)
            activePlayer = 1
        }
        buChoise.isEnabled = false

        checkTheWinner()
    }

    fun checkTheWinner() {

        var winner = -1

        if (player1.contains(1) && player1.contains(2) && player1.contains(3)) {
            winner = 1
        }
        if (player2.contains(1) && player2.contains(2) && player2.contains(3)) {
            winner = 2
        }
        if (player1.contains(4) && player1.contains(5) && player1.contains(6)) {
            winner = 1
        }
        if (player2.contains(4) && player2.contains(5) && player2.contains(6)) {
            winner = 2
        }
        if (player1.contains(7) && player1.contains(8) && player1.contains(9)) {
            winner = 1
        }
        if (player2.contains(7) && player2.contains(8) && player2.contains(9)) {
            winner = 2
        }

        if (player1.contains(1) && player1.contains(4) && player1.contains(7)) {
            winner = 1
        }
        if (player2.contains(1) && player2.contains(4) && player2.contains(7)) {
            winner = 2
        }
        if (player1.contains(2) && player1.contains(5) && player1.contains(8)) {
            winner = 1
        }
        if (player2.contains(2) && player2.contains(5) && player2.contains(8)) {
            winner = 2
        }
        if (player1.contains(3) && player1.contains(6) && player1.contains(9)) {
            winner = 1
        }
        if (player2.contains(3) && player2.contains(6) && player2.contains(9)) {
            winner = 2
        }

        if (player1.contains(1) && player1.contains(5) && player1.contains(9)) {
            winner = 1
        }
        if (player2.contains(1) && player2.contains(5) && player2.contains(9)) {
            winner = 2
        }
        if (player1.contains(3) && player1.contains(5) && player1.contains(7)) {
            winner = 1
        }
        if (player2.contains(3) && player2.contains(5) && player2.contains(7)) {
            winner = 2
        }

        if (winner != -1) {


            if (winner == 1) {
                Toast.makeText(this, "Player 1 win", Toast.LENGTH_LONG).show()
            }
            if (winner == 2) {
                Toast.makeText(this, "Player 2 win", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun AutoPlay(cellID: Int) {


        var buselect: Button?
        when (cellID) {
            1 -> buselect = button1
            2 -> buselect = button2
            3 -> buselect = button3
            4 -> buselect = button5
            6 -> buselect = button6
            7 -> buselect = button7
            8 -> buselect = button8
            9 -> buselect = button9
            else -> {
                buselect = button1

            }
        }
        playGame(cellID, buselect)
    }

    fun buRequestEvent(view: View) {
        var userEmail = player_email.text.toString()
        myRef.child("User").child(splitString(userEmail)).child("Request").push().setValue(myEmail)
        playerOnline(splitString(myEmail!!) + splitString(userEmail))
        playerSymbol = "X"
    }

    fun buAcceptEvent(view: View) {
        var userEmail = player_email.text.toString()
        myRef.child("User").child(splitString(myEmail!!)).child("Request").push().setValue(userEmail)
        playerOnline(splitString(userEmail) + splitString(myEmail!!))
        playerSymbol = "O"
    }

    var sessionID: String? = null
    var playerSymbol: String? = null

    fun playerOnline(sessionID: String) {

        this.sessionID = sessionID
        myRef.child("playerOnline").removeValue()
        myRef.child("playerOnline").child(sessionID).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                try{
                    player1.clear()
                    player2.clear()
                    val td = p0.value as HashMap<String , Any>
                    if (td != null){
                        var value:String
                        for (key in td.keys){
                            value = td[key] as String

                            if (value != myEmail){
                                activePlayer = if(playerSymbol ==="X")1 else 2
                            }else{
                                activePlayer = if(playerSymbol ==="X")2 else 1
                            }
                            AutoPlay(key.toInt())
                        }

                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

        })
    }

    fun incomingCalls() {
        myRef.child("User").child(splitString(myEmail!!)).child("Request")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(p0: DataSnapshot) {

                    try {
                        val td = p0.value as HashMap<String, Any>
                        if (td != null) {
                            var value: String
                            for (key in td.keys) {
                                value = td[key] as String
                                player_email.setText(value)
                                myRef.child("User").child(splitString(myEmail!!)).child("Request").setValue(true)
                                break
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
    }

    fun splitString(str: String): String {
        var split = str.split("@")
        return split[0]
    }
}

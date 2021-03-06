package com.example.pokerproject.ui.login


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pokerproject.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class CreateGameActivity : AppCompatActivity() {

    private lateinit var sharedpreferences: SharedPreferences
    lateinit var userpass: String
    lateinit var gameList: ArrayList<Game>
    lateinit var username: String
    lateinit var password: String
    inline fun Gson.fromJson(json: String) = fromJson<ArrayList<Game>>(
        json,
        object : TypeToken<ArrayList<Game>>() {}.type
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // game parameter inputs
        val date = findViewById<TextView>(R.id.dateValue)
        val smallblind = findViewById<TextView>(R.id.smallBlindValue)
        val bigblind = findViewById<TextView>(R.id.bigBlindValue)
        val buyin = findViewById<TextView>(R.id.buyinValue)
        val cashout = findViewById<TextView>(R.id.cashoutValue)
        val location = findViewById<TextView>(R.id.locValue)
        val gameTypes = resources.getStringArray(R.array.GameTypes)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val duration = findViewById<TextView>(R.id.durValue)

        // username and password
        username = intent.getStringExtra("Username").toString()
        password = intent.getStringExtra("Password").toString()
        userpass = username+password

        //Setting Game Type Selector
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, gameTypes)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        // get and build JSON
        sharedpreferences = getSharedPreferences(LoginActivity.mypreference, Context.MODE_PRIVATE)
        var json = sharedpreferences.getString(userpass, null)
        gameList = json?.let { Gson().fromJson(it) }!!

        // addGame button functionality
        val addGame = findViewById<Button>(R.id.addGame)
        addGame.setOnClickListener{

            // if date is in a valid format, add the game!
            if (!dateValidator(date.text.toString())) {
                Toast.makeText(this, "Invalid Date Format.\nFormat: XX/XX/XXXX\nEX: 12/07/2020", Toast.LENGTH_LONG).show()
            } else if(!inputIsEmpty(date, location, duration, smallblind, bigblind, buyin, cashout)){
                add(
                    date.text.toString(),
                    location.text.toString(),
                    duration.text.toString().toDouble(),
                    spinner.selectedItem.toString(),
                    smallblind.text.toString().toDouble(),
                    bigblind.text.toString().toDouble(),
                    buyin.text.toString().toDouble(),
                    cashout.text.toString().toDouble(),
                )

                // success game added toast
                Toast.makeText(this, "Game Successfully Added.", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "Error: Missing values", Toast.LENGTH_SHORT).show()
            }
        }

        // show games button functionality
        val showGames = findViewById<Button>(R.id.showGames)
        showGames.setOnClickListener{

            // see if there are any games to show, If so, start activity
            if (gameList.isNotEmpty()) {
                val intent = Intent(applicationContext, ShowGamesActivity::class.java)
                    .putExtra("GameList", gameList)
                    .putExtra("username", username)
                    .putExtra("password", password)
                    .putExtra("userpass", userpass)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "No Games Left.\nAdd Game(s).", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun nextID(): Int {
        if(gameList.isNotEmpty()){
            return gameList[gameList.size-1].ID + 1
        }
        return 1
    }

    private fun add(date: String, location: String, duration: Double, gametype: String, smallblind: Double, bigblind: Double, buyin: Double, cashout: Double){
        gameList.add(Game(date, location, duration, gametype, smallblind, bigblind, buyin, cashout, nextID()))
        save()
    }

    private fun save(){
        val editor = sharedpreferences.edit()
        var gson = Gson()
        var json = gson.toJson(gameList)
        editor.putString(userpass, json)
        editor.apply()
    }

    // Ex: 12/07/2020
    private fun dateValidator(date: String) : Boolean {
        val dateRegex = Regex("^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d\$")
        return dateRegex.matches(date)
    }
    
    private fun inputIsEmpty(date: TextView, location: TextView, duration: TextView, smallblind: TextView, bigblind: TextView, buyin: TextView, cashout: TextView): Boolean {
        return date.text.toString().trim().isEmpty() ||
                location.text.toString().trim().isEmpty() ||
                duration.text.toString().trim().isEmpty() ||
                smallblind.text.toString().trim().isEmpty() ||
                bigblind.text.toString().trim().isEmpty() ||
                buyin.text.toString().trim().isEmpty() ||
                cashout.text.toString().trim().isEmpty()
    }

    override fun onBackPressed() {
        if(gameList.isEmpty()){
            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logging Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes"
                ) { _ , _ -> finish() }
                .setNegativeButton("No", null)
                .show()
        }else{
            val intent = Intent(applicationContext, ShowGamesActivity::class.java)
                .putExtra("GameList", gameList)
                .putExtra("username", username)
                .putExtra("password", password)
                .putExtra("userpass", userpass)
            startActivity(intent)
            finish()
        }
    }

}

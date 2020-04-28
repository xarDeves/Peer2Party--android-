package com.cups.splashin.peer2party.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.cups.splashin.peer2party.R

//TODO replace image on StartupScreen with .svg
class StartupScreen : AppCompatActivity() {

    private lateinit var connectBtn: Button
    private lateinit var nickNameText: EditText
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        connectBtn = findViewById(R.id.initMainBtn)
        nickNameText = findViewById(R.id.userNameText)

        sharedPrefs = getSharedPreferences("id", Context.MODE_PRIVATE)
        nickNameText.setText(sharedPrefs.getString("ID", ""))

        //TODO push "ID" to "ChatFragment"
        connectBtn.setOnClickListener {

            val ID = nickNameText.text.toString()
            if (ID.isNotEmpty() || ID.isNotBlank()) {
                sharedPrefs.edit().putString("ID", ID).apply()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("ID", ID)
                startActivity(intent)
                finish()
            }
        }

    }
}

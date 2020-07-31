package com.cups.splashin.peer2party.android.app.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cups.splashin.peer2party.R
import java.io.File

//TODO replace image on StartupScreen with .svg
class StartupScreen : AppCompatActivity() {

    private lateinit var connectBtn: Button
    private lateinit var alias: EditText
    private lateinit var sharedPrefs: SharedPreferences

    //i just copied this part from stack overflow because fuck this OS
    //fuck my life, and fuck the reason why this is necessary.
    //WHAT IS THE FUCKING PURPOSE OF MANIFEST PERMISSIONS THEN?
    //FIX YOUR FUCKING SHIT GOOGLE, JESUS
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    private fun createDirectory(name: String) {
        // create a directory before creating a new file inside it.
        val directory = File(Environment.getExternalStorageDirectory(), name)
        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        connectBtn = findViewById(R.id.initMainBtn)
        alias = findViewById(R.id.userNameText)

        sharedPrefs = getSharedPreferences("id", Context.MODE_PRIVATE)
        alias.setText(sharedPrefs.getString("ID", ""))

        createDirectory("Peer2Party")
        createDirectory("Peer2Party/videos")
        createDirectory("Peer2Party/images")
        createDirectory("Peer2Party/audio")
        createDirectory("Peer2Party/other")

        //TODO these one at a time:
        verifyStoragePermissions(this)

        connectBtn.setOnClickListener {

            val ID = alias.text.toString()
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

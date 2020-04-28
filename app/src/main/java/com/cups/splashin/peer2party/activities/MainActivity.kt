package com.cups.splashin.peer2party.activities

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cups.splashin.peer2party.R
import com.cups.splashin.peer2party.fragments.ChatFragment
import com.cups.splashin.peer2party.fragments.PeerListFragment
import com.cups.splashin.peer2party.viewmodels.MainActivityViewModel
import kotlin.math.max
import kotlin.math.min
import kotlin.system.exitProcess


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {


    private lateinit var viewModel: ViewModel
    private lateinit var ID: String
    private val bundle: Bundle = Bundle()
    private var doubleBackCheck = false

    private lateinit var fragmentManager: FragmentManager
    private lateinit var transactionManager: androidx.fragment.app.FragmentTransaction


    //i just copied this part from stack overflow because fuck this OS
    //fuck my life, and fuck the reason why this is necessary.
    //WHAT IS THE FUCKING PURPOSE OF MANIFEST PERMISSIONS THEN?
    //FIX YOUR FUCKING SHIT GOOGLE, JESUS
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
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

    //this, as well as "ThumbnailMaker.kt" are most possibly deprecated
    //since minWidth won't work on ImageView i'm keeping it for the time being
    //(although it's not applicable anywhere as of now.)
    private fun fetchScreenDimensions() {
        //fetching device's screen size in pixels and passing it to viewModel
        //for thumbnail downscale/upscale (min/max is necessary for orientation change):
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        (viewModel as MainActivityViewModel).screenW = min(height, width)
        (viewModel as MainActivityViewModel).screenH = max(height, width)
    }


    override fun onBackPressed() {

        if (fragmentManager.backStackEntryCount == 1) {
            if (doubleBackCheck) {
                super.onBackPressed()
                finishAffinity()
                exitProcess(0)
            }

            this.doubleBackCheck = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed({ doubleBackCheck = false }, 2000)

        } else {
            super.onBackPressed()

        }

    }

    private fun replaceLandscape(
        fragmentA: ChatFragment,
        fragmentB: PeerListFragment
    ) {

        transactionManager.replace(
            R.id.chatFrame,
            fragmentA,
            "A"
        )

        transactionManager.replace(
            R.id.peerListFrame,
            fragmentB,
            "B"
        )
        transactionManager.commit()
        fragmentManager.executePendingTransactions()


    }

    private fun replacePortrait(fragmentA: ChatFragment) {

        transactionManager.replace(
            R.id.chatFrame,
            fragmentA,
            "A"
        )
        transactionManager.commit()
        fragmentManager.executePendingTransactions()

    }

    private fun addLandscape(fragmentA: ChatFragment, fragmentB: PeerListFragment) {

        transactionManager.add(
            R.id.chatFrame,
            fragmentA,
            "A"
        )

        transactionManager.add(
            R.id.peerListFrame,
            fragmentB,
            "B"
        )

        transactionManager.addToBackStack("fragmentStack")
        transactionManager.commit()
        fragmentManager.executePendingTransactions()
    }

    private fun addPortrait(fragmentA: ChatFragment) {

        transactionManager.add(
            R.id.chatFrame,
            fragmentA,
            "A"
        )
        transactionManager.addToBackStack("fragmentStack")
        transactionManager.commit()
        fragmentManager.executePendingTransactions()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        //in order to save to device's storage:
        verifyStoragePermissions(this)
        fetchScreenDimensions()

        ID = intent.extras.getString("ID")
        bundle.putString("ID", ID)
        (viewModel as MainActivityViewModel).fragmentA.arguments = bundle
        (viewModel as MainActivityViewModel).connect(ID)

        fragmentManager = supportFragmentManager
        transactionManager = fragmentManager.beginTransaction()

        //are you feeling it now mr. krabs? :
        if (findViewById<LinearLayout>(R.id.activity_portrait) != null) {
            if (savedInstanceState == null) {
                addPortrait((viewModel as MainActivityViewModel).fragmentA)
            } else {
                replacePortrait((viewModel as MainActivityViewModel).fragmentA)
            }
        } else if (findViewById<LinearLayout>(R.id.activity_landscape) != null) {
            if (savedInstanceState == null) {
                addLandscape(
                    (viewModel as MainActivityViewModel).fragmentA,
                    (viewModel as MainActivityViewModel).fragmentB
                )
            } else {
                replaceLandscape(
                    (viewModel as MainActivityViewModel).fragmentA,
                    (viewModel as MainActivityViewModel).fragmentB
                )
            }
        }

    }

}
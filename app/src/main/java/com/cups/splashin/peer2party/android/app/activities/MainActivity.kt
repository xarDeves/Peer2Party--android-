package com.cups.splashin.peer2party.android.app.activities

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.cups.splashin.peer2party.R
import com.cups.splashin.peer2party.android.app.MainActivityViewModel
import com.cups.splashin.peer2party.android.app.PeerTransaction
import com.cups.splashin.peer2party.android.app.adapters.ViewPagerAdapter
import com.cups.splashin.peer2party.android.app.fragments.ChatFragment
import com.cups.splashin.peer2party.android.app.fragments.PeerListFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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

        if (doubleBackCheck) {
            super.onBackPressed()
            finishAffinity()
            exitProcess(0)
        }

        doubleBackCheck = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackCheck = false }, 2000)

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

    /*private fun replacePortrait(fragmentA: ChatFragment) {

        transactionManager.replace(
            R.id.chatFrame,
            fragmentA,
            "A"
        )
        transactionManager.commit()
        fragmentManager.executePendingTransactions()

    }*/

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

        transactionManager.commit()
        fragmentManager.executePendingTransactions()
    }

    private fun addPortrait(fragmentA: ChatFragment) {

        transactionManager.add(
            R.id.chatFrame,
            fragmentA,
            "A"
        )
        transactionManager.commit()
        fragmentManager.executePendingTransactions()

    }

    override fun onDestroy() {

        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onStart() {

        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    //TODO unsubscribe onStop
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEvent(event: PeerTransaction) {
        Log.d("fuck", "fetching peers")
        (viewModel as MainActivityViewModel).fetchPeers()
        Log.d("fuck", "setting peers on fragment")
        (viewModel as MainActivityViewModel).fragmentB.peersAdapter.setPeers((viewModel as MainActivityViewModel).peers!!)
        Log.d("fuck", "peers set")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        //in order to save to device's storage:
        verifyStoragePermissions(this)
        fetchScreenDimensions()

        //TODO use service or timer thread to check for active wifi connection
        if (savedInstanceState == null) {
            ID = intent.extras.getString("ID")
            bundle.putString("ID", ID)
            (viewModel as MainActivityViewModel).fragmentA.arguments = bundle
            try {
                (viewModel as MainActivityViewModel).connect(ID)
            } catch (t: Throwable) {
                Toast.makeText(this, t.message, Toast.LENGTH_LONG).show()
            }
        }

        fragmentManager = supportFragmentManager
        transactionManager = fragmentManager.beginTransaction()

        //are you feeling it now mr. krabs? :
        if (findViewById<LinearLayout>(R.id.activity_portrait) != null) {
            val viewPagerAdapter = ViewPagerAdapter(
                fragmentManager,
                viewModel as MainActivityViewModel
            )
            val viewPager: ViewPager = findViewById(R.id.viewpager)
            viewPager.adapter = viewPagerAdapter
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
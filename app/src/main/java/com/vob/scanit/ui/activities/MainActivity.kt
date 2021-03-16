package com.vob.scanit.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.rbddevs.splashy.Splashy
import com.vob.scanit.R

import com.vob.scanit.setAppTheme


import com.vob.scanit.adapters.PDFAdapter
import com.vob.scanit.ui.fragments.DocumentFragment


import com.vob.scanit.ui.fragments.HomeFragment
import com.vob.scanit.ui.fragments.OCRFragment
import com.vob.scanit.ui.fragments.QRFragment

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 7
    private var imageView: ImageView? = null
    private lateinit var bottomNavigationView: BottomNavigationView

    var sharedPreferences: SharedPreferences? = null
    private val SHARED_PREF = "APP_SHARED_PREF"

    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_nav_view)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setSplashScreen()

        sharedPreferences = getSharedPreferences(
                SHARED_PREF,
                Context.MODE_PRIVATE
        )
        setAppTheme(sharedPreferences?.getInt("theme_mode", 0)?:0)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(applicationContext)
       //bottomNavigationView = findViewById(R.id.bottom_nav_view)
        loadFragment(HomeFragment())

        menu.setOnItemSelectedListener {id ->
            when(id) {
                R.id.home ->
                {
                    R.color.home to "Home"
                    loadFragment(HomeFragment())
                    return@setOnItemSelectedListener
                }
                R.id.like -> {
                    R.color.like to "Like"
                    loadFragment(OCRFragment())
                    return@setOnItemSelectedListener
                }
                R.id.search -> {
                    R.color.serach to "Search"
                    loadFragment(QRFragment())
                    return@setOnItemSelectedListener
                }
                R.id.profile -> {
                    R.color.profile to "Profile"
                    loadFragment(DocumentFragment())
                    return@setOnItemSelectedListener
                }
                else -> {
                    R.color.white to ""
                    loadFragment(HomeFragment())
                    return@setOnItemSelectedListener
                }
            }

        }
//        bottomNavigationView.setOnNavigationItemSelectedListener {
//            when(it.itemId)
//            {
//                R.id.home -> {
//                    loadFragment(HomeFragment())
//                    return@setOnNavigationItemSelectedListener true
//                }
//                R.id.ocr -> {
//                    loadFragment(OCRFragment())
//                    return@setOnNavigationItemSelectedListener true
//                }
//                R.id.qr -> {
//                    loadFragment(QRFragment())
//                    return@setOnNavigationItemSelectedListener true
//                }
//                R.id.documents -> {
//                    loadFragment(DocumentFragment())
//                    return@setOnNavigationItemSelectedListener true
//                }
//                else ->
//                {
//                    loadFragment(HomeFragment())
//                    return@setOnNavigationItemSelectedListener true
//                }
//            }
//        }

        setupToolbar()
    }

    private fun setSplashScreen() {
        Splashy(this)
                .setLogo(R.mipmap.scanit_icon_round)
                .setTitle("ScanIt")
                .setTitleColor("#FFFFFF")
                .setSubTitle("Your All-In-One Scanner")
                .setSubTitleColor("#FFFFFF")
                .setProgressColor(R.color.white)
                .setBackgroundColor(R.color.colorPrimaryDark)
                .setFullScreen(true)
                .setDuration(5000)
                .show()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        //toolbar.setTitle("ScanIt")
        //toolbar.setTitleTextAppearance(applicationContext, R.style.TextAppearance_AppCompat_Headline)
       // toolbar.setTitleTextColor(-0x1)
        toolbar.inflateMenu(R.menu.toolbar_menu)

        toolbar.setOnMenuItemClickListener{
            when(it.itemId)
            {
                /*R.id.about_item ->
                {
                    val intent = Intent(this, AboutAppActivity::class.java)
                    startActivity(intent)
                }*/
                R.id.settings_item -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        return super.onCreateOptionsMenu(menu)
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
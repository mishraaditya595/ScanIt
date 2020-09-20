package com.openscan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.monscanner.ScanActivity
import com.monscanner.ScanConstants
import com.openscan.ui.fragments.HomeFragment
import java.io.FileNotFoundException
import java.util.*

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 7
    private var imageView: ImageView? = null
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //imageView = findViewById(R.id.finalImageView)


        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        loadFragment(HomeFragment())
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else ->
                {
                    loadFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }

        setupToolbar()
    }

    private fun setupToolbar() {
        var toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.setTitle("ScanIt")
        toolbar.setTitleTextAppearance(applicationContext, R.style.TextAppearance_AppCompat_Title)
        toolbar.setTitleTextColor(-0x1)
        setSupportActionBar(toolbar)
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun openGalerie(view: View?) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
            startScan(ScanConstants.OPEN_GALERIE)
        }
    }

    fun openCamera(view: View?) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        } else {
            startScan(ScanConstants.OPEN_CAMERA)
        }
    }

    private fun startScan(preference: Int) {
        val intent = Intent(this, ScanActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                try {
                    assert(data != null)
                    val imageUri = Objects.requireNonNull(data!!.extras).getParcelable<Uri>(ScanActivity.SCAN_RESULT)!!
                    val imageStream = contentResolver.openInputStream(imageUri)
                    val scannedImage = BitmapFactory.decodeStream(imageStream)
                    contentResolver.delete(imageUri, null, null)
                    imageView!!.setImageBitmap(scannedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
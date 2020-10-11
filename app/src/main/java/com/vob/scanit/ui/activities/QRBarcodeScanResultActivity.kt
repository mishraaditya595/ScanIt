package com.vob.scanit.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.vob.scanit.R

class QRBarcodeScanResultActivity : AppCompatActivity() {

    lateinit var scanData: String
    lateinit var copyToClipboardBtn: Button
    lateinit var openInBrowserBtn: Button
    lateinit var scanResultTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_q_r_barcode_scan_result)

        scanData = intent.getStringExtra("result")!!

        initialiseFields()

        setupToolbar()
        showResult()

    }

    private fun showResult() {
        val isUrl: Boolean = URLUtil.isValidUrl(scanData)
        scanResultTV.text = scanData
        if (isUrl)
            Toast.makeText(applicationContext,"Is URL",Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(applicationContext,"Not URL",Toast.LENGTH_SHORT).show()
    }

    private fun initialiseFields() {
        copyToClipboardBtn = findViewById(R.id.copy_clip_btn)
        openInBrowserBtn = findViewById(R.id.openInBrowser_btn)
        scanResultTV = findViewById(R.id.scanResult_tv)
    }

    private fun setupToolbar() {
        var toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.title = "Scan Result"
        toolbar.setTitleTextAppearance(applicationContext, R.style.TextAppearance_AppCompat_Title)
        toolbar.setTitleTextColor(-0x1)
        setSupportActionBar(toolbar)
    }
}
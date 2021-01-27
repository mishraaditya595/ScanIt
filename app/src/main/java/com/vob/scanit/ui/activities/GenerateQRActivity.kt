package com.vob.scanit.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.google.zxing.WriterException
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.vob.scanit.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.util.*

class GenerateQRActivity : AppCompatActivity() {

    private lateinit var urlEditText: EditText
    private lateinit var qrImageView: ImageView
    private lateinit var generateBtn: Button
    private lateinit var shareBtn: Button
    private lateinit var saveBtn: Button
    lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_q_r)

        setupToolbar()

        initialiseFields()

        var bitmap: Bitmap? = null

        generateBtn.setOnClickListener {
            val inputValue = urlEditText.text.toString()

            if (inputValue.isNotEmpty()) {
                val qrgEncoder = QRGEncoder(inputValue, null, QRGContents.Type.TEXT, 300)
                qrgEncoder.colorBlack = Color.BLACK
                qrgEncoder.colorWhite = Color.WHITE
                try {
                    // Getting QR-Code as Bitmap
                    bitmap = qrgEncoder.bitmap
                    // Setting Bitmap to ImageView
                    qrImageView.setImageBitmap(bitmap)
                } catch (e: WriterException) {
                    //Log.v(TAG, e.toString())
                }
            } else {
                DynamicToast.makeError(applicationContext, "Cannot generate QR for null input", Toast.LENGTH_SHORT).show()
            }

        }

        /*shareBtn.setOnClickListener {

            val uri = Uri.fromFile(bitmap.`)

            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = "application/pdf"
            startActivity(Intent.createChooser(intent, "Share via"))
        }*/

        saveBtn.setOnClickListener {
            saveQR(bitmap)
        }

        shareBtn.setOnClickListener {
            saveQR(bitmap)
            val uri = FileProvider.getUriForFile(applicationContext, "com.vob.scanit.provider", file)
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = "*/*"
            startActivity(Intent.createChooser(intent, "Share via"))
        }

    }

    private fun saveQR(bitmap: Bitmap?) {
        if (bitmap != null) {
            val root = File(Environment.getExternalStorageDirectory().absolutePath, "Scanner")
            val folder = File(root, "QR Code")
            //val file = File(folder, "${url_et.toString()} $currentDateTimeString .png")
//            val isDirectoryCreated: Boolean = if (folder.exists()) {
//                folder.exists()
//            } else {
//                folder.mkdirs()
//            }

            val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date()).toString()
            val filename = urlEditText.text.toString() + "_" + currentDateTimeString + ".jpeg"
            file = File(folder, filename)
            try {
                val fileOutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                fileOutputStream.close()
                fileOutputStream.flush()
                DynamicToast.makeSuccess(applicationContext, "Saved in storage.").show()
            } catch (e: IOException) {
                DynamicToast.makeError(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            DynamicToast.makeError(applicationContext, "Generate a QR first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initialiseFields() {
        urlEditText = findViewById(R.id.url_et)
        qrImageView = findViewById(R.id.qr_IV)
        generateBtn = findViewById(R.id.generate_btn)
        shareBtn = findViewById(R.id.share_qr_button)
        saveBtn = findViewById(R.id.save_qr_button)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.generate_qr_toolbar)
        toolbar.title = "Generate QR"
        toolbar.setTitleTextAppearance(applicationContext, R.style.TextAppearance_AppCompat_Title)
        toolbar.setTitleTextColor(-0x1)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
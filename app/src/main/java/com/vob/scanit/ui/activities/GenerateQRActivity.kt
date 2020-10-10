package com.vob.scanit.ui.activities

import android.R.attr.bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.WriterException
import com.vob.scanit.R

class GenerateQRActivity : AppCompatActivity() {

    lateinit var url_et: EditText
    lateinit var qr_iv: ImageView
    lateinit var generateBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_q_r)

        initialiseFields()

        generateBtn.setOnClickListener {
            var inputValue = url_et.text.toString()

            if (inputValue.isNotEmpty())
            {
                val qrgEncoder = QRGEncoder(inputValue, null, QRGContents.Type.TEXT, 300)
                qrgEncoder.colorBlack = Color.BLACK
                qrgEncoder.colorWhite = Color.WHITE
                try
                {
                    // Getting QR-Code as Bitmap
                    var bitmap = qrgEncoder.bitmap
                    // Setting Bitmap to ImageView
                    qr_iv.setImageBitmap(bitmap)
                }
                catch (e: WriterException)
                {
                    //Log.v(TAG, e.toString())
                }
            }
            else
            {
                Toast.makeText(applicationContext,"Cannot generate QR for null input",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun initialiseFields() {
        url_et = findViewById(R.id.url_et)
        qr_iv = findViewById(R.id.qr_IV)
        generateBtn = findViewById(R.id.generate_btn)
    }
}
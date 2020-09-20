package com.openscan.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.getbase.floatingactionbutton.FloatingActionButton
import com.monscanner.ScanActivity
import com.monscanner.ScanConstants
import com.openscan.BuildConfig
import com.openscan.R
import org.jetbrains.annotations.Nullable
import java.io.*
import java.util.*


class HomeFragment : Fragment() {

    lateinit var openCameraButton: FloatingActionButton
    lateinit var openFilesButton: FloatingActionButton
    var bitmap: Bitmap? = null
    private val REQUEST_CODE = 7
    private var imageView: ImageView? = null
    lateinit var pdfDocument: PdfDocument
    val fileName = "newCodeTest.pdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        initialiseFields(view)

        openCameraButton.setOnClickListener { openCamera(view) }
        openFilesButton.setOnClickListener { openGallery(view) }

        return view
    }

    private fun initialiseFields(view: View?) {
        openCameraButton = view?.findViewById(R.id.openCameraButton)!!
        openFilesButton = view.findViewById(R.id.openFilesButton)!!
    }

    private fun openCamera(view: View?) {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        } else {
            startScan(ScanConstants.OPEN_CAMERA)
        }
    }

    private fun openGallery(view: View?) {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
            startScan(ScanConstants.OPEN_GALERIE)
        }
    }

    private fun startScan(preference: Int) {
        val intent:Intent = Intent(context!!, ScanActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                try {
                    if (BuildConfig.DEBUG && data == null) {
                        error("Assertion failed")
                    }
                    val imageUri: Uri = Objects.requireNonNull(data!!.extras).getParcelable(ScanActivity.SCAN_RESULT)!!
                    val imageStream: InputStream = activity!!.contentResolver.openInputStream(imageUri)!!
                    val scannedImage = BitmapFactory.decodeStream(imageStream)
                    activity!!.contentResolver.delete(imageUri, null, null)
                    makePDF(scannedImage)
                }
                catch (e: FileNotFoundException)
                {
                    e.printStackTrace()
                }
            }
        }
    }

    fun makePDF(bitmap: Bitmap) {
        pdfDocument = PdfDocument()
        val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page: PdfDocument.Page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.getCanvas()
        val paint = Paint()
        paint.setColor(Color.parseColor("#FFFFFF"))
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null)
        pdfDocument.finishPage(page)
        //if (fileName.getText().toString().isEmpty()) {
        //    Toast.makeText(context, "You need to enter file name as follow\nyour_fileName.pdf", Toast.LENGTH_SHORT).show()
        //}
        saveFile()
    }


    fun saveFile() {
        if (pdfDocument == null) {
            return
        }
        val root = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Scanner")
        var isDirectoryCreated: Boolean = root.exists()
        if (!isDirectoryCreated) {
            isDirectoryCreated = root.mkdir()
        }
        if (checkFileName()) {
            val userInputFileName: String = fileName//.getText().toString()
            val file = File(root, userInputFileName)
            try {
                val fileOutputStream = FileOutputStream(file)
                pdfDocument.writeTo(fileOutputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            pdfDocument.close()
        }

        Toast.makeText(context, "Successful! PATH: Internal Storage/${Environment.DIRECTORY_DOWNLOADS}".trimIndent(), Toast.LENGTH_SHORT).show()
    }

    private fun checkFileName(): Boolean {
        return true
    }


}

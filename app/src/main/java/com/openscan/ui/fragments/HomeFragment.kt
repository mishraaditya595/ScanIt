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
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.getbase.floatingactionbutton.FloatingActionButton
import com.monscanner.ScanActivity
import com.monscanner.ScanConstants
import com.openscan.BuildConfig
import com.openscan.PDFProcessing
import com.openscan.R
import com.openscan.adapters.PDFAdapter
import org.jetbrains.annotations.Nullable
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    lateinit var openCameraButton: FloatingActionButton
    lateinit var openFilesButton: FloatingActionButton
    var bitmap: Bitmap? = null
    private val REQUEST_CODE = 7
    private var imageView: ImageView? = null
    lateinit var pdfDocument: PdfDocument
    val fileName = "newCodeTest.pdf"

    lateinit var listView: ListView
    lateinit var pdfAdapter: PDFAdapter
    lateinit var dir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        initialiseFields(view)
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
        listView = view.findViewById(R.id.listView)
        dir = File(Environment.getExternalStorageDirectory().absolutePath, "Scanner")
        val listOfFiles = getFiles(dir)

        pdfAdapter = PDFAdapter(activity?.applicationContext,listOfFiles)
        listView.adapter = pdfAdapter
    }

    private fun getFiles(dir: File):ArrayList<File> {
        val listFiles: Array<File> = dir.listFiles()
        var fileList: ArrayList<File> = ArrayList()

        if (listFiles != null && listFiles.size > 0) {
            for (item in listFiles) {
                if (item.isDirectory) {
                    getFiles(item)
                } else {
                    var flag: Boolean = false
                    if (item.name.endsWith(".pdf")) {
                        for (element in fileList) {
                            if (element.name.equals(item.name)) {
                                flag = true
                            }
                        }
                        if (flag) {
                            flag = false
                        } else {
                            fileList.add(item)
                        }
                    }
                }
            }
        }
        return fileList
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
                    PDFProcessing().makePDF(scannedImage)
                    Toast.makeText(context, "Successful! PATH: Internal Storage/${Environment.getExternalStorageDirectory().absolutePath}/Scanner".trimIndent(), Toast.LENGTH_SHORT).show()
                }
                catch (e: FileNotFoundException)
                {
                    e.printStackTrace()
                }
            }
        }
    }


}

package com.vob.scanit.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.getbase.floatingactionbutton.FloatingActionButton
import com.monscanner.ScanActivity
import com.monscanner.ScanConstants
import com.vob.scanit.BottomSheetDialog
import com.vob.scanit.BuildConfig
import com.vob.scanit.PDFProcessing
import com.vob.scanit.R
import com.vob.scanit.adapters.PDFAdapter
import com.vob.scanit.ui.activities.DisplayPDFActivity
import org.jetbrains.annotations.Nullable
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {


    lateinit var openCameraButton: FloatingActionButton
    lateinit var openFilesButton: FloatingActionButton
    var bitmap: Bitmap? = null
    private val REQUEST_CODE = 7
    private var imageView: ImageView? = null
    lateinit var pdfDocument: PdfDocument
    lateinit var listView: ListView
    lateinit var pdfAdapter: PDFAdapter
    lateinit var dir: File
    lateinit var listOfFiles: ArrayList<File>


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

        checkForStoragePermissions()

        initialiseFields(view)

        openCameraButton.setOnClickListener { openCamera(view) }
        openFilesButton.setOnClickListener { openGallery(view) }

        return view
    }

    private fun checkForStoragePermissions() {
        //camera permissions
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        }

        //storage permissions
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

    private fun initialiseFields(view: View?) {
        openCameraButton = view?.findViewById(R.id.openCameraButton)!!
        openFilesButton = view.findViewById(R.id.openFilesButton)!!
        listView = view.findViewById(R.id.listView)
        dir = File(Environment.getExternalStorageDirectory().absolutePath, "Scanner")
        listOfFiles = getFiles(dir)

        pdfAdapter = PDFAdapter(activity?.applicationContext, listOfFiles, activity)
        listView.adapter = pdfAdapter
        
        listView.setOnItemClickListener { parent, view, position, id ->

            val file = listOfFiles.get(position)

            val intent = Intent(context?.applicationContext, DisplayPDFActivity::class.java)
            intent.putExtra("uri", file.toURI().toString())
            intent.putExtra("filename", file.name)
            startActivity(intent)

        }
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            openBottomSheet(position)
            true
        }
    }

    private fun openBottomSheet(position: Int) {

        val bottomSheetDialog = BottomSheetDialog()
        bottomSheetDialog.show(activity!!.supportFragmentManager, "Modal Bottom Sheet")
    }

    fun getFiles(dir: File): ArrayList<File> {
            val listFiles: Array<File>? = dir.listFiles()
            var fileList: ArrayList<File> = ArrayList()

            if (listFiles != null && listFiles.size > 0)
            {
                for (item in listFiles)
                {
                    if (item.isDirectory)
                    {
                        getFiles(item)
                    }
                    else
                    {
                        var flag: Boolean = false
                        if (item.name.endsWith(".pdf"))
                        {
                            for (element in fileList)
                            {
                                if (element.name.equals(item.name))
                                {
                                    flag = true
                                }
                            }
                            if (flag)
                            {
                                flag = false
                            } else
                            {
                                fileList.add(item)
                            }
                        }
                    }
                }
            }
            return fileList
        }

        fun openCamera(view: View?) {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
            }
            else
            {
                startScan(ScanConstants.OPEN_CAMERA)
            }
        }

        fun openGallery(view: View?) {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
            else
            {
                startScan(ScanConstants.OPEN_GALERIE)
            }
        }

        fun startScan(preference: Int) {
            val intent: Intent = Intent(context!!, ScanActivity::class.java)
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference)
            startActivityForResult(intent, REQUEST_CODE)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == RESULT_OK)
            {
                if (requestCode == REQUEST_CODE)
                {
                    try
                    {
                        if (BuildConfig.DEBUG && data == null)
                        {
                            error("Assertion failed")
                        }
                        val imageUri: Uri = Objects.requireNonNull(data!!.extras)?.getParcelable(ScanActivity.SCAN_RESULT)!!
                        val imageStream: InputStream = activity!!.contentResolver.openInputStream(imageUri)!!
                        val scannedImage = BitmapFactory.decodeStream(imageStream)
                        activity!!.contentResolver.delete(imageUri, null, null)
                        getNameAndSavePDF(scannedImage)

                        //PDFProcessing().makePDF(scannedImage, filename)
                        //Toast.makeText(context, "Successful! PATH: Internal Storage/${Environment.getExternalStorageDirectory().absolutePath}/Scanner".trimIndent(), Toast.LENGTH_SHORT).show()


                        //Toast.makeText(context, "Null filename", Toast.LENGTH_SHORT).show()
                    }
                    catch (e: FileNotFoundException)
                    {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun getNameAndSavePDF(scannedImage: Bitmap) {
            var name: String = ""
            val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
            val mView: View = layoutInflater.inflate(R.layout.filename_dialog, null)
            val txt_inputText: EditText = mView.findViewById<View>(R.id.txt_input) as EditText
            val btn_cancel: Button = mView.findViewById<View>(R.id.btn_cancel) as Button
            val btn_okay: Button = mView.findViewById<View>(R.id.btn_okay) as Button
            alert.setView(mView)
            val alertDialog: android.app.AlertDialog? = alert.create()
            alertDialog?.setCanceledOnTouchOutside(false)
            btn_cancel.setOnClickListener(View.OnClickListener {
                name = "#123#..456"
                alertDialog?.dismiss()
            })
            btn_okay.setOnClickListener(View.OnClickListener {
                name = txt_inputText.text.toString()
                alertDialog?.dismiss()
                Toast.makeText(context, "Generating PDF...", Toast.LENGTH_LONG).show()
                PDFProcessing().makePDF(scannedImage, name)
            })
            alertDialog?.show()

        }

}

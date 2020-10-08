package com.vob.scanit.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.googlecode.tesseract.android.TessBaseAPI
import com.monscanner.ScanActivity
import com.monscanner.ScanConstants
import com.vob.scanit.R
import java.io.File
import java.io.InputStream
import java.lang.Exception
import java.util.*

class OCRFragment : Fragment() {

    private val TAG: String? = getActivity()?.javaClass?.simpleName
    public val TESS_DATA: String = "/tessdata"
    lateinit var textView: TextView
    lateinit var button: Button
    lateinit var instruction_text: TextView
    lateinit var openCameraButton: FloatingActionButton
    lateinit var openFilesButton: FloatingActionButton
    lateinit var tessBaseAPI: TessBaseAPI
    lateinit var outputFileDir: Uri
    val DATA_PATH: String = android.os.Environment.getExternalStorageDirectory().absolutePath + "/Scanner/Tess"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_o_c_r, container, false)

        initialiseFields(view)
        FirebaseApp.initializeApp(context)

        openCameraButton.setOnClickListener { openCamera(view) }
        openFilesButton.setOnClickListener { openGallery(view) }

        if (textView.text.isEmpty())
        {
            instruction_text.visibility = View.VISIBLE
        }

        return view
    }

    private fun initialiseFields(view: View) {
        textView = view.findViewById(R.id.ocr_tv)
        openCameraButton = view.findViewById(R.id.openCameraButton_ocr)!!
        openFilesButton = view.findViewById(R.id.openFilesButton_ocr)!!
        instruction_text = view.findViewById(R.id.ocr_instruction_tv)
    }

    fun openCamera(view: View?) {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        } else {
            startScan(ScanConstants.OPEN_CAMERA)
        }
    }

    fun openGallery(view: View?) {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
            startScan(ScanConstants.OPEN_GALERIE)
        }
    }

    fun startScan(preference: Int) {
        val imagePath = DATA_PATH + "/image"
        val dir = File(imagePath)
        if (!dir.exists())
            dir.mkdirs()
        val imageFilePath = imagePath + "/ocr.jpg"
        outputFileDir = Uri.fromFile(File(imageFilePath))
        val intent: Intent = Intent(context!!, ScanActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference)
        startActivityForResult(intent, 100)
    }

    private fun startCameraActivity() {
        try {
            val imagePath = DATA_PATH + "/image"
            val dir = File(imagePath)
            if (!dir.exists())
                dir.mkdirs()
            val imageFilePath = imagePath + "/ocr.jpg"
            outputFileDir = Uri.fromFile(File(imageFilePath))
            val intent = Intent (MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileDir)
            //if (intent.resolveActivity(activity!!.packageManager) != null)
                //startActivityForResult(intent, 100)
        }
        catch (e: Exception) {
            Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK)
        {
            //prepareTessData()
            //startOCR(outputFileDir)
            val imageUri: Uri = Objects.requireNonNull(data!!.extras)?.getParcelable(ScanActivity.SCAN_RESULT)!!
            val imageStream: InputStream = activity!!.contentResolver.openInputStream(imageUri)!!
            val scannedImage = BitmapFactory.decodeStream(imageStream)
            activity!!.contentResolver.delete(imageUri, null, null)
            detectText(scannedImage)
        }
        else
        {
            Toast.makeText(activity, "Error while analysing image.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun detectText(scannedImage: Bitmap?)
    {
        var firebaseVisionImage: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(scannedImage!!)
        var firebaseApp = FirebaseApp.initializeApp(context)
        var firebaseVisionTextDetector = FirebaseVision.getInstance().visionTextDetector
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener {
                    displayTextFromImage(it)
                }
                .addOnFailureListener {
                    Toast.makeText(activity,"Error: ${it.message}",Toast.LENGTH_LONG).show()
                }
    }

    private fun displayTextFromImage(firebaseVisionText: FirebaseVisionText) {
        textView.text = ""
        var blockList: List<FirebaseVisionText.Block> = firebaseVisionText.blocks
        if (blockList.size == 0)
        {
            Toast.makeText(activity,"No text found",Toast.LENGTH_SHORT).show()
        }
        else
        {
            blockList.forEach {
                /*var text = it.text
                textView.text = text*/
                var lines = it.lines
                lines.forEach { line ->
                    if (textView.text.toString() == "")
                    {
                        textView.text = line.text
                        textView.append("\n")
                    }
                    else
                    {
                        textView.append(line.text)
                        textView.append("\n")
                    }
                    //var elements = line.elements
                    //elements.forEach { element -> }
                }
            }
        }
        if (textView.text.isNotEmpty())
        {
            instruction_text.visibility = View.INVISIBLE
        }
    }
}
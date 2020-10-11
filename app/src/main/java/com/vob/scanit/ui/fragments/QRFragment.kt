package com.vob.scanit.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
//import com.blikoon.qrcodescanner.QrCodeActivity
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.vob.scanit.R
import com.vob.scanit.ui.activities.GenerateQRActivity
import com.vob.scanit.ui.activities.QRBarcodeScanResultActivity
//import com.google.zxing.integration.android.IntentIntegrator
//import com.vob.scanit.R
import com.vob.scanit.ui.activities.ShowQRData


class QRFragment : Fragment() {

    //private val REQUEST_CODE_QR_SCAN = 101
    //lateinit var scannerView: ScannerView
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_q_r, container, false)

        //val i = Intent(activity, QrCodeActivity::class.java)
        //startActivityForResult(i, REQUEST_CODE_QR_SCAN)

        //val button = view.findViewById<Button>(R.id.startscan_btn)
        //button.setOnClickListener { letsScan() }

        //scannerView = view.findViewById(R.id.scannerView)
        //startScanner()

        val generateQRBtn: Button = view.findViewById(R.id.generate_qr_btn)
        generateQRBtn.setOnClickListener {
            val intent = Intent(context, GenerateQRActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanView)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                val intent = Intent(activity, QRBarcodeScanResultActivity::class.java)
                intent.putExtra("result", it.text)
                startActivity(intent)
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    /*private fun startScanner() {
        scannerView.apply {
            //addObserver(context)
            onResultListener {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun restartScanner() {
        scannerView.resumeScannerView()
    }*/


    /*private fun letsScan() {
        val integrator = IntentIntegrator(activity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("scan")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(false)
        integrator.initiateScan()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        val data = scanResult.contents
        if (data!=null)
            view!!.findViewById<TextView>(R.id.result_tv).text = data
        else
            view!!.findViewById<TextView>(R.id.result_tv).text = "Error"
    }*/

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            //Log.d(LOGTAG, "COULD NOT GET A GOOD RESULT.")
            if (data == null)
                return
            val result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image")
            if (result != null)
            {
                Toast.makeText(context,"QR code could not be scanned.", Toast.LENGTH_SHORT).show()
            }
            return
        }
        if (requestCode == REQUEST_CODE_QR_SCAN)
        {
            if (data == null) return
            //Getting the passed result
            val result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult")
            //Log.d(LOGTAG, "Have scan result in your app activity :$result")
            val intent = Intent(QRFragment().context, ShowQRData::class.java)
            intent.putExtra("data",result)
            startActivity(intent)
            //Toast.makeText(QRFragment().context,result, Toast.LENGTH_SHORT).show()
        }
    }*/
}
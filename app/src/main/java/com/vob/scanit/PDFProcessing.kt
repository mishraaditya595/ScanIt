package com.vob.scanit

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.vob.scanit.ui.fragments.HomeFragment
import java.io.*
import java.util.*

public class PDFProcessing(context: Context) {

    lateinit var pdfDocument: PdfDocument
    val context = context



    fun makePDF(bitmap: Bitmap, filename: String) {
        pdfDocument = PdfDocument()


        val compressedBitmap = compressBitmap(bitmap)
        val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(compressedBitmap.width, compressedBitmap.height, 1).create()
        val page: PdfDocument.Page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        paint.color = Color.parseColor("#FFFFFF")
        canvas.drawBitmap(compressedBitmap, 0.0f, 0.0f, null)
        pdfDocument.finishPage(page)
        //if (fileName.getText().toString().isEmpty()) {
        //    Toast.makeText(context, "You need to enter file name as follow\n your_fileName.pdf", Toast.LENGTH_SHORT).show()
        //}
        if (filename.endsWith(".pdf"))

        {
        //saveFile(filename)
            saveFileToScopedStorage(filename)
        }

        else
        {
            //saveFile("$filename.pdf")
            saveFileToScopedStorage("$filename.pdf")
        }
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val originalHeight = bitmap.height
        val originalWidth = bitmap.width

        val height: Int
        val width: Int

        when {
            originalWidth > originalHeight -> {
                height = 1080
                width = 1920
            }
            originalHeight > originalWidth -> {
                height = 1920
                width = 1080
            }
            else -> {
                height = 1200
                width = 1200
            }
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }


    private fun saveFile(filename: String) {
        if (pdfDocument == null) {
            return
        }
        val root = File(Environment.getExternalStorageDirectory().absolutePath, "Scanner")


//        var isDirectoryCreated: Boolean = if (root.exists()) {
//            root.exists()
//        } else {
//            root.mkdir()
//        }
        if (checkFileName()) {
            val userInputFileName: String = filename
            val file = File(root, userInputFileName)
            try {
                val fileOutputStream = FileOutputStream(file)
                pdfDocument.writeTo(fileOutputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            pdfDocument.close()
        }

//        val successMsg: String = "Successful! PATH: Internal Storage/${Environment.getExternalStorageDirectory().absolutePath}/Scanner"
    }

    private fun checkFileName(): Boolean {
        return true
    }

    fun saveFileToScopedStorage(filename: String) {
        val fos: FileOutputStream
        try {
            val resolver = context.contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename")
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + File.separator + "ScanIt")
            val documentUri = resolver!!.insert(MediaStore.Files.getContentUri("external"), contentValues)

//            val pfd = context.contentResolver.openFileDescriptor(documentUri!!, "w")
//            fos = FileOutputStream(pfd!!.fileDescriptor)
//
//            val `in`: InputStream = context.assets.open("eliza.pdf")
//
//            val buf = ByteArray(4 * 1024)
//            var len: Int
//            while (`in`.read(buf).also { len = it } > 0) {
//                fos.write(buf, 0, len)
//            }
//            fos.close()
//            `in`.close()
//            pfd.close()

            val outputStream = context.contentResolver.openOutputStream(documentUri!!)

            if (outputStream != null)
            {
                pdfDocument.writeTo(outputStream)
            }
            else
            {
                Toast.makeText(context,"Null output stream", Toast.LENGTH_LONG).show()
            }

            pdfDocument.close()


        } catch (e: Exception) {
            Toast.makeText(context, "SS Error", Toast.LENGTH_LONG).show()
        }
        pdfDocument.close()
    }
}

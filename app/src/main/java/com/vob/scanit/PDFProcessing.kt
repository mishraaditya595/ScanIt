package com.vob.scanit

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

public class PDFProcessing() {

    lateinit var pdfDocument: PdfDocument


    public fun makePDF(bitmap: Bitmap, filename: String) {
        pdfDocument = PdfDocument()


        val compressedBitmap = compressBitmap(bitmap)
        val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(compressedBitmap.width, compressedBitmap.height, 1).create()
        val page: PdfDocument.Page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.getCanvas()
        val paint = Paint()
        paint.setColor(Color.parseColor("#FFFFFF"))
        canvas.drawBitmap(compressedBitmap, 0.0f, 0.0f, null)
        pdfDocument.finishPage(page)
        //if (fileName.getText().toString().isEmpty()) {
        //    Toast.makeText(context, "You need to enter file name as follow\nyour_fileName.pdf", Toast.LENGTH_SHORT).show()
        //}
        if (filename.endsWith(".pdf"))
            saveFile(filename)
        else
        {
            saveFile("$filename.pdf")
        }
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val originalHeight = bitmap.height
        val originalWidth = bitmap.width

        var height: Int
        var width: Int

        if (originalWidth>originalHeight){
            height = 1080
            width = 1920
        } else if (originalHeight>originalWidth) {
            height = 1920
            width = 1080
        } else {
            height = 1200
            width = 1200
        }

        val compressedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        return compressedBitmap
    }


    fun saveFile(filename: String) {
        if (pdfDocument == null) {
            return
        }
        val root = File(Environment.getExternalStorageDirectory().absolutePath, "Scanner")

        var isDirectoryCreated: Boolean = root.exists()
        if (!isDirectoryCreated) {
            isDirectoryCreated = root.mkdir()
        }
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

        val successMsg: String = "Successful! PATH: Internal Storage/${Environment.getExternalStorageDirectory().absolutePath}/Scanner"
    }

    private fun checkFileName(): Boolean {
        return true
    }
}

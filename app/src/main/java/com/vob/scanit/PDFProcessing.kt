package com.vob.scanit

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
import android.widget.Toast
import com.vob.scanit.ui.fragments.HomeFragment
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

public class PDFProcessing(context: Context) {

    lateinit var pdfDocument: PdfDocument
    val context = context


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

//    fun queryScopedStorage() : ArrayList<PdfDocument> {
//        val projection = arrayOf(
//                MediaStore.MediaColumns.DISPLAY_NAME,
//                MediaStore.MediaColumns.MIME_TYPE,
//                MediaStore.MediaColumns.RELATIVE_PATH,
//                MediaStore.MediaColumns._ID)
//
//        val cursor = context.contentResolver.query(
//                MediaStore.Files.getContentUri("external"),
//                projection,
//                null,
//                null,
//                null
//        )
//
//        val idColumn = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
//        val nameColumn = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
//        val typeColumn = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
//        val pathColumn = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
//
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                val id = idColumn?.let { cursor.getLong(it) }
//                val name = nameColumn?.let { cursor.getString(it) }
//                val type = typeColumn?.let { cursor.getString(it) }
//                val path = pathColumn?.let { cursor.getString(it) }
//
//                val contentUri = ContentUris.withAppendedId(
//                        MediaStore.Files.getContentUri("external"),
//                        id)
//
//                lateinit var fileList: ArrayList<PdfDocument>
////                fileList.add(PdfDocument(contentUri, name, type, path))
//            }
//        }
//    }

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

            if (outputStream != null) {
                pdfDocument.writeTo(outputStream)
            }
            else {
                Toast.makeText(context,"Null output stream", Toast.LENGTH_LONG).show()
            }

            pdfDocument.close()


        } catch (e: Exception) {
            Toast.makeText(context, "SS Error", Toast.LENGTH_LONG).show()
        }
        pdfDocument.close()
    }
}

package com.openscan.ui

import android.graphics.pdf.PdfDocument
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.openscan.R
import java.io.File


class DisplayPDFActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener {

    lateinit var pdfView: PDFView
    var pageNumber: Int = 0
    lateinit var fileName: String
    var position: Int = -1
    lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_pdf)

        initialiseFields()
        position = intent.getIntExtra("position", -1)
        fileName = intent.getStringExtra("filename")
        file = getIntent().extras["file"] as File
        displayPDF()
    }

    private fun initialiseFields() {
        pdfView = findViewById(R.id.pdf_viewer)
    }

    private fun displayPDF() {
        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(DefaultScrollHandle(this))
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        val pageNum = page
        setTitle(String.format("%s %s / %s", fileName, page+1, pageCount))
    }

    override fun loadComplete(nbPages: Int) {
        val meta = pdfView.documentMeta
        printBookmarksTree(pdfView.tableOfContents,"-")
    }

    private fun printBookmarksTree(tableOfContents: List<com.shockwave.pdfium.PdfDocument.Bookmark>, s: String) {
        for (item: com.shockwave.pdfium.PdfDocument.Bookmark  in tableOfContents) {
            if (item.hasChildren())
            {
                printBookmarksTree(item.children, s+"-")
            }
        }
    }
}
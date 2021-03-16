package com.vob.scanit.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.vob.scanit.R
import java.io.File
import java.util.*

class PDFAdapter(context: Context, var fileList: ArrayList<File>, var activity: Activity) : ArrayAdapter<File?>(context, R.layout.adapter_pdf, fileList as List<File?>) {
    var searchList = ArrayList<File>()
    var viewHolder: ViewHolder? = null
    private var options_btn: ImageView? = null
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getViewTypeCount(): Int {
        return if (fileList.size > 0) fileList.size else 1
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_pdf, parent, false)
            viewHolder = ViewHolder()
            viewHolder!!.filename = view.findViewById(R.id.filename_TV)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder!!.filename!!.text = fileList[position].name
        options_btn = view!!.findViewById(R.id.options_btn)
        options_btn?.setOnClickListener(View.OnClickListener { v -> DynamicToast.makeWarning(v.context, "Long press on the filename to open options menu", Toast.LENGTH_LONG).show() })
        return view
    }

    fun filter(characterText: String) {
        var characterText = characterText
        characterText = characterText.toLowerCase(Locale.getDefault())
        fileList.clear()
        if (characterText.length == 0) {
            fileList.addAll(searchList)
        } else {
            for (file in searchList) {
                if (file.name.toLowerCase(Locale.getDefault()).contains(characterText)) {
                    fileList.add(file)
                }
            }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder {
        var filename: TextView? = null
    }

    init {
        searchList.addAll(fileList)
    }
}
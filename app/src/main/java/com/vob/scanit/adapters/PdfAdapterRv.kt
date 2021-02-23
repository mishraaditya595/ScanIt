package com.vob.scanit.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.vob.scanit.R
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class PdfAdapterRv(private val context: Context, private val activity: Activity,
                   private val fileList: ArrayList<File>,private val pdfInterface: PdfAdapterInterface) :
        RecyclerView.Adapter<PdfAdapterRv.PdfAdapterViewHolder>(){

    private val searchList: ArrayList<File> = ArrayList()

    inner class PdfAdapterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val fileName: TextView = itemView.findViewById(R.id.filename_TV)
        val optionsBtn: ImageView = itemView.findViewById(R.id.options_btn)

    }

    interface PdfAdapterInterface{
        fun onItemClicked(position: Int)
        fun onItemLongClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfAdapterViewHolder {
        val viewHolder = PdfAdapterViewHolder(
                LayoutInflater.from(context).inflate(R.layout.adapter_pdf, parent, false))
        viewHolder.optionsBtn.setOnClickListener{
            DynamicToast.makeWarning(context, "Long press on the filename to open options menu",
                    Toast.LENGTH_LONG).show()
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: PdfAdapterViewHolder, position: Int) {
        holder.fileName.text = fileList[position].name

        holder.itemView.setOnClickListener{
            pdfInterface.onItemClicked(position)
        }
        holder.itemView.setOnLongClickListener{
            pdfInterface.onItemLongClicked(position)
            true
        }
    }

    override fun getItemCount(): Int {
        return fileList.size
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
}
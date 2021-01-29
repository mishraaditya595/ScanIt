package com.vob.scanit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vob.scanit.R
import java.io.File

class DocumentListAdapter(private val documentList: ArrayList<File>):
RecyclerView.Adapter<DocumentListAdapter.DocumentViewHolder>()
{
    class DocumentViewHolder(var view: View): RecyclerView.ViewHolder(view) {}

    fun updateDocumentList(newDocumentList: List<File>) {
        documentList.clear()
        documentList.addAll(newDocumentList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_pdf, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val filename = holder.view.findViewById<TextView>(R.id.filename_TV)
        filename.text = documentList.get(position).name
    }

    override fun getItemCount(): Int {
        return documentList.size
    }
}
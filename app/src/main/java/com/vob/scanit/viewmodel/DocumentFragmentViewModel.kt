package com.vob.scanit.viewmodel

import android.app.Application
import android.content.Context
import android.database.ContentObserver
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vob.scanit.model.Documents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DocumentFragmentViewModel(): ViewModel() {

    val documents = MutableLiveData<ArrayList<File>>()
    private var contentObserver: ContentObserver? = null

    fun loadDocuments() {
        viewModelScope.launch {
            val dir = File(Environment.DIRECTORY_DOCUMENTS + File.separator + "ScanIt")
            val documentList = getFiles(dir)
            documents.postValue(documentList)
        }
    }

    suspend fun getFiles(dir: File): ArrayList<File> {
        var listFiles = dir.listFiles()
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
                        }
                        else
                        {
                            fileList.add(item)
                        }
                    }
                }
            }
        }
        return  fileList
    }

//    suspend fun queryFiles(): ArrayList<Documents> {
//        var documentsList = mutableListOf<Documents>()
//
//        withContext(Dispatchers.IO) {
//            val projection = arrayOf(
//                    MediaStore.MediaColumns._ID,
//                    MediaStore.MediaColumns.DISPLAY_NAME,
//                    MediaStore.MediaColumns.DATE_MODIFIED
//            )
//
//
//        }
//    }
}
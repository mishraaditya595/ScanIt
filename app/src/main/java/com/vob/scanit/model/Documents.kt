package com.vob.scanit.model

import android.net.Uri
import java.util.*

data class Documents(
        val id: Long,
        val displayName: String,
        val date: Date,
        val contentUri: Uri
)
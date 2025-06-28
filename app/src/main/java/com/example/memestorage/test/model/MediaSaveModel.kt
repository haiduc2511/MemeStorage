package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaSaveModel(
    var msId: String = "",
    var userId: String? = null,
    var mediaId: String? = null
) : Parcelable

package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MySavedMediaModel(
    var msmId: String = "",
    var mediaId: String? = null,
    var timeSaved: Int? = null
) : Parcelable

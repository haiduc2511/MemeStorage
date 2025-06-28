package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDiaryMediaModel(
    var udmId: String = "",
    var mediaURL: String? = null,
    var userId: String? = null,
    var caption: String? = null,
    var time: Int? = null
) : Parcelable

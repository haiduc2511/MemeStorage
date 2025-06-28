package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaLikeModel(
    var mlId: String = "",
    var userId: String = "",
    var mediaId: String = ""
) : Parcelable
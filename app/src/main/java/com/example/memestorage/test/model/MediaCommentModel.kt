package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaCommentModel(
    var mcId: String = "",
    var comment: String? = null,
    var userId: String? = null,
    var mediaId: String? = null
) : Parcelable

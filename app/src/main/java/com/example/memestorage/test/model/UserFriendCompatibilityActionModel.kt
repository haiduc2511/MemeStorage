package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserFriendCompatibilityActionModel(
    var ufcaId: String = "",
    var mediaId: String? = null,
    var action: String? = null,
    var byUserId: String? = null
) : Parcelable

package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserFriendDiaryMediaModel(
    var ufdmId: String = "",
    var userId: String? = null,
    var time: String? = null,
    var userDiaryMediaId: String? = null
) : Parcelable

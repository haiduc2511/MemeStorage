package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDiaryMediaAccessModel(
    var udmaId: String = "",
    var userDiaryMediaId: String? = null,
    var userFriendId: String? = null
) : Parcelable

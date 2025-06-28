package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserFriendCompatibilityModel(
    var ufcId: String = "",
    var user1Id: String? = null,
    var user2Id: String? = null,
    var points: Int? = null
) : Parcelable

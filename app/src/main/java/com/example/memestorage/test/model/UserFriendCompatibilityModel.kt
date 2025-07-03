package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserFriendCompatibilityModel(
    var ufcId: String = "",
    var user1Id: String = "",
    var user2Id: String = "",
    var points: Int = 0
) : Parcelable

package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserFriendshipModel(
    var ufId: String = "",
    var user1Id: String? = null,
    var user2Id: String? = null
) : Parcelable

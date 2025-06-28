package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserFriendshipChatModel(
    var ufcId: String = "",
    var userFriendshipId: String? = null,
    var streak: Int? = null,
    var theme: String? = null
) : Parcelable

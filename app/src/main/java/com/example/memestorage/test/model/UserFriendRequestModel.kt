package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserFriendRequestModel(
    var ufrId: String = "",
    var userRequested: String? = null,
    var userRequesting: String? = null,
    var status: String? = null
) : Parcelable

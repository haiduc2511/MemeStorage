package com.example.memestorage.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserFriendshipMessageModel(
    var ufmId: String = "",
    var sender: String? = null,
    var message: String? = null,
    var time: Int? = null,
    var isReplyFromDiary: Boolean? = null,
    var userFriendshipChatId: String? = null
) : Parcelable

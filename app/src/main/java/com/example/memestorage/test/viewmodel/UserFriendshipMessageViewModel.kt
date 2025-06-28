package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.UserFriendshipMessageModel
import com.example.memestorage.test.repo.UserFriendshipMessageRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendshipMessageViewModel(application: Application) : AndroidViewModel(application) {

    private val ufmRepo = UserFriendshipMessageRepo()
    var ufmList: List<UserFriendshipMessageModel> = emptyList()

    fun addUfmFirebase(model: UserFriendshipMessageModel, onCompleteListener: OnCompleteListener<Void>) {
        ufmRepo.addUfmFirebase(model, onCompleteListener)
    }

    fun getUfmFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufmRepo.getUfmFirebase(onCompleteListener)
    }

    fun getUfmByChatId(userFriendshipChatId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufmRepo.getUfmByChatId(userFriendshipChatId, onCompleteListener)
    }

    fun updateUfmFirebase(id: String, model: UserFriendshipMessageModel, onCompleteListener: OnCompleteListener<Void>) {
        ufmRepo.updateUfmFirebase(id, model, onCompleteListener)
    }

    fun deleteUfmFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        ufmRepo.deleteUfmFirebase(id, onCompleteListener)
    }
}

package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.UserFriendshipChatModel
import com.example.memestorage.test.repo.UserFriendshipChatRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendshipChatViewModel(application: Application) : AndroidViewModel(application) {

    private val ufcRepo = UserFriendshipChatRepo()
    var ufcList: List<UserFriendshipChatModel> = emptyList()

    fun addUfcFirebase(model: UserFriendshipChatModel, onCompleteListener: OnCompleteListener<Void>) {
        ufcRepo.addUfcFirebase(model, onCompleteListener)
    }

    fun getUfcFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufcRepo.getUfcFirebase(onCompleteListener)
    }

    fun getUfcByFriendshipId(userFriendshipId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufcRepo.getUfcByFriendshipId(userFriendshipId, onCompleteListener)
    }

    fun updateUfcFirebase(id: String, model: UserFriendshipChatModel, onCompleteListener: OnCompleteListener<Void>) {
        ufcRepo.updateUfcFirebase(id, model, onCompleteListener)
    }

    fun deleteUfcFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        ufcRepo.deleteUfcFirebase(id, onCompleteListener)
    }
}

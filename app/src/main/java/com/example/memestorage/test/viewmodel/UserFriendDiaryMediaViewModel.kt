package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.UserFriendDiaryMediaModel
import com.example.memestorage.test.repo.UserFriendDiaryMediaRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendDiaryMediaViewModel(application: Application) : AndroidViewModel(application) {

    private val ufdmRepo = UserFriendDiaryMediaRepo()
    var ufdmList: List<UserFriendDiaryMediaModel> = emptyList()


    fun addUfdmFirebase(model: UserFriendDiaryMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        ufdmRepo.addUfdmFirebase(model, onCompleteListener)
    }

    fun addUfdmByFriendshipId(model: UserFriendDiaryMediaModel, friendId: String, onCompleteListener: OnCompleteListener<Void>) {
        ufdmRepo.addUfdmByFriendId(model, friendId, onCompleteListener)
    }

    fun getUfdmFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufdmRepo.getUfdmFirebase(onCompleteListener)
    }

    fun getUfdmByFriendshipId(userFriendshipId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufdmRepo.getUfdmByFriendshipId(userFriendshipId, onCompleteListener)
    }

    fun updateUfdmFirebase(id: String, model: UserFriendDiaryMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        ufdmRepo.updateUfdmFirebase(id, model, onCompleteListener)
    }

    fun deleteUfdmFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        ufdmRepo.deleteUfdmFirebase(id, onCompleteListener)
    }
}

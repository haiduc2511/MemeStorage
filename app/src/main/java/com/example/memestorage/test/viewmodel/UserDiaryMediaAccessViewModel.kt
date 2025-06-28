package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.UserDiaryMediaAccessModel
import com.example.memestorage.test.repo.UserDiaryMediaAccessRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserDiaryMediaAccessViewModel(application: Application) : AndroidViewModel(application) {

    private val udmaRepo = UserDiaryMediaAccessRepo()
    var udmaList: List<UserDiaryMediaAccessModel> = emptyList()

    fun addUdmaFirebase(model: UserDiaryMediaAccessModel, onCompleteListener: OnCompleteListener<Void>) {
        udmaRepo.addUdmaFirebase(model, onCompleteListener)
    }

    fun getUdmaFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        udmaRepo.getUdmaFirebase(onCompleteListener)
    }

    fun getUdmaByUserFriendId(userFriendId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        udmaRepo.getUdmaByUserFriendId(userFriendId, onCompleteListener)
    }

    fun updateUdmaFirebase(id: String, model: UserDiaryMediaAccessModel, onCompleteListener: OnCompleteListener<Void>) {
        udmaRepo.updateUdmaFirebase(id, model, onCompleteListener)
    }

    fun deleteUdmaFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        udmaRepo.deleteUdmaFirebase(id, onCompleteListener)
    }
}

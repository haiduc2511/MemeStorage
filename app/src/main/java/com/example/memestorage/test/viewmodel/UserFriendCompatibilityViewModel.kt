package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.UserFriendCompatibilityModel
import com.example.memestorage.test.repo.UserFriendCompatibilityRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendCompatibilityViewModel(application: Application) : AndroidViewModel(application) {

    private val ufcRepo = UserFriendCompatibilityRepo()
    var ufcList: List<UserFriendCompatibilityModel> = emptyList()

    fun addUfcFirebase(model: UserFriendCompatibilityModel, onCompleteListener: OnCompleteListener<Void>) {
        ufcRepo.addUfcFirebase(model, onCompleteListener)
    }

    fun getUfcFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufcRepo.getUfcFirebase(onCompleteListener)
    }

    fun getUfcByUser1Id(user1Id: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufcRepo.getUfcByUser1Id(user1Id, onCompleteListener)
    }

    fun getUfcByUser2Id(user2Id: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufcRepo.getUfcByUser2Id(user2Id, onCompleteListener)
    }

    fun updateUfcFirebase(id: String, model: UserFriendCompatibilityModel, onCompleteListener: OnCompleteListener<Void>) {
        ufcRepo.updateUfcFirebase(id, model, onCompleteListener)
    }

    fun deleteUfcFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        ufcRepo.deleteUfcFirebase(id, onCompleteListener)
    }
}

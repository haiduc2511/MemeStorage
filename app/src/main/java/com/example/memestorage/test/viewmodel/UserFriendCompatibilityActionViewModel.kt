package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.UserFriendCompatibilityActionModel
import com.example.memestorage.test.repo.UserFriendCompatibilityActionRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendCompatibilityActionViewModel(application: Application) : AndroidViewModel(application) {

    private val ufcaRepo = UserFriendCompatibilityActionRepo()
    var ufcaList: List<UserFriendCompatibilityActionModel> = emptyList()

    fun addUfcaFirebase(model: UserFriendCompatibilityActionModel, onCompleteListener: OnCompleteListener<Void>) {
        ufcaRepo.addUfcaFirebase(model, onCompleteListener)
    }

    fun getUfcaFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufcaRepo.getUfcaFirebase(onCompleteListener)
    }

    fun getUfcaByByUserId(byUserId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufcaRepo.getUfcaByByUserId(byUserId, onCompleteListener)
    }

    fun updateUfcaFirebase(id: String, model: UserFriendCompatibilityActionModel, onCompleteListener: OnCompleteListener<Void>) {
        ufcaRepo.updateUfcaFirebase(id, model, onCompleteListener)
    }

    fun deleteUfcaFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        ufcaRepo.deleteUfcaFirebase(id, onCompleteListener)
    }
}

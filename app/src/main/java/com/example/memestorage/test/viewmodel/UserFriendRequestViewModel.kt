package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.UserFriendRequestModel
import com.example.memestorage.test.repo.UserFriendRequestRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendRequestViewModel(application: Application) : AndroidViewModel(application) {

    private val ufrRepo = UserFriendRequestRepo()
    var ufrList: List<UserFriendRequestModel> = emptyList()

    fun addUfrFirebase(model: UserFriendRequestModel, onCompleteListener: OnCompleteListener<Void>) {
        ufrRepo.addUfrFirebase(model, onCompleteListener)
    }

    fun getUfrFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufrRepo.getUfrFirebase(onCompleteListener)
    }

    fun getUfrByUserRequested(userRequested: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufrRepo.getUfrByUserRequested(userRequested, onCompleteListener)
    }

    fun getUfrByUserRequesting(userRequesting: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufrRepo.getUfrByUserRequesting(userRequesting, onCompleteListener)
    }

    fun updateUfrFirebase(id: String, model: UserFriendRequestModel, onCompleteListener: OnCompleteListener<Void>) {
        ufrRepo.updateUfrFirebase(id, model, onCompleteListener)
    }

    fun deleteUfrFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        ufrRepo.deleteUfrFirebase(id, onCompleteListener)
    }
}

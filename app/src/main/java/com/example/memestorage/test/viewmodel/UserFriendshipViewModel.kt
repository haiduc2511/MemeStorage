package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.UserFriendshipModel
import com.example.memestorage.test.repo.UserFriendshipRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendshipViewModel(application: Application) : AndroidViewModel(application) {

    private val ufRepo = UserFriendshipRepo()
    var ufList: List<UserFriendshipModel> = emptyList()

    fun addUfFirebase(model: UserFriendshipModel, onCompleteListener: OnCompleteListener<Void>) {
        ufRepo.addUfFirebase(model, onCompleteListener)
    }

    fun getUfFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufRepo.getUfFirebase(onCompleteListener)
    }

    fun getUfByUser1Id(user1Id: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufRepo.getUfByUser1Id(user1Id, onCompleteListener)
    }

    fun getUfByUser2Id(user2Id: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufRepo.getUfByUser2Id(user2Id, onCompleteListener)
    }

    fun updateUfFirebase(id: String, model: UserFriendshipModel, onCompleteListener: OnCompleteListener<Void>) {
        ufRepo.updateUfFirebase(id, model, onCompleteListener)
    }

    fun deleteUfFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        ufRepo.deleteUfFirebase(id, onCompleteListener)
    }
}

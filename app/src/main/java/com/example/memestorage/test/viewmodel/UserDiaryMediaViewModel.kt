package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.UserDiaryMediaModel
import com.example.memestorage.test.repo.UserDiaryMediaRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserDiaryMediaViewModel(application: Application) : AndroidViewModel(application) {

    private val udmRepo = UserDiaryMediaRepo()
    var udmList: List<UserDiaryMediaModel> = emptyList()

    fun addUdmFirebase(model: UserDiaryMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        udmRepo.addUdmFirebase(model, onCompleteListener)
    }

    fun getUdmFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        udmRepo.getUdmFirebase(onCompleteListener)
    }

    fun getUdmByUserId(userId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        udmRepo.getUdmByUserId(userId, onCompleteListener)
    }

    fun updateUdmFirebase(id: String, model: UserDiaryMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        udmRepo.updateUdmFirebase(id, model, onCompleteListener)
    }

    fun deleteUdmFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        udmRepo.deleteUdmFirebase(id, onCompleteListener)
    }
}

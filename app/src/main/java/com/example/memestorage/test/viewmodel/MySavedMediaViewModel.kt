package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.MySavedMediaModel
import com.example.memestorage.test.repo.MySavedMediaRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class MySavedMediaViewModel(application: Application) : AndroidViewModel(application) {

    private val mySavedMediaRepo = MySavedMediaRepo()
    var mySavedMedia: List<MySavedMediaModel> = emptyList()

    fun addMySavedMediaFirebase(model: MySavedMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        mySavedMediaRepo.addMySavedMediaFirebase(model, onCompleteListener)
    }

    fun getMySavedMediaFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        mySavedMediaRepo.getMySavedMediaFirebase(onCompleteListener)
    }

    fun getMySavedMediaByMediaId(mediaId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        mySavedMediaRepo.getMySavedMediaByMediaId(mediaId, onCompleteListener)
    }

    fun updateMySavedMediaFirebase(id: String, model: MySavedMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        mySavedMediaRepo.updateMySavedMediaFirebase(id, model, onCompleteListener)
    }

    fun deleteMySavedMediaFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        mySavedMediaRepo.deleteMySavedMediaFirebase(id, onCompleteListener)
    }

    fun deleteMySavedMediaByMediaId(mediaId: String) {
        mySavedMediaRepo.deleteMySavedMediaByMediaId(mediaId)
    }
}

package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.MyLikedMediaModel
import com.example.memestorage.test.repo.MyLikedMediaRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class MyLikedMediaViewModel(application: Application) : AndroidViewModel(application) {

    private val myLikedMediaRepo = MyLikedMediaRepo()
    var myLikedMedia: List<MyLikedMediaModel> = emptyList()

    fun addMyLikedMediaFirebase(model: MyLikedMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        myLikedMediaRepo.addMyLikedMediaFirebase(model, onCompleteListener)
    }

    fun getMyLikedMediaFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myLikedMediaRepo.getMyLikedMediaFirebase(onCompleteListener)
    }

    fun getMyLikedMediaByMediaId(mediaId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myLikedMediaRepo.getMyLikedMediaByMediaId(mediaId, onCompleteListener)
    }

    fun updateMyLikedMediaFirebase(id: String, model: MyLikedMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        myLikedMediaRepo.updateMyLikedMediaFirebase(id, model, onCompleteListener)
    }

    fun deleteMyLikedMediaFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myLikedMediaRepo.deleteMyLikedMediaFirebase(id, onCompleteListener)
    }

    fun deleteMyLikedMediaByMediaId(mediaId: String) {
        myLikedMediaRepo.deleteMyLikedMediaByMediaId(mediaId)
    }
}

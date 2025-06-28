package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.MediaLikeModel
import com.example.memestorage.test.repo.MediaLikeRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class MediaLikeViewModel(application: Application) : AndroidViewModel(application) {

    private val mediaLikeRepo = MediaLikeRepo()
    var mediaLikes: List<MediaLikeModel> = emptyList()

    fun addMediaLikeFirebase(model: MediaLikeModel, onCompleteListener: OnCompleteListener<Void>) {
        mediaLikeRepo.addMediaLikeFirebase(model, onCompleteListener)
    }

    fun getMediaLikesFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        mediaLikeRepo.getMediaLikesFirebase(onCompleteListener)
    }

    fun getMediaLikesByUserId(userId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        mediaLikeRepo.getMediaLikesByUserId(userId, onCompleteListener)
    }

    fun getMediaLikesByMediaId(mediaId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        mediaLikeRepo.getMediaLikesByMediaId(mediaId, onCompleteListener)
    }

    fun updateMediaLikeFirebase(id: String, model: MediaLikeModel, onCompleteListener: OnCompleteListener<Void>) {
        mediaLikeRepo.updateMediaLikeFirebase(id, model, onCompleteListener)
    }

    fun deleteMediaLikeFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        mediaLikeRepo.deleteMediaLikeFirebase(id, onCompleteListener)
    }

    fun deleteMediaLikesByMediaId(mediaId: String) {
        mediaLikeRepo.deleteMediaLikesByMediaId(mediaId)
    }

    fun deleteMediaLikesByUserId(userId: String) {
        mediaLikeRepo.deleteMediaLikesByUserId(userId)
    }
}

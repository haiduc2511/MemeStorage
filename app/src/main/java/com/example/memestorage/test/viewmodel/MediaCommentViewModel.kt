package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.MediaCommentModel
import com.example.memestorage.test.repo.MediaCommentRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class MediaCommentViewModel(application: Application) : AndroidViewModel(application) {

    private val mediaCommentRepo = MediaCommentRepo()
    var mediaComments: List<MediaCommentModel> = emptyList()

    fun addMediaCommentFirebase(model: MediaCommentModel, onCompleteListener: OnCompleteListener<Void>) {
        mediaCommentRepo.addMediaCommentFirebase(model, onCompleteListener)
    }

    fun getMediaCommentsFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        mediaCommentRepo.getMediaCommentsFirebase(onCompleteListener)
    }

    fun getMediaCommentsByUserId(userId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        mediaCommentRepo.getMediaCommentsByUserId(userId, onCompleteListener)
    }

    fun getMediaCommentsByMediaId(mediaId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        mediaCommentRepo.getMediaCommentsByMediaId(mediaId, onCompleteListener)
    }

    fun updateMediaCommentFirebase(id: String, model: MediaCommentModel, onCompleteListener: OnCompleteListener<Void>) {
        mediaCommentRepo.updateMediaCommentFirebase(id, model, onCompleteListener)
    }

    fun deleteMediaCommentFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        mediaCommentRepo.deleteMediaCommentFirebase(id, onCompleteListener)
    }

    fun deleteMediaCommentsByMediaId(mediaId: String) {
        mediaCommentRepo.deleteMediaCommentsByMediaId(mediaId)
    }

    fun deleteMediaCommentsByUserId(userId: String) {
        mediaCommentRepo.deleteMediaCommentsByUserId(userId)
    }
}

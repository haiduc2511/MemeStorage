package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.MediaSaveModel
import com.example.memestorage.test.repo.MediaSaveRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class MediaSaveViewModel(application: Application) : AndroidViewModel(application) {

    private val mediaSaveRepo = MediaSaveRepo()
    var mediaSaves: List<MediaSaveModel> = emptyList()

    fun setMediaSaves(list: List<MediaSaveModel>) {
        mediaSaves = list
    }

    fun getMediaSaves(): List<MediaSaveModel> = mediaSaves

    fun addMediaSaveFirebase(model: MediaSaveModel, onCompleteListener: OnCompleteListener<Void>) {
        mediaSaveRepo.addMediaSaveFirebase(model, onCompleteListener)
    }

    fun getMediaSavesFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        mediaSaveRepo.getMediaSavesFirebase(onCompleteListener)
    }

    fun getMediaSavesByUserId(userId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        mediaSaveRepo.getMediaSavesByUserId(userId, onCompleteListener)
    }

    fun getMediaSavesByMediaId(mediaId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        mediaSaveRepo.getMediaSavesByMediaId(mediaId, onCompleteListener)
    }

    fun updateMediaSaveFirebase(id: String, model: MediaSaveModel, onCompleteListener: OnCompleteListener<Void>) {
        mediaSaveRepo.updateMediaSaveFirebase(id, model, onCompleteListener)
    }

    fun deleteMediaSaveFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        mediaSaveRepo.deleteMediaSaveFirebase(id, onCompleteListener)
    }

    fun deleteMediaSavesByMediaId(mediaId: String) {
        mediaSaveRepo.deleteMediaSavesByMediaId(mediaId)
    }

    fun deleteMediaSavesByUserId(userId: String) {
        mediaSaveRepo.deleteMediaSavesByUserId(userId)
    }
}

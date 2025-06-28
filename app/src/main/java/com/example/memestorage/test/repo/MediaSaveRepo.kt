package com.example.memestorage.test.repo

import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.MediaSaveModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MediaSaveRepo {

    companion object {
        private const val MEDIA_SAVES_COLLECTION = "mediaSaves"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val myMediaSavesRef = db.collection(USER_COLLECTION).document(myUserId)

    fun addMediaSaveFirebase(model: MediaSaveModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = myMediaSavesRef.collection(MEDIA_SAVES_COLLECTION).document().id
        model.msId = id
        myMediaSavesRef.collection(MEDIA_SAVES_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getMediaSavesFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myMediaSavesRef.collection(MEDIA_SAVES_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getMediaSavesByUserId(userId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myMediaSavesRef.collection(MEDIA_SAVES_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getMediaSavesByMediaId(mediaId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myMediaSavesRef.collection(MEDIA_SAVES_COLLECTION)
            .whereEqualTo("mediaId", mediaId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateMediaSaveFirebase(id: String, model: MediaSaveModel, onCompleteListener: OnCompleteListener<Void>) {
        myMediaSavesRef.collection(MEDIA_SAVES_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteMediaSaveFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myMediaSavesRef.collection(MEDIA_SAVES_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteMediaSavesByMediaId(mediaId: String) {
        myMediaSavesRef.collection(MEDIA_SAVES_COLLECTION)
            .whereEqualTo("mediaId", mediaId)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot) {
                    doc.reference.delete()
                }
            }
    }

    fun deleteMediaSavesByUserId(userId: String) {
        myMediaSavesRef.collection(MEDIA_SAVES_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot) {
                    doc.reference.delete()
                }
            }
    }
}

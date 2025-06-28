package com.example.memestorage.test.repo

import com.example.memestorage.test.model.MediaLikeModel
import com.example.memestorage.utils.FirebaseHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MediaLikeRepo {

    companion object {
        private const val MEDIA_LIKES_COLLECTION = "mediaLikes"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val myMediaLikesRef = db.collection(USER_COLLECTION).document(myUserId)

    fun addMediaLikeFirebase(model: MediaLikeModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = myMediaLikesRef.collection(MEDIA_LIKES_COLLECTION).document().id
        model.mlId = id
        myMediaLikesRef.collection(MEDIA_LIKES_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getMediaLikesFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myMediaLikesRef.collection(MEDIA_LIKES_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getMediaLikesByUserId(userId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myMediaLikesRef.collection(MEDIA_LIKES_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getMediaLikesByMediaId(mediaId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myMediaLikesRef.collection(MEDIA_LIKES_COLLECTION)
            .whereEqualTo("mediaId", mediaId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateMediaLikeFirebase(id: String, model: MediaLikeModel, onCompleteListener: OnCompleteListener<Void>) {
        myMediaLikesRef.collection(MEDIA_LIKES_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteMediaLikeFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myMediaLikesRef.collection(MEDIA_LIKES_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteMediaLikesByMediaId(mediaId: String) {
        myMediaLikesRef.collection(MEDIA_LIKES_COLLECTION)
            .whereEqualTo("mediaId", mediaId)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot) {
                    doc.reference.delete()
                }
            }
    }

    fun deleteMediaLikesByUserId(userId: String) {
        myMediaLikesRef.collection(MEDIA_LIKES_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot) {
                    doc.reference.delete()
                }
            }
    }
}

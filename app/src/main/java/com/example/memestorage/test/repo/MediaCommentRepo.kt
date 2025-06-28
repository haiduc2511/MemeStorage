package com.example.memestorage.test.repo

import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.MediaCommentModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MediaCommentRepo {

    companion object {
        private const val MEDIA_COMMENTS_COLLECTION = "mediaComments"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val myMediaCommentsRef = db.collection(USER_COLLECTION).document(myUserId)

    fun addMediaCommentFirebase(model: MediaCommentModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = myMediaCommentsRef.collection(MEDIA_COMMENTS_COLLECTION).document().id
        model.mcId = id
        myMediaCommentsRef.collection(MEDIA_COMMENTS_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getMediaCommentsFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myMediaCommentsRef.collection(MEDIA_COMMENTS_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getMediaCommentsByUserId(userId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myMediaCommentsRef.collection(MEDIA_COMMENTS_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getMediaCommentsByMediaId(mediaId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myMediaCommentsRef.collection(MEDIA_COMMENTS_COLLECTION)
            .whereEqualTo("mediaId", mediaId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateMediaCommentFirebase(id: String, model: MediaCommentModel, onCompleteListener: OnCompleteListener<Void>) {
        myMediaCommentsRef.collection(MEDIA_COMMENTS_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteMediaCommentFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myMediaCommentsRef.collection(MEDIA_COMMENTS_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteMediaCommentsByMediaId(mediaId: String) {
        myMediaCommentsRef.collection(MEDIA_COMMENTS_COLLECTION)
            .whereEqualTo("mediaId", mediaId)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot) {
                    doc.reference.delete()
                }
            }
    }

    fun deleteMediaCommentsByUserId(userId: String) {
        myMediaCommentsRef.collection(MEDIA_COMMENTS_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot) {
                    doc.reference.delete()
                }
            }
    }
}

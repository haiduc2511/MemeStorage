package com.example.memestorage.test.repo

import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.MySavedMediaModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MySavedMediaRepo {

    companion object {
        private const val MY_SAVED_MEDIA_COLLECTION = "mySavedMedia"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val mySavedMediaRef = db.collection(USER_COLLECTION).document(myUserId)

    fun addMySavedMediaFirebase(model: MySavedMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = mySavedMediaRef.collection(MY_SAVED_MEDIA_COLLECTION).document().id
        model.msmId = id
        mySavedMediaRef.collection(MY_SAVED_MEDIA_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getMySavedMediaFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        mySavedMediaRef.collection(MY_SAVED_MEDIA_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getMySavedMediaByMediaId(mediaId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        mySavedMediaRef.collection(MY_SAVED_MEDIA_COLLECTION)
            .whereEqualTo("mediaId", mediaId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateMySavedMediaFirebase(id: String, model: MySavedMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        mySavedMediaRef.collection(MY_SAVED_MEDIA_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteMySavedMediaFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        mySavedMediaRef.collection(MY_SAVED_MEDIA_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteMySavedMediaByMediaId(mediaId: String) {
        mySavedMediaRef.collection(MY_SAVED_MEDIA_COLLECTION)
            .whereEqualTo("mediaId", mediaId)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot) {
                    doc.reference.delete()
                }
            }
    }
}

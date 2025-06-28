package com.example.memestorage.test.repo

import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.MyLikedMediaModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MyLikedMediaRepo {

    companion object {
        private const val MY_LIKED_MEDIA_COLLECTION = "myLikedMedia"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val myLikedMediaRef = db.collection(USER_COLLECTION).document(myUserId)

    fun addMyLikedMediaFirebase(model: MyLikedMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = myLikedMediaRef.collection(MY_LIKED_MEDIA_COLLECTION).document().id
        model.mlmId = id
        myLikedMediaRef.collection(MY_LIKED_MEDIA_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getMyLikedMediaFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myLikedMediaRef.collection(MY_LIKED_MEDIA_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getMyLikedMediaByMediaId(mediaId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myLikedMediaRef.collection(MY_LIKED_MEDIA_COLLECTION)
            .whereEqualTo("mediaId", mediaId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateMyLikedMediaFirebase(id: String, model: MyLikedMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        myLikedMediaRef.collection(MY_LIKED_MEDIA_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteMyLikedMediaFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myLikedMediaRef.collection(MY_LIKED_MEDIA_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteMyLikedMediaByMediaId(mediaId: String) {
        myLikedMediaRef.collection(MY_LIKED_MEDIA_COLLECTION)
            .whereEqualTo("mediaId", mediaId)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot) {
                    doc.reference.delete()
                }
            }
    }
}

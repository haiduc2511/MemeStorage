package com.example.memestorage.test.repo

import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.UserFriendRequestModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserFriendRequestRepo {

    companion object {
        private const val USER_FRIEND_REQUEST_COLLECTION = "userFriendRequest"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val myUfrRef = db.collection(USER_COLLECTION).document(myUserId)

    fun addUfrFirebase(model: UserFriendRequestModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = myUfrRef.collection(USER_FRIEND_REQUEST_COLLECTION).document().id
        model.ufrId = id
        myUfrRef.collection(USER_FRIEND_REQUEST_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfrFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfrRef.collection(USER_FRIEND_REQUEST_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfrByUserRequested(userRequested: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfrRef.collection(USER_FRIEND_REQUEST_COLLECTION)
            .whereEqualTo("userRequested", userRequested)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfrByUserRequesting(userRequesting: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfrRef.collection(USER_FRIEND_REQUEST_COLLECTION)
            .whereEqualTo("userRequesting", userRequesting)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateUfrFirebase(id: String, model: UserFriendRequestModel, onCompleteListener: OnCompleteListener<Void>) {
        myUfrRef.collection(USER_FRIEND_REQUEST_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteUfrFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myUfrRef.collection(USER_FRIEND_REQUEST_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }
}

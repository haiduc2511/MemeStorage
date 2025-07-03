package com.example.memestorage.test.repo

import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.UserFriendCompatibilityModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserFriendCompatibilityRepo {

    companion object {
        private const val USER_FRIEND_COMPATIBILITY_COLLECTION = "userFriendCompatibility"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val myUfcRef = db

    fun addUfcFirebase(model: UserFriendCompatibilityModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = myUfcRef.collection(USER_FRIEND_COMPATIBILITY_COLLECTION).document().id
        model.ufcId = id
        myUfcRef.collection(USER_FRIEND_COMPATIBILITY_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfcFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfcRef.collection(USER_FRIEND_COMPATIBILITY_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfcByUser1Id(user1Id: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfcRef.collection(USER_FRIEND_COMPATIBILITY_COLLECTION)
            .whereEqualTo("user1Id", user1Id)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfcByUser2Id(user2Id: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfcRef.collection(USER_FRIEND_COMPATIBILITY_COLLECTION)
            .whereEqualTo("user2Id", user2Id)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateUfcFirebase(id: String, model: UserFriendCompatibilityModel, onCompleteListener: OnCompleteListener<Void>) {
        myUfcRef.collection(USER_FRIEND_COMPATIBILITY_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteUfcFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myUfcRef.collection(USER_FRIEND_COMPATIBILITY_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }
}

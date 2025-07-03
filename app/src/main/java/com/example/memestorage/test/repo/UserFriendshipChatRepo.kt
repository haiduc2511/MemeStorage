package com.example.memestorage.test.repo

import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.UserFriendshipChatModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserFriendshipChatRepo {

    companion object {
        private const val USER_FRIENDSHIP_CHAT_COLLECTION = "userFriendshipChat"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val myUfcRef = db

    fun addUfcFirebase(model: UserFriendshipChatModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = myUfcRef.collection(USER_FRIENDSHIP_CHAT_COLLECTION).document().id
        model.ufcId = id
        myUfcRef.collection(USER_FRIENDSHIP_CHAT_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfcFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfcRef.collection(USER_FRIENDSHIP_CHAT_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfcByFriendshipId(userFriendshipId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfcRef.collection(USER_FRIENDSHIP_CHAT_COLLECTION)
            .whereEqualTo("userFriendshipId", userFriendshipId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateUfcFirebase(id: String, model: UserFriendshipChatModel, onCompleteListener: OnCompleteListener<Void>) {
        myUfcRef.collection(USER_FRIENDSHIP_CHAT_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteUfcFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myUfcRef.collection(USER_FRIENDSHIP_CHAT_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }
}

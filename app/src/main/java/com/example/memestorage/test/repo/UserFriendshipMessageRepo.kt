package com.example.memestorage.test.repo

import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.UserFriendshipMessageModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserFriendshipMessageRepo {

    companion object {
        private const val USER_FRIENDSHIP_MESSAGE_COLLECTION = "userFriendshipMessage"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val myUfmRef = db.collection(USER_COLLECTION).document(myUserId)

    fun addUfmFirebase(model: UserFriendshipMessageModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = myUfmRef.collection(USER_FRIENDSHIP_MESSAGE_COLLECTION).document().id
        model.ufmId = id
        myUfmRef.collection(USER_FRIENDSHIP_MESSAGE_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfmFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfmRef.collection(USER_FRIENDSHIP_MESSAGE_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfmByChatId(userFriendshipChatId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfmRef.collection(USER_FRIENDSHIP_MESSAGE_COLLECTION)
            .whereEqualTo("userFriendshipChatId", userFriendshipChatId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateUfmFirebase(id: String, model: UserFriendshipMessageModel, onCompleteListener: OnCompleteListener<Void>) {
        myUfmRef.collection(USER_FRIENDSHIP_MESSAGE_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteUfmFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myUfmRef.collection(USER_FRIENDSHIP_MESSAGE_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }
}

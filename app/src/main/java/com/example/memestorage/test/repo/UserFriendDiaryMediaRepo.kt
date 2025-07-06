package com.example.memestorage.test.repo

import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.UserFriendDiaryMediaModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserFriendDiaryMediaRepo {

    companion object {
        private const val USER_FRIEND_DIARY_MEDIA_COLLECTION = "userFriendDiaryMedia"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val myUfdmRef = db

    fun addUfdmFirebase(model: UserFriendDiaryMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = myUfdmRef.collection(USER_FRIEND_DIARY_MEDIA_COLLECTION).document().id
        model.ufdmId = id
        myUfdmRef.collection(USER_FRIEND_DIARY_MEDIA_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun addUfdmByFriendId(model: UserFriendDiaryMediaModel, friendId: String, onCompleteListener: OnCompleteListener<Void>) {
        val id = myUfdmRef.collection(USER_COLLECTION).document(friendId).collection(USER_FRIEND_DIARY_MEDIA_COLLECTION).document().id
        model.ufdmId = id
        myUfdmRef.collection(USER_FRIEND_DIARY_MEDIA_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfdmFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfdmRef.collection(USER_FRIEND_DIARY_MEDIA_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfdmByFriendshipId(userFriendshipId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfdmRef.collection(USER_FRIEND_DIARY_MEDIA_COLLECTION)
            .whereEqualTo("userFriendshipId", userFriendshipId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateUfdmFirebase(id: String, model: UserFriendDiaryMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        myUfdmRef.collection(USER_FRIEND_DIARY_MEDIA_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteUfdmFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myUfdmRef.collection(USER_FRIEND_DIARY_MEDIA_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }
}

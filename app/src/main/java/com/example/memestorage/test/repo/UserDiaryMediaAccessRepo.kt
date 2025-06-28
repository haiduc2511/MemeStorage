package com.example.memestorage.test.repo

import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.UserDiaryMediaAccessModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserDiaryMediaAccessRepo {

    companion object {
        private const val USER_DIARY_MEDIA_ACCESS_COLLECTION = "userDiaryMediaAccess"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val myUdmaRef = db.collection(USER_COLLECTION).document(myUserId)

    fun addUdmaFirebase(model: UserDiaryMediaAccessModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = myUdmaRef.collection(USER_DIARY_MEDIA_ACCESS_COLLECTION).document().id
        model.udmaId = id
        myUdmaRef.collection(USER_DIARY_MEDIA_ACCESS_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUdmaFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUdmaRef.collection(USER_DIARY_MEDIA_ACCESS_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUdmaByUserFriendId(userFriendId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUdmaRef.collection(USER_DIARY_MEDIA_ACCESS_COLLECTION)
            .whereEqualTo("userFriendId", userFriendId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateUdmaFirebase(id: String, model: UserDiaryMediaAccessModel, onCompleteListener: OnCompleteListener<Void>) {
        myUdmaRef.collection(USER_DIARY_MEDIA_ACCESS_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteUdmaFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myUdmaRef.collection(USER_DIARY_MEDIA_ACCESS_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }
}

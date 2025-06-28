package com.example.memestorage.test.repo

import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.UserDiaryMediaModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserDiaryMediaRepo {

    companion object {
        private const val USER_DIARY_MEDIA_COLLECTION = "userDiaryMedia"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val myUdmRef = db.collection(USER_COLLECTION).document(myUserId)

    fun addUdmFirebase(model: UserDiaryMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = myUdmRef.collection(USER_DIARY_MEDIA_COLLECTION).document().id
        model.udmId = id
        myUdmRef.collection(USER_DIARY_MEDIA_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUdmFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUdmRef.collection(USER_DIARY_MEDIA_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUdmByUserId(userId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUdmRef.collection(USER_DIARY_MEDIA_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateUdmFirebase(id: String, model: UserDiaryMediaModel, onCompleteListener: OnCompleteListener<Void>) {
        myUdmRef.collection(USER_DIARY_MEDIA_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteUdmFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myUdmRef.collection(USER_DIARY_MEDIA_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }
}

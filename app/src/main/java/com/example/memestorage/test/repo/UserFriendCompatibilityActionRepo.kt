package com.example.memestorage.test.repo

import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.UserFriendCompatibilityActionModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserFriendCompatibilityActionRepo {

    companion object {
        private const val UFCA_COLLECTION = "userFriendCompatibilityAction"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val myUfcaRef = db.collection(USER_COLLECTION).document(myUserId)

    fun addUfcaFirebase(model: UserFriendCompatibilityActionModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = myUfcaRef.collection(UFCA_COLLECTION).document().id
        model.ufcaId = id
        myUfcaRef.collection(UFCA_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfcaFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfcaRef.collection(UFCA_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfcaByByUserId(byUserId: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfcaRef.collection(UFCA_COLLECTION)
            .whereEqualTo("byUserId", byUserId)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateUfcaFirebase(id: String, model: UserFriendCompatibilityActionModel, onCompleteListener: OnCompleteListener<Void>) {
        myUfcaRef.collection(UFCA_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteUfcaFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myUfcaRef.collection(UFCA_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }
}

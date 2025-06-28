package com.example.memestorage.test.repo

import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.UserFriendshipModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserFriendshipRepo {

    companion object {
        private const val USER_FRIENDSHIP_COLLECTION = "userFriendship"
        private const val USER_COLLECTION = "users"
    }

    private val myUserId = FirebaseHelper.getInstance().auth.currentUser!!.uid
    private val db: FirebaseFirestore = FirebaseHelper.getInstance().db
    private val myUfRef = db.collection(USER_COLLECTION).document(myUserId)

    fun addUfFirebase(model: UserFriendshipModel, onCompleteListener: OnCompleteListener<Void>) {
        val id = myUfRef.collection(USER_FRIENDSHIP_COLLECTION).document().id
        model.ufId = id
        myUfRef.collection(USER_FRIENDSHIP_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfRef.collection(USER_FRIENDSHIP_COLLECTION)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfByUser1Id(user1Id: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfRef.collection(USER_FRIENDSHIP_COLLECTION)
            .whereEqualTo("user1Id", user1Id)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUfByUser2Id(user2Id: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        myUfRef.collection(USER_FRIENDSHIP_COLLECTION)
            .whereEqualTo("user2Id", user2Id)
            .get()
            .addOnCompleteListener(onCompleteListener)
    }

    fun updateUfFirebase(id: String, model: UserFriendshipModel, onCompleteListener: OnCompleteListener<Void>) {
        myUfRef.collection(USER_FRIENDSHIP_COLLECTION).document(id)
            .set(model)
            .addOnCompleteListener(onCompleteListener)
    }

    fun deleteUfFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        myUfRef.collection(USER_FRIENDSHIP_COLLECTION).document(id)
            .delete()
            .addOnCompleteListener(onCompleteListener)
    }
}

package com.example.memestorage.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.memestorage.test.model.UserFriendCompatibilityModel
import com.example.memestorage.test.repo.UserFriendCompatibilityRepo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendCompatibilityViewModel(application: Application) : AndroidViewModel(application) {

    private val ufcRepo = UserFriendCompatibilityRepo()
    var ufcList: List<UserFriendCompatibilityModel> = emptyList()

    fun addUfcFirebase(model: UserFriendCompatibilityModel, onCompleteListener: OnCompleteListener<Void>) {
        ufcRepo.addUfcFirebase(model, onCompleteListener)
    }


    fun getUfcFirebase(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufcRepo.getUfcFirebase(onCompleteListener)
    }

    fun findUser2IdInUfcList(user2IdToFind: String): UserFriendCompatibilityModel? {
        return ufcList.find { it.user2Id == user2IdToFind }
    }

    fun findUser1IdInUfcList(user1IdToFind: String): UserFriendCompatibilityModel? {
        return ufcList.find { it.user1Id == user1IdToFind }
    }

    // Check and create or update UserFriendCompatibility
    fun checkAndCreateOrUpdateUserFriendCompatibility(user1Id: String, user2Id: String, points: Int, onCompleteListener: OnCompleteListener<Void>) {
        val existingCompatibility = findUser2IdInUfcList(user2Id)

        if (existingCompatibility != null) {
            // Update existing UserFriendCompatibility
            existingCompatibility.points += points
            ufcRepo.updateUfcFirebase(existingCompatibility.ufcId, existingCompatibility, onCompleteListener)
        } else {
            // Create new UserFriendCompatibility
            val newCompatibility = UserFriendCompatibilityModel(
                ufcId = "", // let Firestore auto-generate ID
                user1Id = user1Id,
                user2Id = user2Id,
                points = points
            )
            addUfcFirebase(newCompatibility, onCompleteListener)
        }
    }
    fun getUfcByUser1Id(user1Id: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufcRepo.getUfcByUser1Id(user1Id, onCompleteListener)
    }

    fun getUfcByUser2Id(user2Id: String, onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        ufcRepo.getUfcByUser2Id(user2Id, onCompleteListener)
    }

    fun updateUfcFirebase(id: String, model: UserFriendCompatibilityModel, onCompleteListener: OnCompleteListener<Void>) {
        ufcRepo.updateUfcFirebase(id, model, onCompleteListener)
    }

    fun deleteUfcFirebase(id: String, onCompleteListener: OnCompleteListener<Void>) {
        ufcRepo.deleteUfcFirebase(id, onCompleteListener)
    }
}

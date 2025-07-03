package com.example.memestorage.test.viewmodel

import android.app.Application
import android.util.Log
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
        Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: Start")
        Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: user1Id = $user1Id")
        Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: user2Id = $user2Id")
        Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: points = $points")
        getUfcByUser1Id(user1Id, OnCompleteListener<QuerySnapshot> {
            Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: getUfcByUser1Id callback triggered")
            val ufcSnapshot = it.result.toObjects(UserFriendCompatibilityModel::class.java)
            Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: ufcSnapshot size = ${ufcSnapshot.size}")
                Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: ufcSnapshot is not empty")
                val existingCompatibility1 = ufcSnapshot.find { it.user2Id == user2Id }
                Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: existingCompatibility1 = $existingCompatibility1")
                if (existingCompatibility1 == null) {
                    Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: existingCompatibility1 is null, checking with user1Id as second user")
                    getUfcByUser1Id(user1Id, OnCompleteListener<QuerySnapshot> {
                        Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: Inner getUfcByUser1Id callback triggered")
                        val ufcSnapshot2 = it.result.toObjects(UserFriendCompatibilityModel::class.java)
                        Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: ufcSnapshot2 size = ${ufcSnapshot2.size}")
                            Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: ufcSnapshot2 is not empty")
                            val existingCompatibility2 = ufcSnapshot2.find { it.user1Id == user1Id }
                            Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: existingCompatibility2 = $existingCompatibility2")
                            if (existingCompatibility2 == null) {
                                Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: existingCompatibility2 is null, creating new compatibility")
                                val newCompatibility = UserFriendCompatibilityModel(
                                    ufcId = "", // let Firestore auto-generate ID
                                    user1Id = user1Id,
                                    user2Id = user2Id,
                                    points = points)
                                Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: newCompatibility = $newCompatibility")
                                addUfcFirebase(newCompatibility, onCompleteListener)
                                Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: addUfcFirebase called for new compatibility")
                            }  else {
                                Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: existingCompatibility2 found, updating points")
                                existingCompatibility2.points += points
                                Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: updated existingCompatibility2.points = ${existingCompatibility2.points}")
                                ufcRepo.updateUfcFirebase(existingCompatibility2.ufcId, existingCompatibility2, onCompleteListener)
                                Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: updateUfcFirebase called for existingCompatibility2")
                            }
                    })
                } else {
                    Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: existingCompatibility1 found, updating points")
                    existingCompatibility1.points += points
                    Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: updated existingCompatibility1.points = ${existingCompatibility1.points}")
                    ufcRepo.updateUfcFirebase(existingCompatibility1.ufcId, existingCompatibility1, onCompleteListener)
                    Log.d("UFC_DEBUG", "checkAndCreateOrUpdateUserFriendCompatibility: updateUfcFirebase called for existingCompatibility1")
                }
           }
        )
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

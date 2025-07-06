package com.example.memestorage.test.repo

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import com.example.memestorage.utils.FirebaseHelper
import com.example.memestorage.test.model.UserDiaryMediaModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.io.File

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

    fun getContentUriFromFile(context: Context, fileUri: Uri): Uri? {
        return try {
            val file = File(fileUri.path!!)
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun uploadVideoCloudinary(fileUri: Uri, applicationContext: Context, uploadCallback: UploadCallback) {
        val contentUri = getContentUriFromFile(applicationContext, fileUri)
        if (contentUri != null) {
            val videoId = System.currentTimeMillis().toString() + myUserId

            val options = mutableMapOf<String, Any>(
                "format" to "mp4",  // Đảm bảo video là mp4
                "resource_type" to "video",  // Đảm bảo video là mp4
                "folder" to "meme_storage/videos",
                "public_id" to videoId
            )

            // Upload video từ content:// Uri
            MediaManager.get().upload(contentUri)
                .unsigned("your_unsigned_preset")  // Đảm bảo preset đúng
                .options(options)
                .callback(uploadCallback)
                .dispatch()
        } else {
            Log.e("TAG", "Failed to get content Uri from file")
        }
    }

}

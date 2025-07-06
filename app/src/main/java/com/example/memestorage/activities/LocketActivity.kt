package com.example.memestorage.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.widget.Toast
import android.widget.VideoView
import com.example.memestorage.R
import com.example.memestorage.databinding.ActivityLocketBinding
import com.example.memestorage.fragmentver2.FriendChatFragment
import com.example.memestorage.test.fragment.UserFriendCompatibilityFragment
import com.example.memestorage.viewmodels.SketchViewModel

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.util.Consumer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.memestorage.adapterver2.ChooseFriendSeeDiaryAdapter
import com.example.memestorage.test.model.UserDiaryMediaAccessModel
import com.example.memestorage.test.model.UserDiaryMediaModel
import com.example.memestorage.test.model.UserFriendDiaryMediaModel
import com.example.memestorage.test.model.UserFriendshipModel
import com.example.memestorage.test.viewmodel.UserDiaryMediaAccessViewModel
import com.example.memestorage.test.viewmodel.UserDiaryMediaViewModel
import com.example.memestorage.test.viewmodel.UserFriendDiaryMediaViewModel
import com.example.memestorage.test.viewmodel.UserFriendshipViewModel
import com.example.memestorage.utils.FileHelper
import com.example.memestorage.utils.FileHelper.savePhotoIntoGallery
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.withContext
import java.io.File
class LocketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocketBinding
    private lateinit var userFriendshipViewModel: UserFriendshipViewModel
    private lateinit var userDiaryMediaViewModel: UserDiaryMediaViewModel
    private lateinit var userFriendDiaryMediaViewModel: UserFriendDiaryMediaViewModel
    private lateinit var userDiaryMediaAccessViewModel: UserDiaryMediaAccessViewModel
    private lateinit var chooseFriendSeeDiaryAdapter: ChooseFriendSeeDiaryAdapter

    private lateinit var sketchViewModel: SketchViewModel
    private var videoCapture: VideoCapture<Recorder>? = null
    private var imageCapture: ImageCapture? = null
    private var recording: Recording? = null
    private var camera: Camera? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> by lazy {
        ProcessCameraProvider.getInstance(this)
    }
    private var path = ""
    private lateinit var selectedFriends: MutableList<String>  // List of selected friends' IDs

    private val recordingListener = Consumer<VideoRecordEvent> { event ->
        when (event) {
            is VideoRecordEvent.Start -> {
                isRecording = true
                binding.buttonRecord?.isVisible = false
                binding?.buttonStopRecord?.isVisible = true
                binding?.textStopRecord?.text =
                    getString(R.string.recording_time_text, 0, 0, 0)
                jobRecordingTime?.cancel()
                recordingTime = 0
                jobRecordingTime = CoroutineScope(Dispatchers.Main).launch {
                    while (true) {
                        delay(1000)
                        recordingTime++
                        val hour = recordingTime / 60 / 60
                        val minute = (recordingTime / 60) % 60
                        val second = (recordingTime) % 60
                        binding?.textStopRecord?.text =
                            getString(R.string.recording_time_text, hour, minute, second)
                    }
                }
                Toast.makeText(
                    application,
                    getString(R.string.recording_text), Toast.LENGTH_SHORT
                )
                    .show()
            }

            is VideoRecordEvent.Finalize -> {
                jobRecordingTime?.cancel()
                try {
                    recording?.close()
                    recording = null
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (event.hasError()) {
                    // update app state when the capture failed.
                    Toast.makeText(
                        application,
                        getString(R.string.recording_text), Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    path = event.outputResults.outputUri.toString()
                    afterRecordStop()
                }
            }
        }
    }

    private var jobRecordingTime: Job? = null
    private var recordingTime = 0
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectedFriends = mutableListOf()

        binding.btnGoToTest.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }
        binding.btnOpenChat.setOnClickListener {
            showImageCategoryFragment()
        }
        binding.btnSeeDiary.setOnClickListener {

        }
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        setContentView(binding.root)
        initViewModel()
        initView()
        initListener()
        getFriendships()

    }

    private fun initView() {
        initVideoView()
        startCamera()
    }

    private fun initViewModel() {
        sketchViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)
            .create(SketchViewModel::class.java)
        userFriendshipViewModel = ViewModelProvider(this).get(UserFriendshipViewModel::class.java)
        userDiaryMediaViewModel = ViewModelProvider(this).get(UserDiaryMediaViewModel::class.java)
        userFriendDiaryMediaViewModel = ViewModelProvider(this).get(UserFriendDiaryMediaViewModel::class.java)
        userDiaryMediaAccessViewModel = ViewModelProvider(this).get(UserDiaryMediaAccessViewModel::class.java)
    }

    private fun getFriendships() {
        // Get all friendships and display in adapter (For demo purposes, assuming the userId is already set)
        val userId = "YOUR_USER_ID_HERE" // Replace with your actual userId
        userFriendshipViewModel.getUfByUser1Id(userId, OnCompleteListener { task ->
            if (task.isSuccessful) {
                val friendships = mutableListOf<UserFriendshipModel>()
                task.result?.forEach { document ->
                    val friendship = document.toObject(UserFriendshipModel::class.java)
                    friendships.add(friendship)
                }

                // Setup adapter
                chooseFriendSeeDiaryAdapter = ChooseFriendSeeDiaryAdapter(friendships) { friend, isSelected ->
                    // Handle selection
                    handleFriendSelection(friend, isSelected)
                }

                binding.rvFriend.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.rvFriend.adapter = chooseFriendSeeDiaryAdapter
            }
        })
    }


    private fun uploadDiaryCloudinary() {
        Log.d("TAG", "uploadDiaryCloudinary: ${Uri.parse(path)} va $path")
        userDiaryMediaViewModel.uploadImageCloudinary(Uri.parse(path), applicationContext, object : UploadCallback {
            override fun onStart(requestId: String) {
                Log.d("TAG", "Upload started for request: $requestId")
            }

            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                Log.d("TAG", "Upload progress for request: $requestId - % ($bytes/$totalBytes bytes)")
            }

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                val mediaURL = resultData["secure_url"] as? String
                uploadDiary(mediaURL!!) // Assuming uploadDiary takes the media URL as a parameter
                Log.d("TAG", "Upload successful. video URL: $mediaURL")
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                Log.e("TAG", "Upload failed for request: $requestId with error: ${error.description}")
            }

            override fun onReschedule(requestId: String, error: ErrorInfo) {
                Log.w("TAG", "Upload rescheduled for request: $requestId due to error: ${error.description}")
            }
        })
    }
    private fun uploadDiary(mediaURL: String) {

    // Assuming diary content is provided
        val diaryContent = "This is my new diary entry!" // Cập nhật với nội dung thực tế từ người dùng
        val mediaURL = mediaURL  // Cập nhật URL media thực tế nếu có
        val userId = "currentUserId" // Cập nhật với ID người dùng thực tế
        val caption = "My first diary entry!" // Cập nhật với chú thích từ người dùng
        val time = System.currentTimeMillis().toInt() // Lấy thời gian hiện tại (thời gian Unix)

        // Tạo đối tượng UserDiaryMediaModel
        val userDiaryMedia = UserDiaryMediaModel(
            udmId = "uniqueId", // ID có thể tạo ra tự động hoặc lấy từ Firestore
            mediaURL = mediaURL,
            userId = userId,
            caption = caption,
            time = time
        )

    // Upload to UserDiaryMediaViewModel
        userDiaryMediaViewModel.addUdmFirebase(userDiaryMedia, OnCompleteListener { task ->
            if (task.isSuccessful) {
                // Upload to UserFriendDiaryMediaViewModel for selected friends
                selectedFriends.forEach { friendId ->
                    val userFriendDiaryMedia = UserFriendDiaryMediaModel(
                        ufdmId = "",  // Generate a unique ID
                        userId = userId,
                        time = System.currentTimeMillis().toString(),  // Assume you get this from somewhere
                        userDiaryMediaId = userDiaryMedia.udmId
                    )

                    userFriendDiaryMediaViewModel.addUfdmByFriendshipId(userFriendDiaryMedia, friendId, OnCompleteListener { uploadTask ->
                        if (uploadTask.isSuccessful) {
                            // Upload access information
                            val userDiaryMediaAccess = UserDiaryMediaAccessModel(
                                udmaId = "uniqueAccessId",  // Generate a unique ID
                                userDiaryMediaId = userDiaryMedia.udmId,
                                userFriendId = friendId
                            )

                            userDiaryMediaAccessViewModel.addUdmaFirebase(userDiaryMediaAccess, OnCompleteListener {
                                Toast.makeText(this, "Diary uploaded successfully!", Toast.LENGTH_SHORT).show()
                            })
                        }
                    })
                }
            }
        })
    }


    private fun handleFriendSelection(friend: UserFriendshipModel, isSelected: Boolean) {
        // Add/remove selected friends to/from the list for uploading Diary
        if (isSelected) {
            // Add to selected list (this will be used for upload)
        } else {
            // Remove from selected list
        }
    }

    private fun initVideoView() {
        val videoView = binding.video
        val layoutParams = videoView.layoutParams

// Set a 16:9 aspect ratio (height = width / 16 * 9)
        val screenWidth = resources.displayMetrics.widthPixels
        val aspectRatio = 4f / 12f
        val videoHeight = (screenWidth / aspectRatio).toInt()

        layoutParams.width = screenWidth
        layoutParams.height = videoHeight

// Apply the updated layout params
        videoView.layoutParams = layoutParams

    }

    private fun initListener() {
        startCamera()

        binding.buttonFlash.setOnClickListener {
            sketchViewModel.toggleFlash()
        }

        binding.buttonRecord.setOnClickListener {
            this.let {
                try {
                    val videoFile =
                        FileHelper.getVideoFile(
                            it,
                            System.currentTimeMillis().toString()
                        )
                    recording = videoCapture?.output?.prepareRecording(
                        it, FileOutputOptions.Builder(videoFile)
                            .build()
                    )?.start(ContextCompat.getMainExecutor(it), recordingListener)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        binding.buttonStopRecord.setOnClickListener {
            try {
                recording?.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.btUploadDiary.setOnClickListener {
            uploadDiaryCloudinary()
        }
    }

    private fun afterRecordStop() {
        binding.video.setVideoURI(path.toUri())
        binding.video.setOnCompletionListener { it.start() }
        binding.video.setOnClickListener {
            if (binding.video.isPlaying) {
                binding.video.pause()
            } else {
                binding.video.start()
            }
        }
        if (binding.video.isVisible) {
            binding.video.isVisible = false
            binding.cameraView.isVisible = true
        } else {
            binding.video.isVisible = true
            binding.cameraView.isVisible = false
        }
        binding.video.start()
    }

    private fun startCamera(isBackCam: Boolean = true) {
        this.let {
            Log.d("SketchCam", "startCamera() CALLED")
            try {
                Log.d("SketchCam", "Adding listener to cameraProviderFuture...")
                cameraProviderFuture.addListener({
                    Log.d("SketchCam", "cameraProviderFuture listener triggered!")

                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                    Log.d("SketchCam", "cameraProvider.get() OK")

                    val preview = Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3).build().also {
                        Log.d("SketchCam", "Setting surface provider...")
                        val surfaceProvider = binding.cameraView?.surfaceProvider
                        if (surfaceProvider == null) {
                            Log.e("SketchCam", "surfaceProvider is NULL!")
                            return@addListener
                        }
                        binding.cameraView?.post {
                            it.setSurfaceProvider(binding.cameraView?.surfaceProvider)
                        }
                        Log.d("SketchCam", "Surface provider set.")
                    }

                    cameraSelector = if (isBackCam) {
                        Log.d("SketchCam", "Using BACK CAMERA")
                        CameraSelector.DEFAULT_BACK_CAMERA
                    } else {
                        Log.d("SketchCam", "Using FRONT CAMERA")
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    }

                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build()
                    Log.d("SketchCam", "imageCapture BUILT.")

                    val recorder = Recorder.Builder().build()
                    Log.d("SketchCam", "Recorder BUILT.")

                    videoCapture = VideoCapture.withOutput(recorder)
                    videoCapture?.targetRotation =
                        binding.cameraView?.display?.rotation ?: Surface.ROTATION_0
                    Log.d("SketchCam", "videoCapture BUILT, targetRotation set.")

                    try {
                        Log.d("SketchCam", "Unbinding all use cases...")
                        cameraProvider.unbindAll()

                        Log.d("SketchCam", "Binding use cases to camera...")
                        camera = cameraProvider.bindToLifecycle(
                            this@LocketActivity,
                            cameraSelector,
                            preview,
                            imageCapture,
                            videoCapture
                        )
                        Log.d("SketchCam", "Camera BOUND TO LIFECYCLE SUCCESSFULLY!")

                    } catch (exc: Exception) {
                        Log.e("SketchCam", "Exception during bindToLifecycle: ${exc.message}")
                        exc.printStackTrace()
                    }

                }, ContextCompat.getMainExecutor(it))
            } catch (ex: Exception) {
                Log.e("SketchCam", "Exception outside listener: ${ex.message}")
                ex.printStackTrace()
                Toast.makeText(
                    it,
                    "Camera is not available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveFile(filePath: String) {
        CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() }).launch {
            File(filePath).savePhotoIntoGallery(application)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@LocketActivity,
                    getString(R.string.image_saved_text), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun showImageCategoryFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer2, FriendChatFragment())
            .addToBackStack(null)
            .commit()
    }
}
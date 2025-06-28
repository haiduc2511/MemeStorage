package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.databinding.FragmentMediaLikeBinding
import com.example.memestorage.test.model.MediaLikeModel
import com.example.memestorage.test.viewmodel.MediaLikeViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class MediaLikeFragment : Fragment() {

    private var _binding: FragmentMediaLikeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mediaLikeViewModel: MediaLikeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaLikeBinding.inflate(inflater, container, false)
        initViewModel()
        initUI()
        return binding.root
    }

    private fun initViewModel() {
        mediaLikeViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(MediaLikeViewModel::class.java)
    }

    private fun initUI() {
        binding.btnAdd.setOnClickListener { addMediaLike() }
        binding.btnUpdate.setOnClickListener { updateMediaLike() }
        binding.btnDelete.setOnClickListener { deleteMediaLike() }
        binding.btnRead.setOnClickListener { readMediaLikes() }
    }

    private fun addMediaLike() {
        val userId = binding.edtUserId.text.toString().trim()
        val mediaId = binding.edtMediaId.text.toString().trim()

        if (userId.isEmpty() || mediaId.isEmpty()) {
            Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val model = MediaLikeModel(
            mlId = "",
            userId = userId,
            mediaId = mediaId
        )

        mediaLikeViewModel.addMediaLikeFirebase(model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Like Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMediaLike() {
        val mlId = binding.edtUserId.text.toString().trim() // Tạm lấy mlId từ edtUserId để demo
        val userId = binding.edtUserId.text.toString().trim()
        val mediaId = binding.edtMediaId.text.toString().trim()

        if (mlId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide mlId to update", Toast.LENGTH_SHORT).show()
            return
        }

        val model = MediaLikeModel(
            mlId = mlId,
            userId = userId,
            mediaId = mediaId
        )

        mediaLikeViewModel.updateMediaLikeFirebase(mlId, model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Like Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteMediaLike() {
        val mlId = binding.edtUserId.text.toString().trim()
        if (mlId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide mlId to delete", Toast.LENGTH_SHORT).show()
            return
        }

        mediaLikeViewModel.deleteMediaLikeFirebase(mlId) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Like Deleted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readMediaLikes() {
        mediaLikeViewModel.getMediaLikesFirebase(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val result = StringBuilder()
                for (doc in task.result!!) {
                    val item = doc.toObject(MediaLikeModel::class.java)
                    result.append("mlId: ${item.mlId}, userId: ${item.userId}, mediaId: ${item.mediaId}\n")
                }
                binding.tvResult.text = result.toString()
            } else {
                Toast.makeText(requireContext(), "Failed to read", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

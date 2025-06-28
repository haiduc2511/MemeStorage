package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.databinding.FragmentMediaCommentBinding
import com.example.memestorage.test.model.MediaCommentModel
import com.example.memestorage.test.viewmodel.MediaCommentViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class MediaCommentFragment : Fragment() {

    private var _binding: FragmentMediaCommentBinding? = null
    private val binding get() = _binding!!

    private lateinit var mediaCommentViewModel: MediaCommentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaCommentBinding.inflate(inflater, container, false)
        initViewModel()
        initUI()
        return binding.root
    }

    private fun initViewModel() {
        mediaCommentViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(MediaCommentViewModel::class.java)
    }

    private fun initUI() {
        binding.btnAdd.setOnClickListener { addMediaComment() }
        binding.btnUpdate.setOnClickListener { updateMediaComment() }
        binding.btnDelete.setOnClickListener { deleteMediaComment() }
        binding.btnRead.setOnClickListener { readMediaComments() }
    }

    private fun addMediaComment() {
        val comment = binding.edtComment.text.toString().trim()
        val userId = binding.edtUserId.text.toString().trim()
        val mediaId = binding.edtMediaId.text.toString().trim()

        if (comment.isEmpty() || userId.isEmpty() || mediaId.isEmpty()) {
            Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val model = MediaCommentModel(
            mcId = "",
            comment = comment,
            userId = userId,
            mediaId = mediaId
        )

        mediaCommentViewModel.addMediaCommentFirebase(model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Comment Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMediaComment() {
        val mcId = binding.edtUserId.text.toString().trim() // Tạm: lấy mcId từ edtUserId
        val comment = binding.edtComment.text.toString().trim()
        val userId = binding.edtUserId.text.toString().trim()
        val mediaId = binding.edtMediaId.text.toString().trim()

        if (mcId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide mcId to update", Toast.LENGTH_SHORT).show()
            return
        }

        val model = MediaCommentModel(
            mcId = mcId,
            comment = comment,
            userId = userId,
            mediaId = mediaId
        )

        mediaCommentViewModel.updateMediaCommentFirebase(mcId, model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Comment Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteMediaComment() {
        val mcId = binding.edtUserId.text.toString().trim()
        if (mcId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide mcId to delete", Toast.LENGTH_SHORT).show()
            return
        }

        mediaCommentViewModel.deleteMediaCommentFirebase(mcId) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Comment Deleted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readMediaComments() {
        mediaCommentViewModel.getMediaCommentsFirebase(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val result = StringBuilder()
                for (doc in task.result!!) {
                    val item = doc.toObject(MediaCommentModel::class.java)
                    result.append("mcId: ${item.mcId}, comment: ${item.comment}, userId: ${item.userId}, mediaId: ${item.mediaId}\n")
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

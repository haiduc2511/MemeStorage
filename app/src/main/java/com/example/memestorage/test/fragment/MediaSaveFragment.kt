package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.databinding.FragmentMediaSaveBinding
import com.example.memestorage.test.model.MediaSaveModel
import com.example.memestorage.test.viewmodel.MediaSaveViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class MediaSaveFragment : Fragment() {

    private var _binding: FragmentMediaSaveBinding? = null
    private val binding get() = _binding!!

    private lateinit var mediaSaveViewModel: MediaSaveViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaSaveBinding.inflate(inflater, container, false)
        initViewModel()
        initUI()
        return binding.root
    }

    private fun initViewModel() {
        mediaSaveViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(MediaSaveViewModel::class.java)
    }

    private fun initUI() {
        binding.btnAdd.setOnClickListener { addMediaSave() }
        binding.btnUpdate.setOnClickListener { updateMediaSave() }
        binding.btnDelete.setOnClickListener { deleteMediaSave() }
        binding.btnRead.setOnClickListener { readMediaSaves() }
    }

    private fun addMediaSave() {
        val userId = binding.edtUserId.text.toString().trim()
        val mediaId = binding.edtMediaId.text.toString().trim()

        if (userId.isEmpty() || mediaId.isEmpty()) {
            Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val model = MediaSaveModel(
            msId = "",
            userId = userId,
            mediaId = mediaId
        )

        mediaSaveViewModel.addMediaSaveFirebase(model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Save Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMediaSave() {
        val msId = binding.edtUserId.text.toString().trim() // demo lấy msId từ userId input
        val userId = binding.edtUserId.text.toString().trim()
        val mediaId = binding.edtMediaId.text.toString().trim()

        if (msId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide msId to update", Toast.LENGTH_SHORT).show()
            return
        }

        val model = MediaSaveModel(
            msId = msId,
            userId = userId,
            mediaId = mediaId
        )

        mediaSaveViewModel.updateMediaSaveFirebase(msId, model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Save Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteMediaSave() {
        val msId = binding.edtUserId.text.toString().trim()
        if (msId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide msId to delete", Toast.LENGTH_SHORT).show()
            return
        }

        mediaSaveViewModel.deleteMediaSaveFirebase(msId) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Save Deleted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readMediaSaves() {
        mediaSaveViewModel.getMediaSavesFirebase(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val result = StringBuilder()
                for (doc in task.result!!) {
                    val item = doc.toObject(MediaSaveModel::class.java)
                    result.append("msId: ${item.msId}, userId: ${item.userId}, mediaId: ${item.mediaId}\n")
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

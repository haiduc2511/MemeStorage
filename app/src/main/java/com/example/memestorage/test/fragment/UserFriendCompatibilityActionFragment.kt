package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.databinding.FragmentUserFriendCompatibilityActionBinding
import com.example.memestorage.test.model.UserFriendCompatibilityActionModel
import com.example.memestorage.test.viewmodel.UserFriendCompatibilityActionViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendCompatibilityActionFragment : Fragment() {

    private var _binding: FragmentUserFriendCompatibilityActionBinding? = null
    private val binding get() = _binding!!

    private lateinit var ufcaViewModel: UserFriendCompatibilityActionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserFriendCompatibilityActionBinding.inflate(inflater, container, false)
        initViewModel()
        initUI()
        return binding.root
    }

    private fun initViewModel() {
        ufcaViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserFriendCompatibilityActionViewModel::class.java)
    }

    private fun initUI() {
        binding.btnAdd.setOnClickListener { addUfca() }
        binding.btnUpdate.setOnClickListener { updateUfca() }
        binding.btnDelete.setOnClickListener { deleteUfca() }
        binding.btnRead.setOnClickListener { readUfca() }
    }

    private fun addUfca() {
        val mediaId = binding.edtMediaId.text.toString().trim()
        val action = binding.edtAction.text.toString().trim()
        val byUserId = binding.edtByUserId.text.toString().trim()

        if (byUserId.isEmpty()) {
            Toast.makeText(requireContext(), "By User ID is required", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserFriendCompatibilityActionModel(
            ufcaId = "",
            mediaId = mediaId,
            action = action,
            byUserId = byUserId
        )

        ufcaViewModel.addUfcaFirebase(model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "UFCA Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUfca() {
        val ufcaId = binding.edtByUserId.text.toString().trim() // Tạm nhập ufcaId từ byUserId input
        val mediaId = binding.edtMediaId.text.toString().trim()
        val action = binding.edtAction.text.toString().trim()
        val byUserId = binding.edtByUserId.text.toString().trim()

        if (ufcaId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide ufcaId to update", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserFriendCompatibilityActionModel(
            ufcaId = ufcaId,
            mediaId = mediaId,
            action = action,
            byUserId = byUserId
        )

        ufcaViewModel.updateUfcaFirebase(ufcaId, model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "UFCA Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUfca() {
        val ufcaId = binding.edtByUserId.text.toString().trim()
        if (ufcaId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide ufcaId to delete", Toast.LENGTH_SHORT).show()
            return
        }

        ufcaViewModel.deleteUfcaFirebase(ufcaId) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "UFCA Deleted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readUfca() {
        ufcaViewModel.getUfcaFirebase(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val result = StringBuilder()
                for (doc in task.result!!) {
                    val item = doc.toObject(UserFriendCompatibilityActionModel::class.java)
                    result.append("ufcaId: ${item.ufcaId}, mediaId: ${item.mediaId}, action: ${item.action}, byUserId: ${item.byUserId}\n")
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

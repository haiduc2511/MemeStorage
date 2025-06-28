package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.databinding.FragmentUserFriendCompatibilityBinding
import com.example.memestorage.test.model.UserFriendCompatibilityModel
import com.example.memestorage.test.viewmodel.UserFriendCompatibilityViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendCompatibilityFragment : Fragment() {

    private var _binding: FragmentUserFriendCompatibilityBinding? = null
    private val binding get() = _binding!!

    private lateinit var ufcViewModel: UserFriendCompatibilityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserFriendCompatibilityBinding.inflate(inflater, container, false)
        initViewModel()
        initUI()
        return binding.root
    }

    private fun initViewModel() {
        ufcViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserFriendCompatibilityViewModel::class.java)
    }

    private fun initUI() {
        binding.btnAdd.setOnClickListener { addUfc() }
        binding.btnUpdate.setOnClickListener { updateUfc() }
        binding.btnDelete.setOnClickListener { deleteUfc() }
        binding.btnRead.setOnClickListener { readUfc() }
    }

    private fun addUfc() {
        val user1Id = binding.edtUser1Id.text.toString().trim()
        val user2Id = binding.edtUser2Id.text.toString().trim()
        val pointsStr = binding.edtPoints.text.toString().trim()

        if (user1Id.isEmpty() || user2Id.isEmpty() || pointsStr.isEmpty()) {
            Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val points = pointsStr.toIntOrNull()
        if (points == null) {
            Toast.makeText(requireContext(), "Points must be Int", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserFriendCompatibilityModel(
            ufcId = "",
            user1Id = user1Id,
            user2Id = user2Id,
            points = points
        )

        ufcViewModel.addUfcFirebase(model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "UFC Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUfc() {
        val ufcId = binding.edtUser1Id.text.toString().trim() // tạm nhập ufcId từ user1Id input
        val user1Id = binding.edtUser1Id.text.toString().trim()
        val user2Id = binding.edtUser2Id.text.toString().trim()
        val pointsStr = binding.edtPoints.text.toString().trim()

        if (ufcId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide ufcId to update", Toast.LENGTH_SHORT).show()
            return
        }

        val points = pointsStr.toIntOrNull()
        if (points == null) {
            Toast.makeText(requireContext(), "Points must be Int", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserFriendCompatibilityModel(
            ufcId = ufcId,
            user1Id = user1Id,
            user2Id = user2Id,
            points = points
        )

        ufcViewModel.updateUfcFirebase(ufcId, model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "UFC Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUfc() {
        val ufcId = binding.edtUser1Id.text.toString().trim()
        if (ufcId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide ufcId to delete", Toast.LENGTH_SHORT).show()
            return
        }

        ufcViewModel.deleteUfcFirebase(ufcId) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "UFC Deleted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readUfc() {
        ufcViewModel.getUfcFirebase(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val result = StringBuilder()
                for (doc in task.result!!) {
                    val item = doc.toObject(UserFriendCompatibilityModel::class.java)
                    result.append("ufcId: ${item.ufcId}, user1Id: ${item.user1Id}, user2Id: ${item.user2Id}, points: ${item.points}\n")
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

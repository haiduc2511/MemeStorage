package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.databinding.FragmentUserFriendshipBinding
import com.example.memestorage.test.model.UserFriendshipModel
import com.example.memestorage.test.viewmodel.UserFriendshipViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendshipFragment : Fragment() {

    private var _binding: FragmentUserFriendshipBinding? = null
    private val binding get() = _binding!!

    private lateinit var ufViewModel: UserFriendshipViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserFriendshipBinding.inflate(inflater, container, false)
        initViewModel()
        initUI()
        return binding.root
    }

    private fun initViewModel() {
        ufViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserFriendshipViewModel::class.java)
    }

    private fun initUI() {
        binding.btnAdd.setOnClickListener { addUf() }
        binding.btnUpdate.setOnClickListener { updateUf() }
        binding.btnDelete.setOnClickListener { deleteUf() }
        binding.btnRead.setOnClickListener { readUf() }
    }

    private fun addUf() {
        val user1Id = binding.edtUser1Id.text.toString().trim()
        val user2Id = binding.edtUser2Id.text.toString().trim()

        if (user1Id.isEmpty() || user2Id.isEmpty()) {
            Toast.makeText(requireContext(), "User IDs cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserFriendshipModel(
            ufId = "",
            user1Id = user1Id,
            user2Id = user2Id
        )

        ufViewModel.addUfFirebase(model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Friendship Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUf() {
        val ufId = binding.edtUser1Id.text.toString().trim() // Tạm nhập ufId từ user1Id
        val user1Id = binding.edtUser1Id.text.toString().trim()
        val user2Id = binding.edtUser2Id.text.toString().trim()

        if (ufId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide ufId to update", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserFriendshipModel(
            ufId = ufId,
            user1Id = user1Id,
            user2Id = user2Id
        )

        ufViewModel.updateUfFirebase(ufId, model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Friendship Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUf() {
        val ufId = binding.edtUser1Id.text.toString().trim()
        if (ufId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide ufId to delete", Toast.LENGTH_SHORT).show()
            return
        }

        ufViewModel.deleteUfFirebase(ufId) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Friendship Deleted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readUf() {
        ufViewModel.getUfFirebase(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val result = StringBuilder()
                for (doc in task.result!!) {
                    val item = doc.toObject(UserFriendshipModel::class.java)
                    result.append("ufId: ${item.ufId}, user1Id: ${item.user1Id}, user2Id: ${item.user2Id}\n")
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

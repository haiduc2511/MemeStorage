package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.databinding.FragmentUserDiaryMediaAccessBinding
import com.example.memestorage.test.model.UserDiaryMediaAccessModel
import com.example.memestorage.test.viewmodel.UserDiaryMediaAccessViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserDiaryMediaAccessFragment : Fragment() {

    private var _binding: FragmentUserDiaryMediaAccessBinding? = null
    private val binding get() = _binding!!

    private lateinit var udmaViewModel: UserDiaryMediaAccessViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDiaryMediaAccessBinding.inflate(inflater, container, false)
        initViewModel()
        initUI()
        return binding.root
    }

    private fun initViewModel() {
        udmaViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserDiaryMediaAccessViewModel::class.java)
    }

    private fun initUI() {
        binding.btnAdd.setOnClickListener { addUdma() }
        binding.btnUpdate.setOnClickListener { updateUdma() }
        binding.btnDelete.setOnClickListener { deleteUdma() }
        binding.btnRead.setOnClickListener { readUdma() }
    }

    private fun addUdma() {
        val userDiaryMediaId = binding.edtUserDiaryMediaId.text.toString().trim()
        val userFriendId = binding.edtUserFriendId.text.toString().trim()

        if (userFriendId.isEmpty()) {
            Toast.makeText(requireContext(), "User Friend ID is required", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserDiaryMediaAccessModel(
            udmaId = "",
            userDiaryMediaId = userDiaryMediaId,
            userFriendId = userFriendId
        )

        udmaViewModel.addUdmaFirebase(model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Diary Media Access Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUdma() {
        val udmaId = binding.edtUserFriendId.text.toString().trim() // Tạm nhập udmaId từ userFriendId input
        val userDiaryMediaId = binding.edtUserDiaryMediaId.text.toString().trim()
        val userFriendId = binding.edtUserFriendId.text.toString().trim()

        if (udmaId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide udmaId to update", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserDiaryMediaAccessModel(
            udmaId = udmaId,
            userDiaryMediaId = userDiaryMediaId,
            userFriendId = userFriendId
        )

        udmaViewModel.updateUdmaFirebase(udmaId, model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Diary Media Access Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUdma() {
        val udmaId = binding.edtUserFriendId.text.toString().trim()
        if (udmaId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide udmaId to delete", Toast.LENGTH_SHORT).show()
            return
        }

        udmaViewModel.deleteUdmaFirebase(udmaId) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Diary Media Access Deleted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readUdma() {
        udmaViewModel.getUdmaFirebase(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val result = StringBuilder()
                for (doc in task.result!!) {
                    val item = doc.toObject(UserDiaryMediaAccessModel::class.java)
                    result.append("udmaId: ${item.udmaId}, userDiaryMediaId: ${item.userDiaryMediaId}, userFriendId: ${item.userFriendId}\n")
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

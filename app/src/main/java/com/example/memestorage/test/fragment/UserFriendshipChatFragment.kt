package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.databinding.FragmentUserFriendshipChatBinding
import com.example.memestorage.test.model.UserFriendshipChatModel
import com.example.memestorage.test.viewmodel.UserFriendshipChatViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendshipChatFragment : Fragment() {

    private var _binding: FragmentUserFriendshipChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var ufcViewModel: UserFriendshipChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserFriendshipChatBinding.inflate(inflater, container, false)
        initViewModel()
        initUI()
        return binding.root
    }

    private fun initViewModel() {
        ufcViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserFriendshipChatViewModel::class.java)
    }

    private fun initUI() {
        binding.btnAdd.setOnClickListener { addUfc() }
        binding.btnUpdate.setOnClickListener { updateUfc() }
        binding.btnDelete.setOnClickListener { deleteUfc() }
        binding.btnRead.setOnClickListener { readUfc() }
    }

    private fun addUfc() {
        val userFriendshipId = binding.edtUserFriendshipId.text.toString().trim()
        val streakStr = binding.edtStreak.text.toString().trim()
        val theme = binding.edtTheme.text.toString().trim()

        val streak = streakStr.toIntOrNull()
        if (streakStr.isNotEmpty() && streak == null) {
            Toast.makeText(requireContext(), "Streak must be Int", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserFriendshipChatModel(
            ufcId = "",
            userFriendshipId = userFriendshipId,
            streak = streak,
            theme = theme
        )

        ufcViewModel.addUfcFirebase(model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Friendship Chat Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUfc() {
        val ufcId = binding.edtUserFriendshipId.text.toString().trim() // Tạm nhập ufcId từ friendshipId
        val userFriendshipId = binding.edtUserFriendshipId.text.toString().trim()
        val streakStr = binding.edtStreak.text.toString().trim()
        val theme = binding.edtTheme.text.toString().trim()

        val streak = streakStr.toIntOrNull()
        if (streakStr.isNotEmpty() && streak == null) {
            Toast.makeText(requireContext(), "Streak must be Int", Toast.LENGTH_SHORT).show()
            return
        }

        if (ufcId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide ufcId to update", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserFriendshipChatModel(
            ufcId = ufcId,
            userFriendshipId = userFriendshipId,
            streak = streak,
            theme = theme
        )

        ufcViewModel.updateUfcFirebase(ufcId, model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Friendship Chat Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUfc() {
        val ufcId = binding.edtUserFriendshipId.text.toString().trim()
        if (ufcId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide ufcId to delete", Toast.LENGTH_SHORT).show()
            return
        }

        ufcViewModel.deleteUfcFirebase(ufcId) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Friendship Chat Deleted!", Toast.LENGTH_SHORT).show()
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
                    val item = doc.toObject(UserFriendshipChatModel::class.java)
                    result.append("ufcId: ${item.ufcId}, userFriendshipId: ${item.userFriendshipId}, streak: ${item.streak}, theme: ${item.theme}\n")
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

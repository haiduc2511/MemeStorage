package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.databinding.FragmentUserFriendshipMessageBinding
import com.example.memestorage.test.model.UserFriendshipMessageModel
import com.example.memestorage.test.viewmodel.UserFriendshipMessageViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserFriendshipMessageFragment : Fragment() {

    private var _binding: FragmentUserFriendshipMessageBinding? = null
    private val binding get() = _binding!!

    private lateinit var ufmViewModel: UserFriendshipMessageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserFriendshipMessageBinding.inflate(inflater, container, false)
        initViewModel()
        initUI()
        return binding.root
    }

    private fun initViewModel() {
        ufmViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserFriendshipMessageViewModel::class.java)
    }

    private fun initUI() {
        binding.btnAdd.setOnClickListener { addUfm() }
        binding.btnUpdate.setOnClickListener { updateUfm() }
        binding.btnDelete.setOnClickListener { deleteUfm() }
        binding.btnRead.setOnClickListener { readUfm() }
    }

    private fun addUfm() {
        val sender = binding.edtSender.text.toString().trim()
        val message = binding.edtMessage.text.toString().trim()
        val timeStr = binding.edtTime.text.toString().trim()
        val isReplyStr = binding.edtIsReplyFromDiary.text.toString().trim()
        val userFriendshipChatId = binding.edtUserFriendshipChatId.text.toString().trim()

        val time = timeStr.toIntOrNull()
        val isReply = isReplyStr.toBooleanStrictOrNull()

        if (timeStr.isNotEmpty() && time == null) {
            Toast.makeText(requireContext(), "Time must be Int", Toast.LENGTH_SHORT).show()
            return
        }

        if (isReplyStr.isNotEmpty() && isReply == null) {
            Toast.makeText(requireContext(), "isReplyFromDiary must be true/false", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserFriendshipMessageModel(
            ufmId = "",
            sender = sender,
            message = message,
            time = time,
            isReplyFromDiary = isReply,
            userFriendshipChatId = userFriendshipChatId
        )

        ufmViewModel.addUfmFirebase(model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Message Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUfm() {
        val ufmId = binding.edtSender.text.toString().trim() // Tạm nhập ufmId từ sender cho demo
        val sender = binding.edtSender.text.toString().trim()
        val message = binding.edtMessage.text.toString().trim()
        val timeStr = binding.edtTime.text.toString().trim()
        val isReplyStr = binding.edtIsReplyFromDiary.text.toString().trim()
        val userFriendshipChatId = binding.edtUserFriendshipChatId.text.toString().trim()

        val time = timeStr.toIntOrNull()
        val isReply = isReplyStr.toBooleanStrictOrNull()

        if (ufmId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide ufmId to update", Toast.LENGTH_SHORT).show()
            return
        }

        if (timeStr.isNotEmpty() && time == null) {
            Toast.makeText(requireContext(), "Time must be Int", Toast.LENGTH_SHORT).show()
            return
        }

        if (isReplyStr.isNotEmpty() && isReply == null) {
            Toast.makeText(requireContext(), "isReplyFromDiary must be true/false", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserFriendshipMessageModel(
            ufmId = ufmId,
            sender = sender,
            message = message,
            time = time,
            isReplyFromDiary = isReply,
            userFriendshipChatId = userFriendshipChatId
        )

        ufmViewModel.updateUfmFirebase(ufmId, model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Message Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUfm() {
        val ufmId = binding.edtSender.text.toString().trim()
        if (ufmId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide ufmId to delete", Toast.LENGTH_SHORT).show()
            return
        }

        ufmViewModel.deleteUfmFirebase(ufmId) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Message Deleted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readUfm() {
        ufmViewModel.getUfmFirebase(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val result = StringBuilder()
                for (doc in task.result!!) {
                    val item = doc.toObject(UserFriendshipMessageModel::class.java)
                    result.append("ufmId: ${item.ufmId}, sender: ${item.sender}, message: ${item.message}, time: ${item.time}, isReplyFromDiary: ${item.isReplyFromDiary}, chatId: ${item.userFriendshipChatId}\n")
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

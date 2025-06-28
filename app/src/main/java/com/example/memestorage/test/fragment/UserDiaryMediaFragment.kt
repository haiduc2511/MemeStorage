package com.example.memestorage.test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memestorage.databinding.FragmentUserDiaryMediaBinding
import com.example.memestorage.test.model.UserDiaryMediaModel
import com.example.memestorage.test.viewmodel.UserDiaryMediaViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot

class UserDiaryMediaFragment : Fragment() {

    private var _binding: FragmentUserDiaryMediaBinding? = null
    private val binding get() = _binding!!

    private lateinit var udmViewModel: UserDiaryMediaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDiaryMediaBinding.inflate(inflater, container, false)
        initViewModel()
        initUI()
        return binding.root
    }

    private fun initViewModel() {
        udmViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserDiaryMediaViewModel::class.java)
    }

    private fun initUI() {
        binding.btnAdd.setOnClickListener { addUdm() }
        binding.btnUpdate.setOnClickListener { updateUdm() }
        binding.btnDelete.setOnClickListener { deleteUdm() }
        binding.btnRead.setOnClickListener { readUdm() }
    }

    private fun addUdm() {
        val mediaURL = binding.edtMediaURL.text.toString().trim()
        val userId = binding.edtUserId.text.toString().trim()
        val caption = binding.edtCaption.text.toString().trim()
        val timeStr = binding.edtTime.text.toString().trim()

        if (userId.isEmpty()) {
            Toast.makeText(requireContext(), "User ID cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val time = timeStr.toIntOrNull()
        if (time == null) {
            Toast.makeText(requireContext(), "Time must be Int", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserDiaryMediaModel(
            udmId = "",
            mediaURL = mediaURL,
            userId = userId,
            caption = caption,
            time = time
        )

        udmViewModel.addUdmFirebase(model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Diary Media Added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUdm() {
        val udmId = binding.edtUserId.text.toString().trim() // Tạm nhập udmId từ userId
        val mediaURL = binding.edtMediaURL.text.toString().trim()
        val userId = binding.edtUserId.text.toString().trim()
        val caption = binding.edtCaption.text.toString().trim()
        val timeStr = binding.edtTime.text.toString().trim()

        if (udmId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide udmId to update", Toast.LENGTH_SHORT).show()
            return
        }

        val time = timeStr.toIntOrNull()
        if (time == null) {
            Toast.makeText(requireContext(), "Time must be Int", Toast.LENGTH_SHORT).show()
            return
        }

        val model = UserDiaryMediaModel(
            udmId = udmId,
            mediaURL = mediaURL,
            userId = userId,
            caption = caption,
            time = time
        )

        udmViewModel.updateUdmFirebase(udmId, model) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Diary Media Updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUdm() {
        val udmId = binding.edtUserId.text.toString().trim()
        if (udmId.isEmpty()) {
            Toast.makeText(requireContext(), "Provide udmId to delete", Toast.LENGTH_SHORT).show()
            return
        }

        udmViewModel.deleteUdmFirebase(udmId) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Diary Media Deleted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readUdm() {
        udmViewModel.getUdmFirebase(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                val result = StringBuilder()
                for (doc in task.result!!) {
                    val item = doc.toObject(UserDiaryMediaModel::class.java)
                    result.append("udmId: ${item.udmId}, mediaURL: ${item.mediaURL}, userId: ${item.userId}, caption: ${item.caption}, time: ${item.time}\n")
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
